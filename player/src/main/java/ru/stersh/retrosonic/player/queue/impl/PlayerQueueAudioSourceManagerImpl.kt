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
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media3.common.HeartRating
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.StarRating
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.stersh.apisonic.models.PlaylistResponse
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.retrosonic.player.android.MusicService
import ru.stersh.retrosonic.player.queue.AudioSource
import ru.stersh.retrosonic.player.queue.PlayerQueueAudioSourceManager
import ru.stersh.retrosonic.player.utils.MEDIA_ITEM_ALBUM_ID
import ru.stersh.retrosonic.player.utils.MEDIA_ITEM_DURATION
import ru.stersh.retrosonic.player.utils.MEDIA_SONG_ID
import ru.stersh.retrosonic.player.utils.mediaControllerFuture
import ru.stersh.retrosonic.player.utils.toMediaItem
import ru.stersh.retrosonic.player.utils.withPlayer

internal class PlayerQueueAudioSourceManagerImpl(
    private val context: Context,
    private val apiSonicProvider: ApiSonicProvider,
) : PlayerQueueAudioSourceManager {
    private val mediaController = mediaControllerFuture(context, MusicService::class.java)
    private val mainExecutor = ContextCompat.getMainExecutor(context)

    override suspend fun playSource(source: AudioSource, shuffled: Boolean) {
        val newQueue = when (source) {
            is AudioSource.Song -> listOf(getSong(source))
            is AudioSource.Album -> getSongs(source)
            is AudioSource.Artist -> getSongs(source)
            is AudioSource.Playlist -> getSongs(source)
        }
        mediaController.withPlayer(mainExecutor) {
            if (shuffled) {
                setMediaItems(newQueue.shuffled())
            } else {
                setMediaItems(newQueue)
            }
            prepare()
            play()
        }
    }

    override suspend fun addSource(source: AudioSource, shuffled: Boolean) {
        val newSongs = when (source) {
            is AudioSource.Song -> listOf(getSong(source))
            is AudioSource.Album -> getSongs(source)
            is AudioSource.Artist -> getSongs(source)
            is AudioSource.Playlist -> getSongs(source)
        }
        mediaController.withPlayer(mainExecutor) {
            if (shuffled) {
                addMediaItems(newSongs.shuffled())
            } else {
                addMediaItems(newSongs)
            }
            prepare()
            play()
        }
    }

    private suspend fun getSong(source: AudioSource.Song): MediaItem {
        return apiSonicProvider
            .getApiSonic()
            .getSong(source.id)
            .toMediaItem(apiSonicProvider)
    }

    private suspend fun getSongs(source: AudioSource.Album): List<MediaItem> {
        val songs = apiSonicProvider
            .getApiSonic()
            .getAlbum(source.id)
            .song
            ?.takeIf { it.isNotEmpty() }
            ?: return emptyList()

        return songs.map { song ->
            song.toMediaItem(apiSonicProvider)
        }
    }

    private suspend fun getSongs(source: AudioSource.Artist): List<MediaItem> = coroutineScope {
        val albums = apiSonicProvider
            .getApiSonic()
            .getArtist(source.id)
            .albums
            ?.takeIf { it.isNotEmpty() }
            ?: return@coroutineScope emptyList()

        return@coroutineScope albums
            .map {
                async { getSongs(AudioSource.Album(it.id)) }
            }
            .awaitAll()
            .flatten()
    }

    private suspend fun getSongs(source: AudioSource.Playlist): List<MediaItem> = coroutineScope {
        return@coroutineScope apiSonicProvider
            .getApiSonic()
            .getPlaylist(source.id)
            .entry
            .map { it.toMediaItem() }
    }

    private suspend fun PlaylistResponse.Entry.toMediaItem(): MediaItem {
        val songUri = apiSonicProvider
            .getApiSonic()
            .downloadUrl(id)
            .toUri()

        val artworkUri = apiSonicProvider
            .getApiSonic()
            .getCoverArtUrl(coverArt)
            .toUri()

        val songRating = userRating
        val rating = if (songRating != null && songRating > 0) {
            StarRating(5, songRating.toFloat())
        } else {
            StarRating(5)
        }
        val starredRating = HeartRating(starred != null)

        val metadata = MediaMetadata
            .Builder()
            .setTitle(title)
            .setArtist(artist)
            .setExtras(
                bundleOf(
                    MEDIA_ITEM_ALBUM_ID to albumId,
                    MEDIA_ITEM_DURATION to duration * 1000L,
                    MEDIA_SONG_ID to id,
                ),
            )
            .setUserRating(starredRating)
            .setOverallRating(rating)
            .setArtworkUri(artworkUri)
            .build()
        val requestMetadata = MediaItem
            .RequestMetadata
            .Builder()
            .setMediaUri(songUri)
            .build()
        return MediaItem
            .Builder()
            .setMediaId(id)
            .setMediaMetadata(metadata)
            .setRequestMetadata(requestMetadata)
            .setUri(songUri)
            .build()
    }
}
