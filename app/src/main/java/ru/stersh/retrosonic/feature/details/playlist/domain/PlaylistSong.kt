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
package ru.stersh.retrosonic.feature.details.playlist.domain

data class PlaylistSong(
    val id: String,
    val title: String,
    val trackNumber: Int,
    val year: Int,
    val duration: Long,
    val coverArtUrl: String?,
    val albumId: String,
    val album: String,
    val artistId: String,
    val artist: String?,
) {
    companion object {

        @JvmStatic
        val emptySong = PlaylistSong(
            id = "-1",
            title = "",
            trackNumber = -1,
            year = -1,
            duration = -1,
            albumId = "-1",
            album = "",
            artistId = "-1",
            artist = "",
            coverArtUrl = null,
        )
    }
}
