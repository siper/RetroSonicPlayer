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
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoader.LoadData
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey

class PlaylistPreviewLoader(val context: Context) : ModelLoader<PlaylistPreview, Bitmap> {
    override fun buildLoadData(
        model: PlaylistPreview,
        width: Int,
        height: Int,
        options: Options,
    ): LoadData<Bitmap> {
        return LoadData(
            ObjectKey(model),
            PlaylistPreviewFetcher(context, model),
        )
    }

    override fun handles(model: PlaylistPreview): Boolean {
        return true
    }

    class Factory(val context: Context) : ModelLoaderFactory<PlaylistPreview, Bitmap> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<PlaylistPreview, Bitmap> {
            return PlaylistPreviewLoader(context)
        }

        override fun teardown() {}
    }
}
