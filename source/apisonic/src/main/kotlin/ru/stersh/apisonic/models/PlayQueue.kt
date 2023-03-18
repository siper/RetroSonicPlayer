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
data class PlayQueueResponse(
    @Json(name = "playQueue") val playQueue: PlayQueue,
) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class PlayQueue(
    @Json(name = "current")
    val current: Long?,
    @Json(name = "position")
    val position: Long,
    @Json(name = "username")
    val username: String,
    @Json(name = "changed")
    val changed: String,
    @Json(name = "changedBy")
    val changedBy: String,
    @Json(name = "entry")
    val entry: List<Song>
)
