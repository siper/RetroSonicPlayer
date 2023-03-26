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
package ru.stersh.retrosonic.glide

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import code.name.monkey.appthemehelper.util.TintHelper
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.annotation.GlideType
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.MediaStoreSignature
import ru.stersh.retrosonic.App.Companion.getContext
import ru.stersh.retrosonic.Constants.USER_BANNER
import ru.stersh.retrosonic.Constants.USER_PROFILE
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.extensions.accentColor
import ru.stersh.retrosonic.glide.artistimage.ArtistImage
import ru.stersh.retrosonic.glide.palette.BitmapPaletteWrapper
import ru.stersh.retrosonic.model.Artist
import ru.stersh.retrosonic.model.Song
import ru.stersh.retrosonic.util.ArtistSignatureUtil
import ru.stersh.retrosonic.util.CustomArtistImageUtil.Companion.getFile
import ru.stersh.retrosonic.util.CustomArtistImageUtil.Companion.getInstance
import java.io.File

@GlideExtension
object RetroGlideExtension {

    private const val DEFAULT_ARTIST_IMAGE =
        R.drawable.default_artist_art
    private const val DEFAULT_SONG_IMAGE: Int = R.drawable.default_audio_art
    private const val DEFAULT_ALBUM_IMAGE = R.drawable.default_album_art

    private val DEFAULT_DISK_CACHE_STRATEGY_ARTIST = DiskCacheStrategy.RESOURCE
    private val DEFAULT_DISK_CACHE_STRATEGY = DiskCacheStrategy.NONE

    private const val DEFAULT_ANIMATION = android.R.anim.fade_in

    @JvmStatic
    @GlideType(BitmapPaletteWrapper::class)
    fun asBitmapPalette(requestBuilder: RequestBuilder<BitmapPaletteWrapper>): RequestBuilder<BitmapPaletteWrapper> {
        return requestBuilder
    }

    fun getArtistModel(artist: Artist): Any {
        return getArtistModel(
            artist,
            getInstance(getContext()).hasCustomArtistImage(artist),
            false,
        )
    }

    fun getArtistModel(artist: Artist, forceDownload: Boolean): Any {
        return getArtistModel(
            artist,
            getInstance(getContext()).hasCustomArtistImage(artist),
            forceDownload,
        )
    }

    private fun getArtistModel(
        artist: Artist,
        hasCustomImage: Boolean,
        forceDownload: Boolean,
    ): Any {
        return if (!hasCustomImage) {
            ArtistImage(artist)
        } else {
            getFile(artist)
        }
    }

    @JvmStatic
    @GlideOption
    fun artistImageOptions(
        baseRequestOptions: BaseRequestOptions<*>,
        artist: Artist,
    ): BaseRequestOptions<*> {
        return baseRequestOptions
            .diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY_ARTIST)
            .priority(Priority.LOW)
            .error(getDrawable(DEFAULT_ARTIST_IMAGE))
            .placeholder(getDrawable(DEFAULT_ARTIST_IMAGE))
            .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
            .signature(createSignature(artist))
    }

    @JvmStatic
    @GlideOption
    fun songCoverOptions(
        baseRequestOptions: BaseRequestOptions<*>,
        song: Song,
    ): BaseRequestOptions<*> {
        return baseRequestOptions.diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .error(getDrawable(DEFAULT_SONG_IMAGE))
            .placeholder(getDrawable(DEFAULT_SONG_IMAGE))
            .signature(createSignature(song))
    }

    @JvmStatic
    @GlideOption
    fun simpleSongCoverOptions(
        baseRequestOptions: BaseRequestOptions<*>,
        song: Song,
    ): BaseRequestOptions<*> {
        return baseRequestOptions.diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .signature(createSignature(song))
    }

    @JvmStatic
    @GlideOption
    fun albumCoverOptions(
        baseRequestOptions: BaseRequestOptions<*>,
        song: Song,
    ): BaseRequestOptions<*> {
        return baseRequestOptions.diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .error(ContextCompat.getDrawable(getContext(), DEFAULT_ALBUM_IMAGE))
            .placeholder(ContextCompat.getDrawable(getContext(), DEFAULT_ALBUM_IMAGE))
            .signature(createSignature(song))
    }

    @JvmStatic
    @GlideOption
    fun userProfileOptions(
        baseRequestOptions: BaseRequestOptions<*>,
        file: File,
        context: Context,
    ): BaseRequestOptions<*> {
        return baseRequestOptions.diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .error(getErrorUserProfile(context))
            .signature(createSignature(file))
    }

    @JvmStatic
    @GlideOption
    fun playlistOptions(
        baseRequestOptions: BaseRequestOptions<*>,
    ): BaseRequestOptions<*> {
        return baseRequestOptions.diskCacheStrategy(DEFAULT_DISK_CACHE_STRATEGY)
            .error(getDrawable(DEFAULT_ALBUM_IMAGE))
    }

    private fun createSignature(song: Song): Key {
        return MediaStoreSignature("", song.dateModified, 0)
    }

    private fun createSignature(file: File): Key {
        return MediaStoreSignature("", file.lastModified(), 0)
    }

    private fun createSignature(artist: Artist): Key {
        return ArtistSignatureUtil.getInstance(getContext())
            .getArtistSignature(artist.name)
    }

    fun getUserModel(): File {
        val dir = getContext().filesDir
        return File(dir, USER_PROFILE)
    }

    fun getBannerModel(): File {
        val dir = getContext().filesDir
        return File(dir, USER_BANNER)
    }

    private fun getErrorUserProfile(context: Context): Drawable {
        return TintHelper.createTintedDrawable(
            context,
            R.drawable.ic_account,
            context.accentColor(),
        )
    }

    fun <TranscodeType> getDefaultTransition(): GenericTransitionOptions<TranscodeType> {
        return GenericTransitionOptions<TranscodeType>().transition(DEFAULT_ANIMATION)
    }

    fun getDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(getContext(), id)
    }
}

// https://github.com/bumptech/glide/issues/527#issuecomment-148840717
fun GlideRequest<Drawable>.crossfadeListener(): GlideRequest<Drawable> {
    return listener(object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean,
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean,
        ): Boolean {
            return if (isFirstResource) {
                false // thumbnail was not shown, do as usual
            } else {
                DrawableCrossFadeFactory.Builder()
                    .setCrossFadeEnabled(true).build()
                    .build(dataSource, isFirstResource)
                    .transition(resource, target as Transition.ViewAdapter)
            }
        }
    })
}
