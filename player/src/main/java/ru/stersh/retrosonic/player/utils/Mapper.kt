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
package ru.stersh.retrosonic.player.utils

import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media3.common.HeartRating
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.StarRating
import ru.stersh.apisonic.models.Song
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider

internal suspend fun Song.toMediaItem(apiSonicProvider: ApiSonicProvider): MediaItem {
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
        .setOverallRating(rating)
        .setUserRating(starredRating)
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
