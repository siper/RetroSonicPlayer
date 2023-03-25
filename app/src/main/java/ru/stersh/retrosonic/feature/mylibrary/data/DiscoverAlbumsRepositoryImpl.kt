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
package ru.stersh.retrosonic.feature.mylibrary.data

import ru.stersh.apisonic.models.AlbumList2
import ru.stersh.apisonic.models.ListType
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.retrosonic.feature.mylibrary.domain.Album
import ru.stersh.retrosonic.feature.mylibrary.domain.DiscoverAlbumsRepository

internal class DiscoverAlbumsRepositoryImpl(private val apiSonicProvider: ApiSonicProvider) : DiscoverAlbumsRepository {
    override suspend fun getRecentlyAdded(): List<Album> {
        return apiSonicProvider
            .getApiSonic()
            .getAlbumList2(ListType.NEWEST)
            .map { it.toDomain() }
    }

    override suspend fun getRecentlyPlayed(): List<Album> {
        return apiSonicProvider
            .getApiSonic()
            .getAlbumList2(ListType.RECENT)
            .map { it.toDomain() }
    }

    override suspend fun getMostPlayed(): List<Album> {
        return apiSonicProvider
            .getApiSonic()
            .getAlbumList2(ListType.FREQUENT)
            .map { it.toDomain() }
    }

    override suspend fun getRandom(): List<Album> {
        return apiSonicProvider
            .getApiSonic()
            .getAlbumList2(ListType.RANDOM)
            .map { it.toDomain() }
    }

    private suspend fun AlbumList2.Album.toDomain(): Album {
        return Album(
            id = id,
            title = name,
            coverUrl = coverArt?.let { apiSonicProvider.getApiSonic().getCoverArtUrl(it) }
        )
    }
}
