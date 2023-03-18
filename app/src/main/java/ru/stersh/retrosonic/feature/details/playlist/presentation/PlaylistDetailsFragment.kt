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
package ru.stersh.retrosonic.feature.details.playlist.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.databinding.FragmentPlaylistDetailBinding
import ru.stersh.retrosonic.extensions.surfaceColor
import ru.stersh.retrosonic.fragments.base.AbsMainActivityFragment
import ru.stersh.retrosonic.glide.GlideApp
import ru.stersh.retrosonic.util.MusicUtil
import ru.stersh.retrosonic.util.ThemedFastScroller

class PlaylistDetailsFragment : AbsMainActivityFragment(R.layout.fragment_playlist_detail) {
    private val arguments by navArgs<PlaylistDetailsFragmentArgs>()
    private val viewModel by viewModel<PlaylistDetailsViewModel> {
        parametersOf(arguments.extraPlaylistId)
    }

    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var playlistSongAdapter: PlaylistDetailsSongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment_container
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(surfaceColor())
            setPathMotion(MaterialArcMotion())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlaylistDetailBinding.bind(view)
        mainActivity.setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.playlistCoverContainer.transitionName = arguments.extraPlaylistId
        binding.toolbar.title = " "
        setUpRecyclerView()
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.appBarLayout.statusBarForeground = MaterialShapeDrawable.createWithElevationOverlay(requireContext())

        lifecycleScope.launchWhenStarted {
            viewModel.playlistDetails.collect {
                view.doOnPreDraw {
                    startPostponedEnterTransition()
                }
                binding.playlistTitle.text = it.title
                binding.playlistInfo.text = MusicUtil.getPlaylistInfoString(requireContext(), it.songCount, it.duration)
                GlideApp
                    .with(requireActivity())
                    .load(it.coverArtUrl)
                    .into(binding.image)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.playlistSongs.collect {
                playlistSongAdapter.swapDataSet(it)
            }
        }

        binding.fragmentPlaylistContent.playAction.setOnClickListener {
            viewModel.playAll()
        }

        binding.fragmentPlaylistContent.shuffleAction.setOnClickListener {
            viewModel.playShuffled()
        }
    }

    private fun setUpRecyclerView() {
        playlistSongAdapter = PlaylistDetailsSongAdapter(requireActivity()) { songId ->
            viewModel.playSong(songId)
        }

        binding.fragmentPlaylistContent.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            binding.fragmentPlaylistContent.recyclerView.adapter = playlistSongAdapter
            ThemedFastScroller.create(this)
        }
    }

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_playlist_detail, menu)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        return true // PlaylistMenuHelper.handleMenuClick(requireActivity(), playlist, item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
