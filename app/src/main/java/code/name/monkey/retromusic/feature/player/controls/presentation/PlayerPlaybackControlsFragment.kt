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
package code.name.monkey.retromusic.feature.player.controls.presentation

import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import code.name.monkey.appthemehelper.util.ATHUtil
import code.name.monkey.appthemehelper.util.ColorUtil
import code.name.monkey.appthemehelper.util.MaterialValueHelper
import code.name.monkey.appthemehelper.util.TintHelper
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.databinding.FragmentPlayerPlaybackControlsBinding
import code.name.monkey.retromusic.extensions.accentColor
import code.name.monkey.retromusic.extensions.applyColor
import code.name.monkey.retromusic.extensions.ripAlpha
import code.name.monkey.retromusic.fragments.base.AbsPlayerControlsFragment
import code.name.monkey.retromusic.fragments.base.goToAlbum
import code.name.monkey.retromusic.fragments.base.goToArtist
import code.name.monkey.retromusic.util.PreferenceUtil
import code.name.monkey.retromusic.util.color.MediaNotificationProcessor
import com.google.android.material.slider.Slider
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerPlaybackControlsFragment : AbsPlayerControlsFragment(R.layout.fragment_player_playback_controls) {

    private val viewModel: PlayerPlaybackControlsViewModel by viewModel()

    private var _binding: FragmentPlayerPlaybackControlsBinding? = null
    private val binding
        get() = _binding!!

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
        _binding = FragmentPlayerPlaybackControlsBinding.bind(view)
        setupPlayerProgress()
        setupPlayPauseButton()
        setupSongInfo()
        setupSlider()
        setupNextPreviousButtons()
    }

    private fun setupSlider() {
        binding.progressSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.seek(value.toLong())
            }
        }
    }

    private fun setupNextPreviousButtons() {
        binding.previousButton.setOnClickListener {
            viewModel.previous()
            it.showBounceAnimation()
        }
        binding.nextButton.setOnClickListener {
            viewModel.next()
            it.showBounceAnimation()
        }
    }

    private fun setupPlayerProgress() {
        lifecycleScope.launchWhenStarted {
            viewModel.progress.collect { progress ->
                binding.songTotalTime.text = progress.total
                binding.songCurrentProgress.text = progress.current
                setSeekbarInfo(progress)
            }
        }
    }

    private fun setupSongInfo() {
        lifecycleScope.launchWhenStarted {
            viewModel.songInfo.collect { songInfo ->
                binding.title.text = songInfo.title
                binding.text.text = songInfo.artist
            }
        }
        binding.title.isSelected = true
        binding.text.isSelected = true
        binding.title.setOnClickListener {
            goToAlbum(requireActivity())
        }
        binding.text.setOnClickListener {
            goToArtist(requireActivity())
        }
    }

    private fun setSeekbarInfo(progress: PlayerProgressUi) {
        val totalTime = progress.totalMs
        val currentTime = progress.currentMs

        if (totalTime > 0) {
            binding.progressSlider.valueFrom = 0f
            binding.progressSlider.valueTo = totalTime.toFloat()
            binding.progressSlider.value = currentTime.toFloat()
        }
    }

    private fun setupPlayPauseButton() {
        var isPlaying = false
        lifecycleScope.launchWhenStarted {
            viewModel.isPlaying.collect {
                isPlaying = it
                if (isPlaying) {
                    binding.playPauseButton.setImageResource(R.drawable.ic_pause)
                } else {
                    binding.playPauseButton.setImageResource(R.drawable.ic_play_arrow)
                }
            }
        }
        binding.playPauseButton.setOnClickListener {
            if (isPlaying) {
                viewModel.pause()
            } else {
                viewModel.play()
            }
            it.showBounceAnimation()
        }
    }

    override fun setColor(color: MediaNotificationProcessor) {
        val colorBg = ATHUtil.resolveColor(requireContext(), android.R.attr.colorBackground)
        if (ColorUtil.isColorLight(colorBg)) {
            lastPlaybackControlsColor =
                MaterialValueHelper.getSecondaryTextColor(requireContext(), true)
            lastDisabledPlaybackControlsColor =
                MaterialValueHelper.getSecondaryDisabledTextColor(requireContext(), true)
        } else {
            lastPlaybackControlsColor =
                MaterialValueHelper.getPrimaryTextColor(requireContext(), false)
            lastDisabledPlaybackControlsColor =
                MaterialValueHelper.getPrimaryDisabledTextColor(requireContext(), false)
        }

        val colorFinal = if (PreferenceUtil.isAdaptiveColor) {
            color.primaryTextColor
        } else {
            accentColor()
        }.ripAlpha()

        TintHelper.setTintAuto(
            binding.playPauseButton,
            MaterialValueHelper.getPrimaryTextColor(
                requireContext(),
                ColorUtil.isColorLight(colorFinal)
            ),
            false
        )
        TintHelper.setTintAuto(binding.playPauseButton, colorFinal, true)
        binding.progressSlider.applyColor(colorFinal)
        volumeFragment?.setTintable(colorFinal)
        updateRepeatState()
        updateShuffleState()
        updatePrevNextColor()
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
