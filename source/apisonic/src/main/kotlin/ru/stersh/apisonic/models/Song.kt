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
package ru.stersh.apisonic.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SongResponse(@Json(name = "song") val song: Song) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class SimilarSongsResponse(@Json(name = "similarSongs") val similarSongs: SimilarSongs) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class SimilarSongs(@Json(name = "song") val similarSongs: List<Song>)

@JsonClass(generateAdapter = true)
data class TopSongsResponse(@Json(name = "topSongs") val topSongs: TopSongs) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class TopSongs(@Json(name = "song") val topSongs: List<Song>)

@JsonClass(generateAdapter = true)
data class RandomSongsResponse(@Json(name = "randomSongs") val randomSongs: RandomSongs) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class RandomSongs(@Json(name = "song") val randomSongs: List<Song>)

@JsonClass(generateAdapter = true)
data class SongsByGenreResponse(@Json(name = "songsByGenre") val songsByGenre: SongsByGenre) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class SongsByGenre(@Json(name = "song") val songsByGenre: List<Song>)

@JsonClass(generateAdapter = true)
data class Song(
    @Json(name = "album") val album: String,
    @Json(name = "albumId") val albumId: String,
    @Json(name = "artist") val artist: String,
    @Json(name = "artistId") val artistId: String?,
    @Json(name = "bitRate") val bitRate: Int,
    @Json(name = "contentType") val contentType: String,
    @Json(name = "coverArt") val coverArt: String,
    @Json(name = "created") val created: String,
    @Json(name = "duration") val duration: Int,
    @Json(name = "genre") val genre: String,
    @Json(name = "id") val id: String,
    @Json(name = "isDir") val isDir: Boolean,
    @Json(name = "parent") val parent: String,
    @Json(name = "path") val path: String,
    @Json(name = "playCount") val playCount: Int,
    @Json(name = "size") val size: Int,
    @Json(name = "suffix") val suffix: String,
    @Json(name = "title") val title: String,
    @Json(name = "track") val track: Int,
    @Json(name = "type") val type: String,
    @Json(name = "year") val year: Int,
    @Json(name = "starred") val starred: String?,
    @Json(name = "averageRating") val averageRating: Double?,
    @Json(name = "userRating") val userRating: Int?,
)
