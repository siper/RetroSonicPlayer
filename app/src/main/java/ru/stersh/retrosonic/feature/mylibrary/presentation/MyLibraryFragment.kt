/*
 * Copyright (c) 2020 Retro Sonic contributors.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package ru.stersh.retrosonic.feature.mylibrary.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.core.text.parseAsHtml
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import code.name.monkey.appthemehelper.common.ATHToolbarActivity
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.stersh.retrosonic.EXTRA_ALBUM_ID
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.databinding.FragmentHomeBinding
import ru.stersh.retrosonic.dialogs.CreatePlaylistDialog
import ru.stersh.retrosonic.dialogs.ImportPlaylistDialog
import ru.stersh.retrosonic.extensions.accentColor
import ru.stersh.retrosonic.extensions.dip
import ru.stersh.retrosonic.extensions.drawNextToNavbar
import ru.stersh.retrosonic.extensions.findActivityNavController
import ru.stersh.retrosonic.extensions.setUpMediaRouteButton
import ru.stersh.retrosonic.fragments.ReloadType
import ru.stersh.retrosonic.fragments.base.AbsMainActivityFragment
import ru.stersh.retrosonic.glide.GlideApp
import ru.stersh.retrosonic.glide.RetroGlideExtension
import ru.stersh.retrosonic.interfaces.IScrollHelper
import ru.stersh.retrosonic.util.defaultNavOptions

class MyLibraryFragment : AbsMainActivityFragment(R.layout.fragment_home), IScrollHelper {

    private val viewModel: MyLibraryViewModel by viewModel()

    private var _binding: MyLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeBinding = FragmentHomeBinding.bind(view)
        _binding = MyLibraryBinding(homeBinding)
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.title = null

        enterTransition = MaterialFadeThrough().addTarget(binding.contentContainer)
        reenterTransition = MaterialFadeThrough().addTarget(binding.contentContainer)

        checkForMargins()

        val myLibraryAdapter = ListDelegationAdapter(
            albumDelegate { _, item ->
                findActivityNavController(R.id.fragment_container)
                    .navigate(
                        R.id.albumDetailsFragment,
                        bundleOf(EXTRA_ALBUM_ID to item.id)
                    )
            },
            sectionDelegate()
        )
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3).apply {
                spanSizeLookup = object : SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (myLibraryAdapter.items?.get(position) is SectionUi) {
                            3
                        } else {
                            1
                        }
                    }
                }
            }
            adapter = myLibraryAdapter
        }
        lifecycleScope.launchWhenStarted {
            viewModel.items.collect {
                myLibraryAdapter.items = it
                myLibraryAdapter.notifyDataSetChanged()
            }
        }

        setupUser()
        setupTitle()
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.appBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(requireContext())
        binding.toolbar.drawNextToNavbar()
    }

    private fun setupTitle() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_search, null, defaultNavOptions)
        }
        val hexColor = String.format("#%06X", 0xFFFFFF and accentColor())
        val appName = "Retro <span  style='color:$hexColor';>Sonic</span>".parseAsHtml()
        binding.appNameText.text = appName
    }

    private fun setupUser() {
        lifecycleScope.launchWhenStarted {
            viewModel
                .username
                .collect {
                    binding.titleWelcome.text = String.format("%s", it)
                }
        }
        lifecycleScope.launchWhenStarted {
            viewModel
                .avatarUrl
                .collect {
                    GlideApp
                        .with(requireActivity())
                        .load(it)
                        .userProfileOptions(RetroGlideExtension.getUserModel(), requireContext())
                        .into(binding.userImage)
                }
        }
    }

    private fun checkForMargins() {
        if (mainActivity.isBottomNavVisible) {
            binding.recyclerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = dip(R.dimen.bottom_nav_height)
            }
        }
    }

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        menu.removeItem(R.id.action_grid_size)
        menu.removeItem(R.id.action_layout_type)
        menu.removeItem(R.id.action_sort_order)
        menu.findItem(R.id.action_settings).setShowAsAction(SHOW_AS_ACTION_IF_ROOM)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireContext(),
            binding.toolbar,
            menu,
            ATHToolbarActivity.getToolbarBackgroundColor(binding.toolbar),
        )
        // Setting up cast button
        requireContext().setUpMediaRouteButton(menu)
    }

    override fun scrollToTop() {
        binding.container.scrollTo(0, 0)
        binding.appBarLayout.setExpanded(true)
    }

    private fun setSharedAxisYTransitions() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
            .addTarget(CoordinatorLayout::class.java)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> findNavController().navigate(
                R.id.settings_fragment,
                null,
                defaultNavOptions,
            )
            R.id.action_import_playlist -> ImportPlaylistDialog().show(
                childFragmentManager,
                "ImportPlaylist",
            )
            R.id.action_add_to_playlist -> CreatePlaylistDialog.create(emptyList()).show(
                childFragmentManager,
                "ShowCreatePlaylistDialog",
            )
        }
        return false
    }

    override fun onPrepareMenu(menu: Menu) {
        super.onPrepareMenu(menu)
        ToolbarContentTintHelper.handleOnPrepareOptionsMenu(requireActivity(), binding.toolbar)
    }

    override fun onResume() {
        super.onResume()
        checkForMargins()
        libraryViewModel.forceReload(ReloadType.HomeSections)
        exitTransition = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        const val TAG: String = "BannerHomeFragment"

        @JvmStatic
        fun newInstance(): MyLibraryFragment {
            return MyLibraryFragment()
        }
    }
}
