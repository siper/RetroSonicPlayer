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
package ru.stersh.retrosonic.feature.library.album.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stersh.apisonic.models.AlbumList2
import ru.stersh.apisonic.models.ListType
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.retrosonic.feature.library.album.domain.LibraryAlbum
import ru.stersh.retrosonic.feature.library.album.domain.LibraryAlbumRepository

class LibraryAlbumRepositoryImpl(private val apiSonicProvider: ApiSonicProvider) : LibraryAlbumRepository {
    override fun getAlbums(): Flow<List<LibraryAlbum>> = flow {
        apiSonicProvider
            .getApiSonic()
            .getAlbumList2(ListType.ALPHABETICAL_BY_ARTIST, size = 500)
            .map { it.toLibraryAlbum() }
            .also { emit(it) }
    }

    private suspend fun AlbumList2.Album.toLibraryAlbum(): LibraryAlbum {
        return LibraryAlbum(
            id = id,
            title = name,
            artist = artist,
            coverUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
            year = year,
        )
    }
}
