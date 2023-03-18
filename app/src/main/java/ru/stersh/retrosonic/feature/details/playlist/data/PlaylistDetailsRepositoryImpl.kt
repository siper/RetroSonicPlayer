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
package ru.stersh.retrosonic.feature.details.playlist.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stersh.apisonic.models.PlaylistResponse
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.retrosonic.feature.details.playlist.domain.PlaylistDetails
import ru.stersh.retrosonic.feature.details.playlist.domain.PlaylistDetailsRepository
import ru.stersh.retrosonic.feature.details.playlist.domain.PlaylistSong

class PlaylistDetailsRepositoryImpl(private val apiSonicProvider: ApiSonicProvider) : PlaylistDetailsRepository {
    override fun getPlaylistDetails(id: String): Flow<PlaylistDetails> {
        return flow {
            apiSonicProvider
                .getApiSonic()
                .getPlaylist(id)
                .toDomain()
                .also { emit(it) }
        }
    }

    override fun getPlaylistSongs(id: String): Flow<List<PlaylistSong>> {
        return flow {
            apiSonicProvider
                .getApiSonic()
                .getPlaylist(id)
                .entry
                .map { it.toDomain() }
                .also { emit(it) }
        }
    }

    private suspend fun PlaylistResponse.Entry.toDomain(): PlaylistSong {
        return PlaylistSong(
            id = id,
            albumId = albumId,
            album = album,
            artist = artist,
            artistId = artistId,
            title = title,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
            duration = duration * 1000L,
            year = year,
            trackNumber = track,
        )
    }

    private suspend fun PlaylistResponse.Playlist.toDomain(): PlaylistDetails {
        return PlaylistDetails(
            title = name,
            songCount = songCount,
            duration = duration * 1000L,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
        )
    }
}
