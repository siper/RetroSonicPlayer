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
data class GenreResponse(@Json(name = "genres") val genres: Genres) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class Genres(
    @Json(name = "genre") val genres: List<Genre>,
)

@JsonClass(generateAdapter = true)
data class Genre(
    @Json(name = "albumCount") val albumCount: Int,
    @Json(name = "songCount") val songCount: Int,
    @Json(name = "value") val value: String,
)
