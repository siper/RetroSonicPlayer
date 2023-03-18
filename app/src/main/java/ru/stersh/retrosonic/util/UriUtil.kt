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
package ru.stersh.retrosonic.util

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import ru.stersh.retrosonic.Constants

object UriUtil {
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getUriFromPath(context: Context, path: String): Uri {
        val uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val proj = arrayOf(MediaStore.Files.FileColumns._ID)
        context.contentResolver.query(
            uri,
            proj,
            Constants.DATA + "=?",
            arrayOf(path),
            null,
        )?.use { cursor ->
            if (cursor.count != 0) {
                cursor.moveToFirst()
                return ContentUris.withAppendedId(uri, cursor.getLong(0))
            }
        }
        return Uri.EMPTY
    }
}
