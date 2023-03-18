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
package ru.stersh.retrosonic.feature.library.artist.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stersh.apisonic.models.Artist
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.retrosonic.feature.library.artist.domain.LibraryArtist
import ru.stersh.retrosonic.feature.library.artist.domain.LibraryArtistRepository

class LibraryArtistRepositoryImpl(private val apiSonicProvider: ApiSonicProvider) : LibraryArtistRepository {
    override fun getArtists(): Flow<List<LibraryArtist>> = flow {
        apiSonicProvider
            .getApiSonic()
            .getArtists()
            .asArtistList()
            .map { it.toDomain() }
            .also { emit(it) }
    }

    private suspend fun Artist.toDomain(): LibraryArtist {
        return LibraryArtist(
            id = id,
            title = name,
            coverArtUrl = apiSonicProvider
                .getApiSonic()
                .getCoverArtUrl(coverArt),
        )
    }
}
