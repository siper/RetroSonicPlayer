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
package ru.stersh.retrosonic.player.queue.impl

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ru.stersh.retrosonic.player.android.MusicService
import ru.stersh.retrosonic.player.queue.PlayerQueueManager
import ru.stersh.retrosonic.player.utils.mediaControllerFuture
import ru.stersh.retrosonic.player.utils.mediaItems
import ru.stersh.retrosonic.player.utils.withPlayer

class PlayerQueueManagerImpl(private val context: Context) : PlayerQueueManager {
    private val mainExecutor = ContextCompat.getMainExecutor(context)

    override fun currentPlayingItemPosition(): Flow<Int> = callbackFlow {
        val controller = mediaControllerFuture(context, MusicService::class.java)

        var listener: Player.Listener? = null
        var player: Player? = null

        controller.withPlayer(mainExecutor) {
            trySend(currentMediaItemIndex)

            val sessionListener = object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    if (events.containsAny(
                            Player.EVENT_TIMELINE_CHANGED,
                            Player.EVENT_IS_PLAYING_CHANGED,
                            Player.EVENT_MEDIA_METADATA_CHANGED,
                            Player.EVENT_METADATA,
                        )
                    ) {
                        trySend(currentMediaItemIndex)
                    }
                }
            }
            addListener(sessionListener)

            listener = sessionListener
            player = this
        }

        awaitClose {
            listener?.let { player?.removeListener(it) }
        }
    }

    override fun getQueue(): Flow<List<MediaItem>> = callbackFlow {
        val controller = mediaControllerFuture(context, MusicService::class.java)

        var listener: Player.Listener? = null
        var player: Player? = null

        controller.withPlayer(mainExecutor) {
            trySend(mediaItems)

            val sessionListener = object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    if (events.containsAny(
                            Player.EVENT_TIMELINE_CHANGED,
                            Player.EVENT_IS_PLAYING_CHANGED,
                            Player.EVENT_MEDIA_METADATA_CHANGED,
                            Player.EVENT_METADATA,
                        )
                    ) {
                        trySend(mediaItems)
                    }
                }
            }
            addListener(sessionListener)

            listener = sessionListener
            player = this
        }

        awaitClose {
            listener?.let { player?.removeListener(it) }
        }
    }

    override fun clearQueue() {
        mediaControllerFuture(context, MusicService::class.java).withPlayer(mainExecutor) {
            clearMediaItems()
        }
    }

    override fun playPosition(position: Int) {
        mediaControllerFuture(context, MusicService::class.java).withPlayer(mainExecutor) {
            seekTo(position, 0L)
        }
    }

    override fun moveSong(from: Int, to: Int) {
        mediaControllerFuture(context, MusicService::class.java).withPlayer(mainExecutor) {
            moveMediaItem(from, to)
        }
    }

    override fun removeSong(position: Int) {
        mediaControllerFuture(context, MusicService::class.java).withPlayer(mainExecutor) {
            removeMediaItem(position)
        }
    }
}
