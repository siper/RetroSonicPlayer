/*
 * Copyright (c) 2020 Hemanth Savarla.
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
package code.name.monkey.retromusic.fragments.player.adaptive

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import code.name.monkey.appthemehelper.util.ToolbarContentTintHelper
import code.name.monkey.retromusic.R
import ru.stersh.retrosonic.core.storage.domain.PlayQueueStorage
import code.name.monkey.retromusic.databinding.FragmentAdaptivePlayerBinding
import code.name.monkey.retromusic.extensions.*
import code.name.monkey.retromusic.fragments.base.AbsPlayerFragment
import code.name.monkey.retromusic.fragments.player.PlayerAlbumCoverFragment
import code.name.monkey.retromusic.helper.MusicPlayerRemote
import code.name.monkey.retromusic.util.color.MediaNotificationProcessor
import kotlinx.coroutines.flow.filterNotNull
import org.koin.android.ext.android.inject

class AdaptiveFragment : AbsPlayerFragment(R.layout.fragment_adaptive_player) {

    private val playQueueStorage: PlayQueueStorage by inject()
    private var _binding: FragmentAdaptivePlayerBinding? = null
    private val binding get() = _binding!!
    override fun playerToolbar(): Toolbar {
        return binding.playerToolbar
    }

    private var lastColor: Int = 0
    private lateinit var playbackControlsFragment: AdaptivePlaybackControlsFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdaptivePlayerBinding.bind(view)
        setUpSubFragments()
        setUpPlayerToolbar()
        binding.playbackControlsFragment.drawAboveSystemBars()
        lifecycleScope.launchWhenStarted {
            playQueueStorage
                .getCurrentSong()
                .filterNotNull()
                .collect { song ->
                    binding.playerToolbar.apply {
                        title = song.title
                        subtitle = song.artist
                    }
                }
        }
    }

    private fun setUpSubFragments() {
        playbackControlsFragment =
            whichFragment(R.id.playbackControlsFragment) as AdaptivePlaybackControlsFragment
        val playerAlbumCoverFragment =
            whichFragment(R.id.playerAlbumCoverFragment) as PlayerAlbumCoverFragment
        playerAlbumCoverFragment.apply {
            removeSlideEffect()
            setCallbacks(this@AdaptiveFragment)
        }
    }

    private fun setUpPlayerToolbar() {
        binding.playerToolbar.apply {
            inflateMenu(R.menu.menu_player)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
            ToolbarContentTintHelper.colorizeToolbar(this, surfaceColor(), requireActivity())
            setTitleTextColor(textColorPrimary())
            setSubtitleTextColor(textColorSecondary())
            setOnMenuItemClickListener(this@AdaptiveFragment)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        updateIsFavorite()
        updateSong()
    }

    override fun onPlayingMetaChanged() {
        updateIsFavorite()
        updateSong()
    }

    private fun updateSong() {
        val song = MusicPlayerRemote.currentSongId
//        binding.playerToolbar.apply {
//            title = song.title
//            subtitle = song.artistName
//        }
    }

    override fun toggleFavorite(songId: String) {
        super.toggleFavorite(songId)
        if (songId == MusicPlayerRemote.currentSongId) {
            updateIsFavorite()
        }
    }

    override fun onFavoriteToggled() {
//        toggleFavorite(MusicPlayerRemote.currentSongId)
    }

    override fun onColorChanged(color: MediaNotificationProcessor) {
        playbackControlsFragment.setColor(color)
        lastColor = color.primaryTextColor
        libraryViewModel.updateColor(color.primaryTextColor)
        ToolbarContentTintHelper.colorizeToolbar(
            binding.playerToolbar,
            colorControlNormal(),
            requireActivity()
        )
    }

    override fun onShow() {
    }

    override fun onHide() {
        onBackPressed()
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun toolbarIconColor(): Int {
        return colorControlNormal()
    }

    override val paletteColor: Int
        get() = lastColor
}
