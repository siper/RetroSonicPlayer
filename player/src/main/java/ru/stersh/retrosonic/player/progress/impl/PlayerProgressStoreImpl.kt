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
package ru.stersh.retrosonic.player.progress.impl

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.retrosonic.player.android.MusicService
import ru.stersh.retrosonic.player.progress.PlayerProgress
import ru.stersh.retrosonic.player.progress.PlayerProgressStore
import ru.stersh.retrosonic.player.utils.mediaControllerFuture
import ru.stersh.retrosonic.player.utils.withPlayer
import java.text.SimpleDateFormat
import java.util.*

internal class PlayerProgressStoreImpl(
    private val context: Context,
    private val apiSonicProvider: ApiSonicProvider,
) : PlayerProgressStore {
    private val executor = ContextCompat.getMainExecutor(context)
    private val minuteTimeFormat = SimpleDateFormat("mm:ss")
    private val hourTimeFormat = SimpleDateFormat("hh:mm:ss")
    private var scrobbleSender: ScrobbleSender? = null

    override fun playerProgress(): Flow<PlayerProgress?> = callbackFlow {
        val mediaControllerFuture = mediaControllerFuture(context, MusicService::class.java)

        var player: Player? = null
        var callback: Player.Listener? = null

        mediaControllerFuture.withPlayer(executor) {
            trySend(progress)

            launch {
                while (true) {
                    delay(SEND_PROGRESS_DELAY)

                    val id = currentMediaItem?.mediaId
                    if (id != null && scrobbleSender?.id != id) {
                        scrobbleSender = ScrobbleSender(id, apiSonicProvider)
                    }

                    val currentProgress = progress ?: continue
                    when {
                        currentProgress.currentTimeMs > SEND_SCROBBLE_EVENT_TIME -> {
                            launch { scrobbleSender?.trySendScrobble() }
                        }
                        (currentProgress.currentTimeMs + SEND_SCROBBLE_EVENT_TIME) >= currentProgress.totalTimeMs -> {
                            launch { scrobbleSender?.trySendSubmission() }
                        }
                    }
                    trySend(progress)
                }
            }

            val sessionCallback = object : Player.Listener {
                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    trySend(progress)
                }
            }
            addListener(sessionCallback)

            player = this
            callback = sessionCallback
        }

        awaitClose {
            callback?.let { player?.removeListener(it) }
            callback = null
            player = null
        }
    }.distinctUntilChanged()

    private val Player.progress: PlayerProgress?
        get() {
            val totalTimeMs = duration.takeIf { it != C.TIME_UNSET } ?: return null
            val currentTimeMs = if (currentPosition > totalTimeMs) {
                totalTimeMs
            } else {
                currentPosition
            }
            return PlayerProgress(
                totalTimeMs = totalTimeMs,
                currentTimeMs = currentTimeMs,
                totalTime = formatTime(totalTimeMs),
                currentTime = formatTime(currentTimeMs),
            )
        }

    private fun formatTime(time: Long): String {
        return if (time >= ONE_HOUR_MS) {
            hourTimeFormat.format(Date(time))
        } else {
            minuteTimeFormat.format(Date(time))
        }
    }

    companion object {
        private const val ONE_HOUR_MS = 3600000L
        private const val SEND_PROGRESS_DELAY = 500L
        private const val SEND_SCROBBLE_EVENT_TIME = 5000L
    }
}
