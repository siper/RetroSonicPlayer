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
package ru.stersh.retrosonic.feature.details.album.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stersh.apisonic.models.Album
import ru.stersh.apisonic.models.Artist
import ru.stersh.apisonic.models.Song
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.retrosonic.feature.details.album.domain.AlbumDetails
import ru.stersh.retrosonic.feature.details.album.domain.AlbumDetailsRepository

class AlbumDetailsRepositoryImpl(private val apiSonicProvider: ApiSonicProvider) : AlbumDetailsRepository {
    override fun getAlbumDetails(id: String): Flow<AlbumDetails> = flow {
        val album = apiSonicProvider.getApiSonic().getAlbum(id)
        val artist = apiSonicProvider.getApiSonic().getArtist(album.artistId)

        album
            .toDomain(artist.toDomain())
            .also { emit(it) }
    }

    private suspend fun Album.toDomain(artist: AlbumDetails.Artist): AlbumDetails {
        return AlbumDetails(
            id = id,
            title = name,
            artist = artist,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
            year = year,
            songs = song?.map { it.toDomain() } ?: emptyList(),
        )
    }

    private suspend fun Artist.toDomain(): AlbumDetails.Artist {
        return AlbumDetails.Artist(
            id = id,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
            title = name,
        )
    }

    private suspend fun Song.toDomain(): AlbumDetails.Song {
        return AlbumDetails.Song(
            id = id,
            albumId = albumId,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
            year = year,
            title = title,
            artist = artist,
            album = album,
        )
    }
}
