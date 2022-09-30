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
import android.view.View
import android.widget.RemoteViews
import androidx.core.graphics.drawable.toBitmap
import code.name.monkey.appthemehelper.util.MaterialValueHelper
import code.name.monkey.appthemehelper.util.VersionUtils
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.feature.main.presentation.MainActivity
import code.name.monkey.retromusic.appwidgets.base.BaseAppWidget
import code.name.monkey.retromusic.extensions.getTintedDrawable
import code.name.monkey.retromusic.glide.palette.BitmapPaletteWrapper
import ru.stersh.retrosonic.player.android.MusicService
import code.name.monkey.retromusic.util.PreferenceUtil
import com.bumptech.glide.request.target.Target

class AppWidgetCard : BaseAppWidget() {
    private var target: Target<BitmapPaletteWrapper>? = null // for cancellation

    /**
     * Initialize given widgets to default state, where we launch Music on default click and hide
     * actions if service not running.
     */
    override fun defaultAppWidget(context: Context, appWidgetIds: IntArray) {
        val appWidgetView = RemoteViews(context.packageName, R.layout.app_widget_card)

        appWidgetView.setViewVisibility(R.id.media_titles, View.INVISIBLE)
        appWidgetView.setImageViewResource(R.id.image, R.drawable.default_audio_art)
        val secondaryColor = MaterialValueHelper.getSecondaryTextColor(context, true)
        appWidgetView.setImageViewBitmap(
            R.id.button_next,
            context.getTintedDrawable(
                R.drawable.ic_skip_next,
                secondaryColor
            ).toBitmap()
        )
        appWidgetView.setImageViewBitmap(
            R.id.button_prev,
            context.getTintedDrawable(
                R.drawable.ic_skip_previous,
                secondaryColor
            ).toBitmap()
        )
        appWidgetView.setImageViewBitmap(
            R.id.button_toggle_play_pause,
            context.getTintedDrawable(
                R.drawable.ic_play_arrow_white_32dp,
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
        val appWidgetView = RemoteViews(service.packageName, R.layout.app_widget_card)
//
//        val isPlaying = service.isPlaying
//        val song = service.currentSongId

        // Set the titles and artwork
//        TODO: Fix widget update
//        if (song.title.isEmpty() && song.artistName.isEmpty()) {
//            appWidgetView.setViewVisibility(R.id.media_titles, View.INVISIBLE)
//        } else {
//            appWidgetView.setViewVisibility(R.id.media_titles, View.VISIBLE)
//            appWidgetView.setTextViewText(R.id.title, song.title)
//            appWidgetView.setTextViewText(R.id.text, getSongArtistAndAlbum(song))
//        }

        // Set correct drawable for pause state
        val playPauseRes =
            if (true) R.drawable.ic_pause else R.drawable.ic_play_arrow_white_32dp
        appWidgetView.setImageViewBitmap(
            R.id.button_toggle_play_pause,
            service.getTintedDrawable(
                playPauseRes,
                MaterialValueHelper.getSecondaryTextColor(service, true)
            ).toBitmap()
        )

        // Set prev/next button drawables
        appWidgetView.setImageViewBitmap(
            R.id.button_next,
            service.getTintedDrawable(
                R.drawable.ic_skip_next,
                MaterialValueHelper.getSecondaryTextColor(service, true)
            ).toBitmap()
        )
        appWidgetView.setImageViewBitmap(
            R.id.button_prev,
            service.getTintedDrawable(
                R.drawable.ic_skip_previous,
                MaterialValueHelper.getSecondaryTextColor(service, true)
            ).toBitmap()
        )

        // Link actions buttons to intents
        linkButtons(service, appWidgetView)

        if (imageSize == 0) {
            imageSize =
                service.resources.getDimensionPixelSize(R.dimen.app_widget_card_image_size)
        }
        if (cardRadius == 0f) {
            cardRadius =
                service.resources.getDimension(R.dimen.app_widget_card_radius)
        }

        // Load the album cover async and push the update on completion
        // TODO: Fix cover art widget update
//        service.runOnUiThread {
//            if (target != null) {
//                Glide.with(service).clear(target)
//            }
//            target = GlideApp.with(service).asBitmapPalette().songCoverOptions(song)
//                .load(RetroGlideExtension.getSongModel(song))
//                .centerCrop()
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
//                    override fun onLoadCleared(placeholder: Drawable?) {}
//
//                    private fun update(bitmap: Bitmap?, color: Int) {
//                        // Set correct drawable for pause state
//                        appWidgetView.setImageViewBitmap(
//                            R.id.button_toggle_play_pause,
//                            service.getTintedDrawable(playPauseRes, color).toBitmap()
//                        )
//
//                        // Set prev/next button drawables
//                        appWidgetView.setImageViewBitmap(
//                            R.id.button_next,
//                            service.getTintedDrawable(R.drawable.ic_skip_next, color).toBitmap()
//                        )
//                        appWidgetView.setImageViewBitmap(
//                            R.id.button_prev,
//                            service.getTintedDrawable(R.drawable.ic_skip_previous, color).toBitmap()
//                        )
//
//                        val image = getAlbumArtDrawable(service, bitmap)
//                        val roundedBitmap = createRoundedBitmap(
//                            image, imageSize, imageSize, cardRadius, 0F, cardRadius, 0F
//                        )
//                        appWidgetView.setImageViewBitmap(R.id.image, roundedBitmap)
//
//                        pushUpdate(service, appWidgetIds, appWidgetView)
//                    }
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
//        views.setOnClickPendingIntent(R.id.image, pendingIntent)
//        views.setOnClickPendingIntent(R.id.media_titles, pendingIntent)
//
//        // Previous track
//        pendingIntent = buildPendingIntent(context, ACTION_REWIND, serviceName)
//        views.setOnClickPendingIntent(R.id.button_prev, pendingIntent)
//
//        // Play and pause
//        pendingIntent = buildPendingIntent(context, ACTION_TOGGLE_PAUSE, serviceName)
//        views.setOnClickPendingIntent(R.id.button_toggle_play_pause, pendingIntent)
//
//        // Next track
//        pendingIntent = buildPendingIntent(context, ACTION_SKIP, serviceName)
//        views.setOnClickPendingIntent(R.id.button_next, pendingIntent)
    }

    companion object {

        const val NAME = "app_widget_card"

        private var mInstance: AppWidgetCard? = null
        private var imageSize = 0
        private var cardRadius = 0f

        val instance: AppWidgetCard
            @Synchronized get() {
                if (mInstance == null) {
                    mInstance = AppWidgetCard()
                }
                return mInstance!!
            }
    }
}
