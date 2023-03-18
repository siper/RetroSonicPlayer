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
package ru.stersh.retrosonic.feature.player.cover.data

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ru.stersh.retrosonic.feature.player.cover.domain.CoverArtUrlList
import ru.stersh.retrosonic.feature.player.cover.domain.CoverArtUrlsRepository
import ru.stersh.retrosonic.player.android.MusicService
import ru.stersh.retrosonic.player.utils.mediaControllerFuture
import ru.stersh.retrosonic.player.utils.mediaItems

class CoverArtUrlsRepositoryImpl(private val context: Context) : CoverArtUrlsRepository {

    override fun getCoverArtUrls(): Flow<CoverArtUrlList> = callbackFlow {
        val mediaControllerFuture = mediaControllerFuture(context, MusicService::class.java)

        var player: Player? = null
        var callback: Player.Listener? = null

        mediaControllerFuture.addListener(
            {
                val sessionPlayer = mediaControllerFuture.get()
                player = sessionPlayer
                var lastItems = getNewCoverArtUrlList(sessionPlayer)

                trySend(lastItems)

                val sessionCallback = object : Player.Listener {
                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                        val newItems = getNewCoverArtUrlList(sessionPlayer)
                        if (lastItems != newItems) {
                            lastItems = newItems
                            trySend(newItems)
                        }
                    }
                }
                callback = sessionCallback
                sessionPlayer.addListener(sessionCallback)
            },
            ContextCompat.getMainExecutor(context),
        )

        awaitClose {
            val c = callback ?: return@awaitClose
            player?.removeListener(c)
        }
    }

    private fun getNewCoverArtUrlList(player: Player): CoverArtUrlList {
        return CoverArtUrlList(
            list = player.mediaItems.map { it.mediaMetadata.artworkUri.toString() },
            selectedPosition = player.currentMediaItemIndex,
        )
    }
}
