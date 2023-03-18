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

interface SubsonicResponse

@JsonClass(generateAdapter = true)
data class Response<T : SubsonicResponse>(
    @Json(name = "subsonic-response") val subsonicResponse: T,
)

@JsonClass(generateAdapter = true)
data class EmptyResponse(
    @Json(name = "status") val status: String,
    @Json(name = "version") val version: String,
) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class LicenseResponse(
    @Json(name = "license") val license: License,
) : SubsonicResponse

@JsonClass(generateAdapter = true)
data class License(
    @Json(name = "email") val email: String,
    @Json(name = "trialExpires") val trialExpires: String,
    @Json(name = "valid") val valid: Boolean,
)
