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
package ru.stersh.retrosonic.feature.player.mini

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.databinding.FragmentMiniPlayerBinding
import ru.stersh.retrosonic.extensions.accentColor
import ru.stersh.retrosonic.extensions.show
import ru.stersh.retrosonic.extensions.textColorPrimary
import ru.stersh.retrosonic.extensions.textColorSecondary
import ru.stersh.retrosonic.glide.GlideApp
import ru.stersh.retrosonic.glide.RetroGlideExtension
import ru.stersh.retrosonic.util.PreferenceUtil
import ru.stersh.retrosonic.util.RetroUtil
import kotlin.math.abs

open class MiniPlayerFragment : Fragment(R.layout.fragment_mini_player) {

    private var _binding: FragmentMiniPlayerBinding? = null
    private val binding: FragmentMiniPlayerBinding
        get() = requireNotNull(_binding)

    private val viewModel: MiniPlayerViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMiniPlayerBinding.bind(view)
        view.setOnTouchListener(FlingPlayBackController(requireContext()))
        setUpButtons()
        setupSongInfo()
        setupProgress()
        setupPlayPause()
    }

    private fun setupPlayPause() {
        lifecycleScope.launchWhenStarted {
            viewModel.isPlaying.collect { isPlaying ->
                if (isPlaying) {
                    binding.miniPlayerPlayPauseButton.setImageResource(R.drawable.ic_pause)
                } else {
                    binding.miniPlayerPlayPauseButton.setImageResource(R.drawable.ic_play_arrow)
                }
                binding.miniPlayerPlayPauseButton.setOnClickListener {
                    if (isPlaying) {
                        viewModel.pause()
                    } else {
                        viewModel.play()
                    }
                }
            }
        }
    }

    private fun setupProgress() = lifecycleScope.launchWhenStarted {
        binding.progressBar.accentColor()
        viewModel.progress.collect { progress ->
            binding.progressBar.max = progress.total.toInt()
            binding.progressBar.progress = progress.current.toInt()
        }
    }

    private fun setupSongInfo() = lifecycleScope.launchWhenStarted {
        viewModel.info.collect { info ->
            val builder = SpannableStringBuilder()

            val title = info.title?.toSpannable()
            title?.setSpan(ForegroundColorSpan(textColorPrimary()), 0, title.length, 0)

            val text = info.artist?.toSpannable()
            text?.setSpan(ForegroundColorSpan(textColorSecondary()), 0, text.length, 0)

            builder.append(title).append(" • ").append(text)

            binding.miniPlayerTitle.isSelected = true
            binding.miniPlayerTitle.text = builder

            GlideApp
                .with(requireContext())
                .load(info.coverArtUrl)
                .transition(RetroGlideExtension.getDefaultTransition())
                .into(binding.image)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setUpButtons() {
        if (RetroUtil.isTablet) {
            binding.actionNext.show()
            binding.actionPrevious.show()
        } else {
            binding.actionNext.isVisible = PreferenceUtil.isExtraControls
            binding.actionPrevious.isVisible = PreferenceUtil.isExtraControls
        }
        binding.actionNext.setOnClickListener {
            viewModel.next()
        }
        binding.actionPrevious.setOnClickListener {
            viewModel.previous()
        }
    }

    inner class FlingPlayBackController(context: Context) : View.OnTouchListener {

        private var flingPlayBackController = GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float,
                ): Boolean {
                    if (abs(velocityX) > abs(velocityY)) {
                        if (velocityX < 0) {
                            viewModel.next()
                            return true
                        } else if (velocityX > 0) {
                            viewModel.previous()
                            return true
                        }
                    }
                    return false
                }
            },
        )

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            return flingPlayBackController.onTouchEvent(event)
        }
    }
}
