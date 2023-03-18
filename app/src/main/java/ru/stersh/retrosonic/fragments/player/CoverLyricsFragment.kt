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
package ru.stersh.retrosonic.fragments.player

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.SHOW_LYRICS
import ru.stersh.retrosonic.databinding.FragmentCoverLyricsBinding
import ru.stersh.retrosonic.extensions.dipToPix
import ru.stersh.retrosonic.fragments.base.AbsPlayerFragment
import ru.stersh.retrosonic.fragments.base.goToLyrics
import ru.stersh.retrosonic.helper.MusicProgressViewUpdateHelper
import ru.stersh.retrosonic.model.lyrics.AbsSynchronizedLyrics
import ru.stersh.retrosonic.model.lyrics.Lyrics
import ru.stersh.retrosonic.util.PreferenceUtil
import ru.stersh.retrosonic.util.color.MediaNotificationProcessor

class CoverLyricsFragment :
    Fragment(R.layout.fragment_cover_lyrics),
    MusicProgressViewUpdateHelper.Callback,
    SharedPreferences.OnSharedPreferenceChangeListener {
    private var progressViewUpdateHelper: MusicProgressViewUpdateHelper? = null
    private var _binding: FragmentCoverLyricsBinding? = null
    private val binding get() = _binding!!

    private val lyricsLayout: FrameLayout get() = binding.playerLyrics
    private val lyricsLine1: TextView get() = binding.playerLyricsLine1
    private val lyricsLine2: TextView get() = binding.playerLyricsLine2

    private var lyrics: Lyrics? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCoverLyricsBinding.bind(view)
        progressViewUpdateHelper = MusicProgressViewUpdateHelper(this, 500, 1000)
        if (PreferenceUtil.showLyrics) {
            progressViewUpdateHelper?.start()
        }
        // Remove background on Fit theme
        binding.playerLyricsLine2.setOnClickListener {
            goToLyrics(requireActivity())
        }
    }

    fun setColors(color: MediaNotificationProcessor) {
        binding.run {
            playerLyrics.background = null
            playerLyricsLine1.setTextColor(color.primaryTextColor)
            playerLyricsLine1.setShadowLayer(dipToPix(10f), 0f, 0f, color.backgroundColor)
            playerLyricsLine2.setTextColor(color.primaryTextColor)
            playerLyricsLine2.setShadowLayer(dipToPix(10f), 0f, 0f, color.backgroundColor)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == SHOW_LYRICS) {
            if (sharedPreferences?.getBoolean(key, false) == true) {
                progressViewUpdateHelper?.start()
                binding.root.isVisible = true
                updateLyrics()
            } else {
                progressViewUpdateHelper?.stop()
                binding.root.isVisible = false
            }
        }
    }

    private fun updateLyrics() {
        lyrics = null
        // TODO: update lyrics
//        lifecycleScope.launch(Dispatchers.IO) {
//            val song = MusicPlayerRemote.currentSongId
//            lyrics = try {
//                val lrcFile: File? = LyricUtil.getSyncedLyricsFile(song)
//                val data: String = LyricUtil.getStringFromLrc(lrcFile)
//                Lyrics.parse(song,
//                    data.ifEmpty {
//                        // Get Embedded Lyrics
//                        LyricUtil.getEmbeddedSyncedLyrics(song.data)
//                    }
//                )
//            } catch (err: FileNotFoundException) {
//                null
//            } catch (e: CannotReadException) {
//                null
//            }
//        }
    }

    override fun onUpdateProgressViews(progress: Int, total: Int) {
        if (_binding == null) return

        if (!isLyricsLayoutVisible()) {
            hideLyricsLayout()
            return
        }

        if (lyrics !is AbsSynchronizedLyrics) return
        val synchronizedLyrics = lyrics as AbsSynchronizedLyrics

        lyricsLayout.isVisible = true
        lyricsLayout.alpha = 1f

        val oldLine = lyricsLine2.text.toString()
        val line = synchronizedLyrics.getLine(progress)

        if (oldLine != line || oldLine.isEmpty()) {
            lyricsLine1.text = oldLine
            lyricsLine2.text = line

            lyricsLine1.isVisible = true
            lyricsLine2.isVisible = true

            lyricsLine2.measure(
                View.MeasureSpec.makeMeasureSpec(
                    lyricsLine2.measuredWidth,
                    View.MeasureSpec.EXACTLY,
                ),
                View.MeasureSpec.UNSPECIFIED,
            )
            val h: Float = lyricsLine2.measuredHeight.toFloat()

            lyricsLine1.alpha = 1f
            lyricsLine1.translationY = 0f
            lyricsLine1.animate().alpha(0f).translationY(-h).duration =
                AbsPlayerFragment.VISIBILITY_ANIM_DURATION

            lyricsLine2.alpha = 0f
            lyricsLine2.translationY = h
            lyricsLine2.animate().alpha(1f).translationY(0f).duration =
                AbsPlayerFragment.VISIBILITY_ANIM_DURATION
        }
    }

    private fun isLyricsLayoutVisible(): Boolean {
        return lyrics != null && lyrics!!.isSynchronized && lyrics!!.isValid
    }

    private fun hideLyricsLayout() {
        lyricsLayout.animate().alpha(0f).setDuration(AbsPlayerFragment.VISIBILITY_ANIM_DURATION)
            .withEndAction {
                if (_binding == null) return@withEndAction
                lyricsLayout.isVisible = false
                lyricsLine1.text = null
                lyricsLine2.text = null
            }
    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(this)
        progressViewUpdateHelper?.stop()
        _binding = null
    }
}
