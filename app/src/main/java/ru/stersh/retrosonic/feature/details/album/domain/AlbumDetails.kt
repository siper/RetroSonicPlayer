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
package ru.stersh.retrosonic.feature.details.album.domain

data class AlbumDetails(
    val id: String,
    val title: String,
    val artist: Artist,
    val coverArtUrl: String,
    val year: Int,
    val songs: List<Song>,
) {
    data class Artist(
        val id: String,
        val title: String,
        val coverArtUrl: String,
    )

    data class Song(
        val id: String,
        val albumId: String,
        val coverArtUrl: String,
        val title: String,
        val artist: String,
        val album: String,
        val year: Int,
    )
}
