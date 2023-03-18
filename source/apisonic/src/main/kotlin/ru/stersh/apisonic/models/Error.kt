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
internal data class ErrorResponse(
    @Json(name = "subsonic-response") val data: ErrorResponseBody,
)

@JsonClass(generateAdapter = true)
internal data class ErrorResponseBody(
    @Json(name = "status") val status: String,
    @Json(name = "version") val version: String,
    @Json(name = "error") val error: Error,
)

@JsonClass(generateAdapter = true)
internal data class Error(
    @Json(name = "code") val code: Int,
    @Json(name = "message") val message: String,
)
