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
data class MusicFoldersResponse(@Json(name = "musicFolders") val musicFolders: MusicFolders) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class MusicFolders(
    @Json(name = "musicFolder") val musicFolders: List<MusicFolder>,
)

@JsonClass(generateAdapter = true)
data class MusicFolder(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
)
