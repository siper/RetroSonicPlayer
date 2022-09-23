/*
 * Copyright (c) 2020 Hemanth Savarla.
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
package code.name.monkey.retromusic.appwidgets

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.graphics.drawable.toBitmap
import code.name.monkey.appthemehelper.util.MaterialValueHelper
import code.name.monkey.appthemehelper.util.VersionUtils
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.activities.MainActivity
import code.name.monkey.retromusic.appwidgets.base.BaseAppWidget
import code.name.monkey.retromusic.extensions.getTintedDrawable
import code.name.monkey.retromusic.glide.palette.BitmapPaletteWrapper
import code.name.monkey.retromusic.service.MusicService
import code.name.monkey.retromusic.service.MusicService.Companion.ACTION_TOGGLE_PAUSE
import code.name.monkey.retromusic.service.MusicService.Companion.TOGGLE_FAVORITE
import code.name.monkey.retromusic.util.PreferenceUtil
import code.name.monkey.retromusic.util.RetroUtil
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class AppWidgetCircle : BaseAppWidget() {
    private var target: Target<BitmapPaletteWrapper>? = null // for cancellation

    /**
     * Initialize given widgets to default state, where we launch Music on default click and hide
     * actions if service not running.
     */
    override fun defaultAppWidget(context: Context, appWidgetIds: IntArray) {
        val appWidgetView = RemoteViews(context.packageName, R.layout.app_widget_circle)

        appWidgetView.setImageViewResource(R.id.image, R.drawable.default_audio_art)
        val secondaryColor = MaterialValueHelper.getSecondaryTextColor(context, true)
        appWidgetView.setImageViewBitmap(
            R.id.button_toggle_play_pause,
            context.getTintedDrawable(
                R.drawable.ic_play_arrow,
                secondaryColor
            ).toBitmap()
        )

        linkButtons(context, appWidgetView)
        pushUpdate(context, appWidgetIds, appWidgetView)
    }

    /**
     * Update all active widget instances by pushing changes
     */
    override fun performUpdate(service: MusicService, appWidgetIds: IntArray?) {
        val appWidgetView = RemoteViews(service.packageName, R.layout.app_widget_circle)

        val isPlaying = service.isPlaying
        val song = service.currentSongId

        // Set correct drawable for pause state
        val playPauseRes =
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
        appWidgetView.setImageViewBitmap(
            R.id.button_toggle_play_pause,
            service.getTintedDrawable(
                playPauseRes,
                MaterialValueHelper.getSecondaryTextColor(service, true)
            ).toBitmap()
        )
        val isFavorite = runBlocking(Dispatchers.IO) {
            // TODO: fix favorite
//            return@runBlocking MusicUtil.isFavorite(song)
            false
        }
        val favoriteRes =
            if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        appWidgetView.setImageViewBitmap(
            R.id.button_toggle_favorite,
            service.getTintedDrawable(
                favoriteRes,
                MaterialValueHelper.getSecondaryTextColor(service, true)
            ).toBitmap()
        )

        // Link actions buttons to intents
        linkButtons(service, appWidgetView)

        if (imageSize == 0) {
            val p = RetroUtil.getScreenSize(service)
            imageSize = p.x.coerceAtMost(p.y)
        }
 // TODO: fix widget coverart load
        // Load the album cover async and push the update on completion
//        service.runOnUiThread {
//            if (target != null) {
//                Glide.with(service).clear(target)
//            }
//            target = GlideApp.with(service).asBitmapPalette().songCoverOptions(song)
//                .load(RetroGlideExtension.getSongModel(song))
//                .apply(RequestOptions.circleCropTransform())
//                .into(object : CustomTarget<BitmapPaletteWrapper>(imageSize, imageSize) {
//                    override fun onResourceReady(
//                        resource: BitmapPaletteWrapper,
//                        transition: Transition<in BitmapPaletteWrapper>?,
//                    ) {
//                        val palette = resource.palette
//                        update(
//                            resource.bitmap, palette.getVibrantColor(
//                                palette.getMutedColor(
//                                    MaterialValueHelper.getSecondaryTextColor(
//                                        service, true
//                                    )
//                                )
//                            )
//                        )
//                    }
//
//                    override fun onLoadFailed(errorDrawable: Drawable?) {
//                        super.onLoadFailed(errorDrawable)
//                        update(null, MaterialValueHelper.getSecondaryTextColor(service, true))
//                    }
//
//                    private fun update(bitmap: Bitmap?, color: Int) {
//                        // Set correct drawable for pause state
//                        appWidgetView.setImageViewBitmap(
//                            R.id.button_toggle_play_pause,
//                            service.getTintedDrawable(
//                                playPauseRes, color
//                            ).toBitmap()
//                        )
//
//                        // Set favorite button drawables
//                        appWidgetView.setImageViewBitmap(
//                            R.id.button_toggle_favorite,
//                            service.getTintedDrawable(
//                                favoriteRes, color
//                            ).toBitmap()
//                        )
//                        if (bitmap != null) {
//                            appWidgetView.setImageViewBitmap(R.id.image, bitmap)
//                        }
//
//                        pushUpdate(service, appWidgetIds, appWidgetView)
//                    }
//
//                    override fun onLoadCleared(placeholder: Drawable?) {}
//                })
//        }
    }

    /**
     * Link up various button actions using [PendingIntent].
     */
    private fun linkButtons(context: Context, views: RemoteViews) {
        val action = Intent(context, MainActivity::class.java)
            .putExtra(
                MainActivity.EXPAND_PANEL,
                PreferenceUtil.isExpandPanel
            )

        val serviceName = ComponentName(context, MusicService::class.java)

        // Home
        action.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        var pendingIntent =
            PendingIntent.getActivity(
                context, 0, action, if (VersionUtils.hasMarshmallow())
                    PendingIntent.FLAG_IMMUTABLE
                else 0
            )
        views.setOnClickPendingIntent(R.id.image, pendingIntent)
        // Favorite track
        pendingIntent = buildPendingIntent(context, TOGGLE_FAVORITE, serviceName)
        views.setOnClickPendingIntent(R.id.button_toggle_favorite, pendingIntent)

        // Play and pause
        pendingIntent = buildPendingIntent(context, ACTION_TOGGLE_PAUSE, serviceName)
        views.setOnClickPendingIntent(R.id.button_toggle_play_pause, pendingIntent)
    }

    companion object {

        const val NAME = "app_widget_circle"

        private var mInstance: AppWidgetCircle? = null
        private var imageSize = 0

        val instance: AppWidgetCircle
            @Synchronized get() {
                if (mInstance == null) {
                    mInstance = AppWidgetCircle()
                }
                return mInstance!!
            }
    }
}
