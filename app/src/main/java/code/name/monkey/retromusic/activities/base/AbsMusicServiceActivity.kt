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
package code.name.monkey.retromusic.activities.base

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import code.name.monkey.appthemehelper.util.VersionUtils
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.repository.RealRepository
import org.koin.android.ext.android.inject
import ru.stersh.retrosonic.player.android.MusicService
import java.lang.ref.WeakReference

abstract class AbsMusicServiceActivity : AbsBaseActivity() {

    private val repository: RealRepository by inject()
    private var musicStateReceiver: MusicStateReceiver? = null
    private var receiverRegistered: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serviceIntent = Intent(this, MusicService::class.java)
        startService(serviceIntent)
        setPermissionDeniedMessage(getString(R.string.permission_external_storage_denied))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (receiverRegistered) {
            unregisterReceiver(musicStateReceiver)
            receiverRegistered = false
        }
    }

    override fun getPermissionsToRequest(): Array<String> {
        return mutableListOf(Manifest.permission.READ_EXTERNAL_STORAGE).apply {
            if (!VersionUtils.hasR()) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    private class MusicStateReceiver(activity: AbsMusicServiceActivity) : BroadcastReceiver() {

        private val reference: WeakReference<AbsMusicServiceActivity> = WeakReference(activity)

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val activity = reference.get()
//            if (activity != null && action != null) {
//                when (action) {
//                    FAVORITE_STATE_CHANGED -> activity.onFavoriteStateChanged()
//                    META_CHANGED -> activity.onPlayingMetaChanged()
//                    QUEUE_CHANGED -> activity.onQueueChanged()
//                    PLAY_STATE_CHANGED -> activity.onPlayStateChanged()
//                    REPEAT_MODE_CHANGED -> activity.onRepeatModeChanged()
//                    SHUFFLE_MODE_CHANGED -> activity.onShuffleModeChanged()
//                    MEDIA_STORE_CHANGED -> activity.onMediaStoreChanged()
//                }
//            }
        }
    }

    companion object {
        val TAG: String = AbsMusicServiceActivity::class.java.simpleName
    }
}
