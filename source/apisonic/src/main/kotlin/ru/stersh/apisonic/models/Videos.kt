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
data class VideosResponse(@Json(name = "videos") val videos: VideosObject) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class VideosObject(
    @Json(name = "video") val videos: List<Video>,
)

@JsonClass(generateAdapter = true)
data class Video(
    @Json(name = "album") val album: String,
    @Json(name = "bitRate") val bitRate: Int,
    @Json(name = "bookmarkPosition") val bookmarkPosition: Int,
    @Json(name = "contentType") val contentType: String,
    @Json(name = "created") val created: String,
    @Json(name = "duration") val duration: Int,
    @Json(name = "id") val id: String,
    @Json(name = "isDir") val isDir: Boolean,
    @Json(name = "isVideo") val isVideo: Boolean,
    @Json(name = "originalHeight") val originalHeight: Int,
    @Json(name = "originalWidth") val originalWidth: Int,
    @Json(name = "parent") val parent: String,
    @Json(name = "path") val path: String,
    @Json(name = "playCount") val playCount: Int,
    @Json(name = "size") val size: Int,
    @Json(name = "suffix") val suffix: String,
    @Json(name = "title") val title: String,
    @Json(name = "type") val type: String,
)
