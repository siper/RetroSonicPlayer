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
data class StarredResponse(
    @Json(name = "starred") val starred: Starred,
) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class Starred(
    // TODO Emptylist instead of nullable possible?
    @Json(name = "album") val albums: List<Album>?,
    @Json(name = "artist") val artists: List<Artist>?,
    @Json(name = "song") val songs: List<Song>?,
) {

    @JsonClass(generateAdapter = true)
    data class Album(
        @Json(name = "album") val album: String,
        @Json(name = "artist") val artist: String,
        @Json(name = "averageRating") val averageRating: Double,
        @Json(name = "coverArt") val coverArt: String,
        @Json(name = "created") val created: String,
        @Json(name = "genre") val genre: String,
        @Json(name = "id") val id: String,
        @Json(name = "isDir") val isDir: Boolean,
        @Json(name = "parent") val parent: String,
        @Json(name = "playCount") val playCount: Int,
        @Json(name = "starred") val starred: String,
        @Json(name = "title") val title: String,
        @Json(name = "year") val year: Int,
    )

    @JsonClass(generateAdapter = true)
    data class Artist(
        @Json(name = "artistImageUrl") val artistImageUrl: String?,
        @Json(name = "id") val id: String,
        @Json(name = "name") val name: String,
        @Json(name = "starred") val starred: String,
    )
}

@JsonClass(generateAdapter = true)
data class Starred2Response(
    @Json(name = "starred2") val starred2: Starred2,
) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class Starred2(
    @Json(name = "album") val album: List<Album>?,
    @Json(name = "artist") val artist: List<Artist>?,
    @Json(name = "song") val song: List<Song>?,
) {

    @JsonClass(generateAdapter = true)
    data class Album(
        @Json(name = "artist") val artist: String,
        @Json(name = "artistId") val artistId: String,
        @Json(name = "coverArt") val coverArt: String,
        @Json(name = "created") val created: String,
        @Json(name = "duration") val duration: Int,
        @Json(name = "genre") val genre: String,
        @Json(name = "id") val id: String,
        @Json(name = "name") val name: String,
        @Json(name = "playCount") val playCount: Int,
        @Json(name = "songCount") val songCount: Int,
        @Json(name = "starred") val starred: String,
        @Json(name = "year") val year: Int,
    )

    @JsonClass(generateAdapter = true)
    data class Artist(
        @Json(name = "coverArt") val coverArt: String,
        @Json(name = "albumCount") val albumCount: Int,
        @Json(name = "id") val id: String,
        @Json(name = "name") val name: String,
        @Json(name = "starred") val starred: String,
    )
}
