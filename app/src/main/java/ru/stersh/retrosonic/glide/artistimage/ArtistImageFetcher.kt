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
package ru.stersh.retrosonic.glide.artistimage

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.stersh.retrosonic.model.Data
import ru.stersh.retrosonic.model.DeezerResponse
import ru.stersh.retrosonic.network.DeezerService
import ru.stersh.retrosonic.util.MusicUtil
import ru.stersh.retrosonic.util.PreferenceUtil
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class ArtistImageFetcher(
    private val context: Context,
    private val deezerService: DeezerService,
    val model: ArtistImage,
    private val okhttp: OkHttpClient,
) : DataFetcher<InputStream> {

    private var streamFetcher: OkHttpStreamFetcher? = null
    private var response: Call<DeezerResponse>? = null
    private var isCancelled: Boolean = false

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        try {
            if (!MusicUtil.isArtistNameUnknown(model.artist.name) &&
                PreferenceUtil.isAllowedToDownloadMetadata(context)
            ) {
                val artists = model.artist.name.split(",", "&")
                response = deezerService.getArtistImage(artists[0])
                response?.enqueue(object : Callback<DeezerResponse> {
                    override fun onResponse(
                        call: Call<DeezerResponse>,
                        response: Response<DeezerResponse>,
                    ) {
                        if (!response.isSuccessful) {
                            throw IOException("Request failed with code: " + response.code())
                        }

                        if (isCancelled) {
                            callback.onDataReady(null)
                            return
                        }

                        try {
                            val deezerResponse = response.body()
                            val imageUrl =
                                deezerResponse?.data?.get(0)?.let { getHighestQuality(it) }
                            // Fragile way to detect a place holder image returned from Deezer:
                            // ex: "https://e-cdns-images.dzcdn.net/images/artist//250x250-000000-80-0-0.jpg"
                            // the double slash implies no artist identified
                            val placeHolder = imageUrl?.contains("/images/artist//") ?: false
                            if (!placeHolder) {
                                streamFetcher = OkHttpStreamFetcher(okhttp, GlideUrl(imageUrl))
                                streamFetcher?.loadData(priority, callback)
                            } else {
                                callback.onDataReady(getFallbackAlbumImage())
                            }
                        } catch (e: Exception) {
                            callback.onDataReady(getFallbackAlbumImage())
                        }
                    }

                    override fun onFailure(call: Call<DeezerResponse>, t: Throwable) {
                        callback.onDataReady(getFallbackAlbumImage())
                    }
                })
            } else {
                callback.onDataReady(null)
            }
        } catch (e: Exception) {
            callback.onLoadFailed(e)
        }
    }

    private fun getFallbackAlbumImage(): InputStream? {
        model.artist.safeGetFirstAlbum().id.let { id ->
            return if (id != -1L) {
                val imageUri = MusicUtil.getMediaStoreAlbumCoverUri(model.artist.safeGetFirstAlbum().id)
                try {
                    context.contentResolver.openInputStream(imageUri)
                } catch (e: FileNotFoundException) {
                    null
                } catch (e: UnsupportedOperationException) {
                    null
                }
            } else {
                null
            }
        }
    }

    private fun getHighestQuality(imageUrl: Data): String {
        return when {
            imageUrl.pictureXl.isNotEmpty() -> imageUrl.pictureXl
            imageUrl.pictureBig.isNotEmpty() -> imageUrl.pictureBig
            imageUrl.pictureMedium.isNotEmpty() -> imageUrl.pictureMedium
            imageUrl.pictureSmall.isNotEmpty() -> imageUrl.pictureSmall
            imageUrl.picture.isNotEmpty() -> imageUrl.picture
            else -> ""
        }
    }

    override fun cleanup() {
        streamFetcher?.cleanup()
    }

    override fun cancel() {
        isCancelled = true
        response?.cancel()
        streamFetcher?.cancel()
    }
}
