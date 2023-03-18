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
package ru.stersh.retrosonic.feature.library.playlist.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stersh.apisonic.models.Playlists
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.retrosonic.feature.library.playlist.domain.LibraryPlaylist
import ru.stersh.retrosonic.feature.library.playlist.domain.LibraryPlaylistRepository

class LibraryPlaylistRepositoryImpl(private val apiSonicProvider: ApiSonicProvider) : LibraryPlaylistRepository {

    override fun getPlaylists(): Flow<List<LibraryPlaylist>> = flow {
        apiSonicProvider
            .getApiSonic()
            .getPlaylists()
            .map { it.toDomain() }
            .also { emit(it) }
    }

    private suspend fun Playlists.Playlist.toDomain(): LibraryPlaylist {
        return LibraryPlaylist(
            id = id,
            title = name,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
            songCount = songCount,
            duration = duration * 1000L,
        )
    }
}
