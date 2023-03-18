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
data class ArtistsResponse(@Json(name = "artists") val artists: Artists) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class Artists(
    @Json(name = "ignoredArticles") val ignoredArticles: String,
    @Json(name = "index") val indices: List<Index>,
) {

    @JsonClass(generateAdapter = true)
    data class Index(
        @Json(name = "name") val name: String,
        @Json(name = "artist") val artists: List<Artist>,
    )

    fun ignoredArticlesList(): List<String> = ignoredArticles.split(' ')

    fun asArtistList(): List<Artist> = indices.flatMap { index: Index -> index.artists }

    fun asIndexedMap(): Map<String, List<Artist>> =
        indices.associate { index: Index -> Pair(index.name, index.artists) }
}
