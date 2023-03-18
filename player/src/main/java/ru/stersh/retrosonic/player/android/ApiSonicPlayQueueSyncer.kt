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
package ru.stersh.retrosonic.player.android

import androidx.media3.common.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.retrosonic.player.utils.mediaItems
import ru.stersh.retrosonic.player.utils.toMediaItem
import timber.log.Timber

internal class ApiSonicPlayQueueSyncer(private val apiSonicProvider: ApiSonicProvider) {

    suspend fun loadPlayQueue(player: Player) {
        val playQueue = withContext(Dispatchers.IO) {
            runCatching {
                apiSonicProvider
                    .getApiSonic()
                    .getPlayQueue()
                    .playQueue
            }
                .onFailure { Timber.w(it) }
                .getOrNull()
        } ?: return

        val items = playQueue.entry.map { it.toMediaItem(apiSonicProvider) }
        val currentIndex = items
            .indexOfFirst { it.mediaId == playQueue.current?.toString() }
            .takeIf { it != -1 }
        val position = playQueue.position

        if (currentIndex != null) {
            player.setMediaItems(items, currentIndex, position)
        } else {
            player.setMediaItems(items)
        }
        player.prepare()
    }

    suspend fun syncPlayQueue(player: Player) = withContext(Dispatchers.Main) {
        if (!player.isPlaying) {
            return@withContext
        }

        val items = player.mediaItems.map { it.mediaId }
        val current = player.currentMediaItem?.mediaId
        val position = player.currentPosition

        withContext(Dispatchers.IO) {
            runCatching {
                apiSonicProvider
                    .getApiSonic()
                    .savePlayQueue(items, current, position)
            }.onFailure { Timber.w(it) }
        }
    }
}
