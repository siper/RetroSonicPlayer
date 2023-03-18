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
data class PlaylistsResponse(@Json(name = "playlists") val playlists: Playlists) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class Playlists(@Json(name = "playlist") val playlists: List<Playlist>?) {

    @JsonClass(generateAdapter = true)
    data class Playlist(
        @Json(name = "changed") val changed: String,
        @Json(name = "coverArt") val coverArt: String,
        @Json(name = "created") val created: String,
        @Json(name = "duration") val duration: Int,
        @Json(name = "id") val id: String,
        @Json(name = "name") val name: String,
        @Json(name = "owner") val owner: String,
        @Json(name = "public") val isPublic: Boolean,
        @Json(name = "songCount") val songCount: Int,
    )
}

@JsonClass(generateAdapter = true)
data class PlaylistResponse(@Json(name = "playlist") val playlist: Playlist) : SubsonicResponse {

    @JsonClass(generateAdapter = true)
    data class Playlist(
        @Json(name = "changed") val changed: String,
        @Json(name = "coverArt") val coverArt: String,
        @Json(name = "created") val created: String,
        @Json(name = "duration") val duration: Int,
        @Json(name = "entry") val entry: List<Entry>,
        @Json(name = "id") val id: String,
        @Json(name = "name") val name: String,
        @Json(name = "owner") val owner: String,
        @Json(name = "public") val `public`: Boolean,
        @Json(name = "songCount") val songCount: Int,
    )

    @JsonClass(generateAdapter = true)
    data class Entry(
        @Json(name = "album") val album: String,
        @Json(name = "albumId") val albumId: String,
        @Json(name = "artist") val artist: String,
        @Json(name = "artistId") val artistId: String,
        @Json(name = "bitRate") val bitRate: Int,
        @Json(name = "contentType") val contentType: String,
        @Json(name = "coverArt") val coverArt: String,
        @Json(name = "created") val created: String,
        @Json(name = "discNumber") val discNumber: Int?,
        @Json(name = "duration") val duration: Int,
        @Json(name = "genre") val genre: String,
        @Json(name = "id") val id: String,
        @Json(name = "isDir") val isDir: Boolean,
        @Json(name = "isVideo") val isVideo: Boolean,
        @Json(name = "parent") val parent: String,
        @Json(name = "path") val path: String,
        @Json(name = "playCount") val playCount: Int,
        @Json(name = "size") val size: Int,
        @Json(name = "suffix") val suffix: String,
        @Json(name = "starred") val starred: String?,
        @Json(name = "userRating") val userRating: Int?,
        @Json(name = "title") val title: String,
        @Json(name = "track") val track: Int,
        @Json(name = "transcodedContentType") val transcodedContentType: String?,
        @Json(name = "transcodedSuffix") val transcodedSuffix: String?,
        @Json(name = "type") val type: String,
        @Json(name = "year") val year: Int,
    )
}
