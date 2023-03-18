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
package ru.stersh.retrosonic.fragments.player.lockscreen

import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.TextView
import code.name.monkey.appthemehelper.util.ATHUtil
import code.name.monkey.appthemehelper.util.ColorUtil
import code.name.monkey.appthemehelper.util.MaterialValueHelper
import code.name.monkey.appthemehelper.util.TintHelper
import com.google.android.material.slider.Slider
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.databinding.FragmentLockScreenPlaybackControlsBinding
import ru.stersh.retrosonic.extensions.applyColor
import ru.stersh.retrosonic.extensions.ripAlpha
import ru.stersh.retrosonic.extensions.textColorSecondary
import ru.stersh.retrosonic.fragments.base.AbsPlayerControlsFragment
import ru.stersh.retrosonic.helper.PlayPauseButtonOnClickHandler
import ru.stersh.retrosonic.util.PreferenceUtil
import ru.stersh.retrosonic.util.color.MediaNotificationProcessor

class LockScreenControlsFragment :
    AbsPlayerControlsFragment(R.layout.fragment_lock_screen_playback_controls) {

    private var _binding: FragmentLockScreenPlaybackControlsBinding? = null
    private val binding get() = _binding!!

    override val progressSlider: Slider
        get() = binding.progressSlider

    override val shuffleButton: ImageButton
        get() = binding.shuffleButton

    override val repeatButton: ImageButton
        get() = binding.repeatButton

    override val nextButton: ImageButton
        get() = binding.nextButton

    override val previousButton: ImageButton
        get() = binding.previousButton

    override val songTotalTime: TextView
        get() = binding.songTotalTime

    override val songCurrentProgress: TextView
        get() = binding.songCurrentProgress

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLockScreenPlaybackControlsBinding.bind(view)
        setUpPlayPauseFab()
        binding.title.isSelected = true
    }

    private fun updateSong() {
//        val song = MusicPlayerRemote.currentSongId
//        binding.title.text = song.title
//        binding.text.text = String.format("%s - %s", song.artistName, song.albumName)
    }

    override fun setColor(color: MediaNotificationProcessor) {
        val context = context ?: return

        val colorBg = ATHUtil.resolveColor(context, android.R.attr.colorBackground)
        if (ColorUtil.isColorLight(colorBg)) {
            lastPlaybackControlsColor =
                MaterialValueHelper.getSecondaryTextColor(context, true)
            lastDisabledPlaybackControlsColor =
                MaterialValueHelper.getSecondaryDisabledTextColor(context, true)
        } else {
            lastPlaybackControlsColor =
                MaterialValueHelper.getPrimaryTextColor(context, false)
            lastDisabledPlaybackControlsColor =
                MaterialValueHelper.getPrimaryDisabledTextColor(context, false)
        }

        val colorFinal = if (PreferenceUtil.isAdaptiveColor) {
            color.primaryTextColor
        } else {
            textColorSecondary()
        }.ripAlpha()

        volumeFragment?.setTintable(colorFinal)
        binding.progressSlider.applyColor(colorFinal)

        updateRepeatState()
        updateShuffleState()
        updatePrevNextColor()

        val isDark = ColorUtil.isColorLight(colorFinal)
        binding.text.setTextColor(colorFinal)

        TintHelper.setTintAuto(
            binding.playPauseButton,
            MaterialValueHelper.getPrimaryTextColor(context, isDark),
            false,
        )
        TintHelper.setTintAuto(binding.playPauseButton, colorFinal, true)
    }

    private fun setUpPlayPauseFab() {
        binding.playPauseButton.setOnClickListener(PlayPauseButtonOnClickHandler())
    }

    private fun updatePlayPauseDrawableState() {
//        if (MusicPlayerRemote.isPlaying) {
//            binding.playPauseButton.setImageResource(R.drawable.ic_pause)
//        } else {
//            binding.playPauseButton.setImageResource(R.drawable.ic_play_arrow_white_32dp)
//        }
    }

    public override fun show() {
        binding.playPauseButton.animate()
            .scaleX(1f)
            .scaleY(1f)
            .rotation(360f)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    public override fun hide() {
        binding.playPauseButton.apply {
            scaleX = 0f
            scaleY = 0f
            rotation = 0f
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
