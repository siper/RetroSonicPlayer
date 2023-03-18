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
package ru.stersh.retrosonic.activities

import android.app.KeyguardManager
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.getSystemService
import code.name.monkey.appthemehelper.util.VersionUtils
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.activities.base.AbsMusicServiceActivity
import ru.stersh.retrosonic.databinding.ActivityLockScreenBinding
import ru.stersh.retrosonic.extensions.hideStatusBar
import ru.stersh.retrosonic.extensions.setTaskDescriptionColorAuto
import ru.stersh.retrosonic.extensions.whichFragment
import ru.stersh.retrosonic.fragments.player.lockscreen.LockScreenControlsFragment

class LockScreenActivity : AbsMusicServiceActivity() {
    private lateinit var binding: ActivityLockScreenBinding
    private var fragment: LockScreenControlsFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lockScreenInit()
        binding = ActivityLockScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideStatusBar()
        setTaskDescriptionColorAuto()

        val config = SlidrConfig.Builder().listener(object : SlidrListener {
            override fun onSlideStateChanged(state: Int) {
            }

            override fun onSlideChange(percent: Float) {
            }

            override fun onSlideOpened() {
            }

            override fun onSlideClosed(): Boolean {
                if (VersionUtils.hasOreo()) {
                    val keyguardManager =
                        getSystemService<KeyguardManager>()
                    keyguardManager?.requestDismissKeyguard(this@LockScreenActivity, null)
                }
                finish()
                return true
            }
        }).position(SlidrPosition.BOTTOM).build()

        Slidr.attach(this, config)

        fragment = whichFragment<LockScreenControlsFragment>(R.id.playback_controls_fragment)

        binding.slide.apply {
            translationY = 100f
            alpha = 0f
            animate().translationY(0f).alpha(1f).setDuration(1500).start()
        }
    }

    @Suppress("Deprecation")
    private fun lockScreenInit() {
        if (VersionUtils.hasOreoMR1()) {
            setShowWhenLocked(true)
            // setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                //          or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    private fun updateSongs() {
//        TODO: fix update songs
//        val song = MusicPlayerRemote.currentSongId
//        GlideApp.with(this)
//            .asBitmapPalette()
//            .songCoverOptions(song)
//            .load(RetroGlideExtension.getSongModel(song))
//            .dontAnimate()
//            .into(object : RetroMusicColoredTarget(binding.image) {
//                override fun onColorReady(colors: MediaNotificationProcessor) {
//                    fragment?.setColor(colors)
//                }
//            })
    }
}
