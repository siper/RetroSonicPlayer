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
package ru.stersh.retrosonic.cast

import androidx.core.net.toUri
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaInfo.STREAM_TYPE_BUFFERED
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.common.images.WebImage
import ru.stersh.retrosonic.cast.RetroWebServer.Companion.MIME_TYPE_AUDIO
import ru.stersh.retrosonic.cast.RetroWebServer.Companion.PART_COVER_ART
import ru.stersh.retrosonic.cast.RetroWebServer.Companion.PART_SONG
import ru.stersh.retrosonic.model.Song
import ru.stersh.retrosonic.util.RetroUtil
import java.net.MalformedURLException
import java.net.URL

object CastHelper {

    private const val CAST_MUSIC_METADATA_ID = "metadata_id"
    private const val CAST_MUSIC_METADATA_ALBUM_ID = "metadata_album_id"
    private const val CAST_URL_PROTOCOL = "http"

    fun Song.toMediaInfo(): MediaInfo? {
        val song = this
        val baseUrl: URL
        try {
            baseUrl = URL(CAST_URL_PROTOCOL, RetroUtil.getIpAddress(true), SERVER_PORT, "")
        } catch (e: MalformedURLException) {
            return null
        }

        val songUrl = "$baseUrl/$PART_SONG?id=${song.id}"
        val albumArtUrl = "$baseUrl/$PART_COVER_ART?id=${song.albumId}"
        val musicMetadata = MediaMetadata(MEDIA_TYPE_MUSIC_TRACK).apply {
            putInt(CAST_MUSIC_METADATA_ID, song.id.toInt())
            putInt(CAST_MUSIC_METADATA_ALBUM_ID, song.albumId.toInt())
            putString(KEY_TITLE, song.title)
            putString(KEY_ARTIST, song.artistName)
            putString(KEY_ALBUM_TITLE, song.albumName)
            putInt(KEY_TRACK_NUMBER, song.trackNumber)
            addImage(WebImage(albumArtUrl.toUri()))
        }
        return MediaInfo.Builder(songUrl).apply {
            setStreamType(STREAM_TYPE_BUFFERED)
            setContentType(MIME_TYPE_AUDIO)
            setMetadata(musicMetadata)
            setStreamDuration(song.duration)
        }.build()
    }
}
