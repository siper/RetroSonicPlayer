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
package ru.stersh.retrosonic.player.controls.data

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.HeartRating
import androidx.media3.common.Player
import kotlinx.coroutines.launch
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.retrosonic.core.extensions.ApplicationScope
import ru.stersh.retrosonic.player.android.MusicService
import ru.stersh.retrosonic.player.controls.PlayerControls
import ru.stersh.retrosonic.player.utils.MEDIA_SONG_ID
import ru.stersh.retrosonic.player.utils.mediaControllerFuture
import ru.stersh.retrosonic.player.utils.mediaItems

internal class PlayerControlsImpl(
    private val context: Context,
    private val apiSonicProvider: ApiSonicProvider,
) : PlayerControls {
    private val mediaControllerFuture = mediaControllerFuture(context, MusicService::class.java)
    private val mainExecutor = ContextCompat.getMainExecutor(context)

    override fun play() = withPlayer {
        play()
    }

    override fun pause() = withPlayer {
        pause()
    }

    override fun next() = withPlayer {
        seekToNextMediaItem()
    }

    override fun previous() = withPlayer {
        if (currentPosition >= 3000) {
            seekTo(0)
        } else {
            seekToPreviousMediaItem()
        }
    }

    override fun seek(time: Long) = withPlayer {
        seekTo(time)
    }

    override fun toggleFavorite(favorite: Boolean) {
        withPlayer {
            val item = currentMediaItem ?: return@withPlayer
            val id = item.mediaMetadata.extras?.getString(MEDIA_SONG_ID) ?: return@withPlayer
            ApplicationScope.launch {
                if (favorite) {
                    apiSonicProvider.getApiSonic().starSong(id)
                } else {
                    apiSonicProvider.getApiSonic().unstarSong(id)
                }
            }
        }
        toggleFavoriteInMediaItems(favorite)
    }

    private fun toggleFavoriteInMediaItems(favorite: Boolean) = withPlayer {
        val item = currentMediaItem ?: return@withPlayer
        val index = mediaItems.indexOf(item).takeIf { it != -1 } ?: return@withPlayer
        val newMetadata = item
            .mediaMetadata
            .buildUpon()
            .setUserRating(HeartRating(favorite))
            .build()
        val newItem = item
            .buildUpon()
            .setMediaMetadata(newMetadata)
            .build()
        val newMediaItems = mediaItems.toMutableList()
        newMediaItems.removeAt(index)
        newMediaItems.add(index, newItem)
        val startPosition = currentPosition
        setMediaItems(newMediaItems, index, startPosition)
    }

    private inline fun withPlayer(crossinline block: Player.() -> Unit) {
        mediaControllerFuture.addListener(
            {
                val player = mediaControllerFuture.get()
                block.invoke(player)
            },
            mainExecutor,
        )
    }
}
