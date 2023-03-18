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
package ru.stersh.retrosonic.feature.details.artist.data

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stersh.apisonic.models.Album
import ru.stersh.apisonic.models.Song
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.retrosonic.feature.details.artist.domain.ArtistDetails
import ru.stersh.retrosonic.feature.details.artist.domain.ArtistDetailsAlbum
import ru.stersh.retrosonic.feature.details.artist.domain.ArtistDetailsRepository
import ru.stersh.retrosonic.feature.details.artist.domain.ArtistDetailsSong

class ArtistDetailsRepositoryImpl(private val apiSonicProvider: ApiSonicProvider) : ArtistDetailsRepository {
    override fun getArtistDetails(id: String): Flow<ArtistDetails> = flow {
        val artist = apiSonicProvider.getApiSonic().getArtist(id)
        val artistInfo = apiSonicProvider.getApiSonic().getArtistInfo2(id)

        ArtistDetails(
            title = artist.name,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(artist.coverArt),
            biography = artistInfo.biography,
            albumCount = artist.albumCount,
            songCount = artist.albums?.sumOf { it.songCount } ?: 0,
            totalDuration = (artist.albums?.sumOf { it.duration } ?: 0) * 1000L,
        ).also { emit(it) }
    }

    override fun getArtistAlbums(id: String): Flow<List<ArtistDetailsAlbum>> = flow {
        getAlbums(id)
            .map { it.toDomain() }
            .also { emit(it) }
    }

    override fun getArtistSongs(id: String): Flow<List<ArtistDetailsSong>> = flow {
        getSongs(id)
            .map { it.toDomain() }
            .also { emit(it) }
    }

    private suspend fun getSongs(artistId: String): List<Song> = coroutineScope {
        return@coroutineScope getAlbums(artistId).map {
            async { apiSonicProvider.getApiSonic().getAlbum(it.id).song ?: emptyList() }
        }
            .awaitAll()
            .flatten()
    }

    private suspend fun getAlbums(artistId: String): List<Album> {
        return apiSonicProvider
            .getApiSonic()
            .getArtist(artistId)
            .albums ?: emptyList()
    }

    private suspend fun Album.toDomain(): ArtistDetailsAlbum {
        return ArtistDetailsAlbum(
            id = id,
            title = name,
            artist = artist,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
            year = year,
        )
    }

    private suspend fun Song.toDomain(): ArtistDetailsSong {
        return ArtistDetailsSong(
            id = id,
            title = title,
            artist = artist,
            album = album,
            albumId = albumId,
            track = track,
            duration = duration * 1000L,
            year = year,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
        )
    }
}
