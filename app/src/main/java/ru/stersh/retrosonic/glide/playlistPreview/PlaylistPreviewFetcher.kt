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
package ru.stersh.retrosonic.glide.playlistPreview

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.util.AutoGeneratedPlaylistBitmap
import java.util.concurrent.Executors

class PlaylistPreviewFetcher(val context: Context, private val playlistPreview: PlaylistPreview) :
    DataFetcher<Bitmap>, CoroutineScope by GlideScope() {
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Bitmap>) {
        launch {
            try {
                val bitmap =
                    AutoGeneratedPlaylistBitmap.getBitmap(
                        context,
                        playlistPreview.songs.shuffled(),
                    )
                callback.onDataReady(bitmap)
            } catch (e: Exception) {
                callback.onLoadFailed(e)
            }
        }
    }

    override fun cleanup() {}

    override fun cancel() {
        cancel(null)
    }

    override fun getDataClass(): Class<Bitmap> {
        return Bitmap::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.LOCAL
    }
}

private val glideDispatcher: CoroutineDispatcher by lazy {
    Executors.newFixedThreadPool(4).asCoroutineDispatcher()
}

@Suppress("FunctionName")
internal fun GlideScope(): CoroutineScope = CoroutineScope(SupervisorJob() + glideDispatcher)
