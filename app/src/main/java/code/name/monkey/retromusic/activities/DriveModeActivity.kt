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
package code.name.monkey.retromusic.activities

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import code.name.monkey.retromusic.activities.base.AbsMusicServiceActivity
import code.name.monkey.retromusic.databinding.ActivityDriveModeBinding
import code.name.monkey.retromusic.extensions.accentColor
import code.name.monkey.retromusic.extensions.drawAboveSystemBars
import code.name.monkey.retromusic.helper.MusicProgressViewUpdateHelper
import code.name.monkey.retromusic.helper.MusicProgressViewUpdateHelper.Callback
import code.name.monkey.retromusic.repository.Repository
import code.name.monkey.retromusic.util.MusicUtil
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


/**
 * Created by hemanths on 2020-02-02.
 */

class DriveModeActivity : AbsMusicServiceActivity(), Callback {

    private lateinit var binding: ActivityDriveModeBinding
    private var lastPlaybackControlsColor: Int = Color.GRAY
    private var lastDisabledPlaybackControlsColor: Int = Color.GRAY
    private lateinit var progressViewUpdateHelper: MusicProgressViewUpdateHelper
    private val repository: Repository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriveModeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpMusicControllers()

        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this)
        lastPlaybackControlsColor = accentColor()
        binding.close.setOnClickListener {
            onBackPressed()
        }
        binding.repeatButton.drawAboveSystemBars()
    }

    private fun setUpMusicControllers() {
        setUpPlayPauseFab()
        setUpPrevNext()
        setUpRepeatButton()
        setUpShuffleButton()
        setUpProgressSlider()
        setupFavouriteToggle()
    }

    private fun setupFavouriteToggle() {
        binding.songFavourite.setOnClickListener {
//            toggleFavorite(MusicPlayerRemote.currentSongId ?: return@setOnClickListener)
        }
    }

    private fun toggleFavorite(songId: String) {
// TODO: fix favorite
        lifecycleScope.launch(Dispatchers.IO) {
//            val playlist = repository.favoritePlaylist()
//            val songEntity = songId.toSongEntity(playlist.playListId)
//            val isFavorite = repository.isSongFavorite(songId.id)
//            if (isFavorite) {
//                repository.removeSongFromPlaylist(songEntity)
//            } else {
//                repository.insertSongs(listOf(songId.toSongEntity(playlist.playListId)))
//            }
//            sendBroadcast(Intent(MusicService.FAVORITE_STATE_CHANGED))
        }
    }

    private fun updateFavorite() {
        lifecycleScope.launch(Dispatchers.IO) {
            // TODO: fix favorite
//            val isFavorite: Boolean =
//                repository.isSongFavorite(MusicPlayerRemote.currentSongId)
//            withContext(Dispatchers.Main) {
//                binding.songFavourite.setImageResource(if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border)
//            }
        }
    }

    private fun setUpProgressSlider() {
        binding.progressSlider.addOnChangeListener { _: Slider, progress: Float, fromUser: Boolean ->
//            if (fromUser) {
//                MusicPlayerRemote.seekTo(progress.toInt())
//                onUpdateProgressViews(
//                    MusicPlayerRemote.songProgressMillis,
//                    MusicPlayerRemote.songDurationMillis
//                )
//            }
        }
    }

    override fun onPause() {
        super.onPause()
        progressViewUpdateHelper.stop()
    }

    override fun onResume() {
        super.onResume()
        progressViewUpdateHelper.start()
    }

    private fun setUpPrevNext() {
//        binding.nextButton.setOnClickListener { MusicPlayerRemote.playNextSong() }
//        binding.previousButton.setOnClickListener { MusicPlayerRemote.back() }
    }

    private fun setUpShuffleButton() {
//        binding.shuffleButton.setOnClickListener { MusicPlayerRemote.toggleShuffleMode() }
    }

    private fun setUpRepeatButton() {
//        binding.repeatButton.setOnClickListener { MusicPlayerRemote.cycleRepeatMode() }
    }

    private fun setUpPlayPauseFab() {
//        binding.playPauseButton.setOnClickListener(PlayPauseButtonOnClickHandler())
    }

    private fun updatePlayPauseDrawableState() {
//        if (MusicPlayerRemote.isPlaying) {
//            binding.playPauseButton.setImageResource(R.drawable.ic_pause)
//        } else {
//            binding.playPauseButton.setImageResource(R.drawable.ic_play_arrow)
//        }
    }

    fun updateShuffleState() {
//        when (MusicPlayerRemote.shuffleMode) {
//            MusicService.SHUFFLE_MODE_SHUFFLE -> binding.shuffleButton.setColorFilter(
//                lastPlaybackControlsColor,
//                PorterDuff.Mode.SRC_IN
//            )
//            else -> binding.shuffleButton.setColorFilter(
//                lastDisabledPlaybackControlsColor,
//                PorterDuff.Mode.SRC_IN
//            )
//        }
    }

    private fun updateRepeatState() {
//        when (MusicPlayerRemote.repeatMode) {
//            MusicService.REPEAT_MODE_NONE -> {
//                binding.repeatButton.setImageResource(R.drawable.ic_repeat)
//                binding.repeatButton.setColorFilter(
//                    lastDisabledPlaybackControlsColor,
//                    PorterDuff.Mode.SRC_IN
//                )
//            }
//            MusicService.REPEAT_MODE_ALL -> {
//                binding.repeatButton.setImageResource(R.drawable.ic_repeat)
//                binding.repeatButton.setColorFilter(
//                    lastPlaybackControlsColor,
//                    PorterDuff.Mode.SRC_IN
//                )
//            }
//            MusicService.REPEAT_MODE_THIS -> {
//                binding.repeatButton.setImageResource(R.drawable.ic_repeat_one)
//                binding.repeatButton.setColorFilter(
//                    lastPlaybackControlsColor,
//                    PorterDuff.Mode.SRC_IN
//                )
//            }
//        }
    }

    private fun updateSong() {
        // TODO: Update song
//        val song = MusicPlayerRemote.currentSongId
//
//        binding.songTitle.text = song.title
//        binding.songText.text = song.artistName
//
//        GlideApp.with(this)
//            .load(RetroGlideExtension.getSongModel(song))
//            .songCoverOptions(song)
//            .transform(BlurTransformation.Builder(this).build())
//            .into(binding.image)
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        binding.progressSlider.run {
            valueTo = total.toFloat()
            value = progress.toFloat().coerceIn(valueFrom, valueTo)
        }

        binding.songTotalTime.text = MusicUtil.getReadableDurationString(total.toLong())
        binding.songCurrentProgress.text = MusicUtil.getReadableDurationString(progress.toLong())
    }
}
