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

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.Status
import ru.stersh.retrosonic.util.MusicUtil

const val SERVER_PORT = 9090

class RetroWebServer(val context: Context) : NanoHTTPD(SERVER_PORT) {
    companion object {
        private const val MIME_TYPE_IMAGE = "image/jpg"
        const val MIME_TYPE_AUDIO = "audio/mp3"

        const val PART_COVER_ART = "coverart"
        const val PART_SONG = "song"
        const val PARAM_ID = "id"
    }

    override fun serve(session: IHTTPSession?): Response {
        if (session?.uri?.contains(PART_COVER_ART) == true) {
            val albumId = session.parameters?.get(PARAM_ID)?.get(0) ?: return errorResponse()
            val albumArtUri = MusicUtil.getMediaStoreAlbumCoverUri(albumId.toLong())
            val fis: InputStream?
            try {
                fis = context.contentResolver.openInputStream(albumArtUri)
            } catch (e: FileNotFoundException) {
                return errorResponse()
            }
            return newChunkedResponse(Status.OK, MIME_TYPE_IMAGE, fis)
        } else if (session?.uri?.contains(PART_SONG) == true) {
            val songId = session.parameters?.get(PARAM_ID)?.get(0) ?: return errorResponse()
            val songUri = MusicUtil.getSongFileUri(songId.toLong())
            val songPath = MusicUtil.getSongFilePath(context, songUri)
            val song = File(songPath)
            return serveFile(session.headers!!, song, MIME_TYPE_AUDIO)
        }
        return newFixedLengthResponse(Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found")
    }

    private fun serveFile(
        header: MutableMap<String, String>,
        file: File,
        mime: String,
    ): Response {
        var res: Response
        try {
            // Support (simple) skipping:
            var startFrom: Long = 0
            var endAt: Long = -1
            // The value of header range will be bytes=0-1024 something like this
            // We get the value of from Bytes i.e. startFrom and toBytes i.e. endAt below
            var range = header["range"]
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length)
                    val minus = range.indexOf('-')
                    try {
                        if (minus > 0) {
                            startFrom = range
                                .substring(0, minus).toLong()
                            endAt = range.substring(minus + 1).toLong()
                        }
                    } catch (ignored: NumberFormatException) {
                    }
                }
            }

            // Chunked Response is used when serving audio file
            // Change return code and add Content-Range header when skipping is
            // requested
            val fileLen = file.length()
            if (range != null && startFrom >= 0) {
                if (startFrom >= fileLen) {
                    res = newFixedLengthResponse(
                        Status.RANGE_NOT_SATISFIABLE,
                        MIME_PLAINTEXT,
                        "",
                    )
                    res.addHeader("Content-Range", "bytes 0-0/$fileLen")
                } else {
                    if (endAt < 0) {
                        endAt = fileLen - 1
                    }
                    var newLen = endAt - startFrom + 1
                    if (newLen < 0) {
                        newLen = 0
                    }
                    val dataLen = newLen
                    val fis: FileInputStream = object : FileInputStream(file) {
                        @Throws(IOException::class)
                        override fun available(): Int {
                            return dataLen.toInt()
                        }
                    }
                    fis.skip(startFrom)
                    res = newChunkedResponse(
                        Status.PARTIAL_CONTENT,
                        mime,
                        fis,
                    )
                    res.addHeader("Content-Length", "" + dataLen)
                    res.addHeader(
                        "Content-Range",
                        "bytes " + startFrom + "-" +
                            endAt + "/" + fileLen,
                    )
                }
            } else {
                res = newFixedLengthResponse(
                    Status.OK,
                    mime,
                    file.inputStream(),
                    file.length(),
                )
                res.addHeader("Accept-Ranges", "bytes")
                res.addHeader("Content-Length", "" + fileLen)
            }
        } catch (ioe: IOException) {
            res = newFixedLengthResponse(
                Status.FORBIDDEN,
                MIME_PLAINTEXT,
                "FORBIDDEN: Reading file failed.",
            )
        }
        return res
    }

    private fun errorResponse(message: String = "Error Occurred"): Response {
        return newFixedLengthResponse(Status.INTERNAL_ERROR, MIME_PLAINTEXT, message)
    }
}
