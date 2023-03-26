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

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.edit
import androidx.core.content.getSystemService
import androidx.core.content.res.use
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import code.name.monkey.appthemehelper.util.VersionUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import ru.stersh.retrosonic.ADAPTIVE_COLOR_APP
import ru.stersh.retrosonic.ALBUM_ARTISTS_ONLY
import ru.stersh.retrosonic.ALBUM_ART_ON_LOCK_SCREEN
import ru.stersh.retrosonic.ALBUM_COVER_STYLE
import ru.stersh.retrosonic.ALBUM_COVER_TRANSFORM
import ru.stersh.retrosonic.ALBUM_DETAIL_SONG_SORT_ORDER
import ru.stersh.retrosonic.ALBUM_GRID_SIZE
import ru.stersh.retrosonic.ALBUM_GRID_SIZE_LAND
import ru.stersh.retrosonic.ALBUM_GRID_STYLE
import ru.stersh.retrosonic.ALBUM_SONG_SORT_ORDER
import ru.stersh.retrosonic.ALBUM_SORT_ORDER
import ru.stersh.retrosonic.APPBAR_MODE
import ru.stersh.retrosonic.ARTIST_ALBUM_SORT_ORDER
import ru.stersh.retrosonic.ARTIST_DETAIL_SONG_SORT_ORDER
import ru.stersh.retrosonic.ARTIST_GRID_SIZE
import ru.stersh.retrosonic.ARTIST_GRID_SIZE_LAND
import ru.stersh.retrosonic.ARTIST_GRID_STYLE
import ru.stersh.retrosonic.ARTIST_SONG_SORT_ORDER
import ru.stersh.retrosonic.ARTIST_SORT_ORDER
import ru.stersh.retrosonic.AUDIO_DUCKING
import ru.stersh.retrosonic.AUDIO_FADE_DURATION
import ru.stersh.retrosonic.AUTO_DOWNLOAD_IMAGES_POLICY
import ru.stersh.retrosonic.App
import ru.stersh.retrosonic.BLACK_THEME
import ru.stersh.retrosonic.BLUETOOTH_PLAYBACK
import ru.stersh.retrosonic.BLURRED_ALBUM_ART
import ru.stersh.retrosonic.CAROUSEL_EFFECT
import ru.stersh.retrosonic.CIRCLE_PLAY_BUTTON
import ru.stersh.retrosonic.CLASSIC_NOTIFICATION
import ru.stersh.retrosonic.COLORED_APP_SHORTCUTS
import ru.stersh.retrosonic.COLORED_NOTIFICATION
import ru.stersh.retrosonic.CROSS_FADE_DURATION
import ru.stersh.retrosonic.CUSTOM_FONT
import ru.stersh.retrosonic.DESATURATED_COLOR
import ru.stersh.retrosonic.EXPAND_NOW_PLAYING_PANEL
import ru.stersh.retrosonic.EXTRA_SONG_INFO
import ru.stersh.retrosonic.FILTER_SONG
import ru.stersh.retrosonic.GAP_LESS_PLAYBACK
import ru.stersh.retrosonic.GENERAL_THEME
import ru.stersh.retrosonic.GENRE_SORT_ORDER
import ru.stersh.retrosonic.HOME_ALBUM_GRID_STYLE
import ru.stersh.retrosonic.HOME_ARTIST_GRID_STYLE
import ru.stersh.retrosonic.KEEP_SCREEN_ON
import ru.stersh.retrosonic.LANGUAGE_NAME
import ru.stersh.retrosonic.LAST_ADDED_CUTOFF
import ru.stersh.retrosonic.LAST_CHANGELOG_VERSION
import ru.stersh.retrosonic.LAST_SLEEP_TIMER_VALUE
import ru.stersh.retrosonic.LAST_USED_TAB
import ru.stersh.retrosonic.LIBRARY_CATEGORIES
import ru.stersh.retrosonic.LOCK_SCREEN
import ru.stersh.retrosonic.LYRICS_OPTIONS
import ru.stersh.retrosonic.LYRICS_TYPE
import ru.stersh.retrosonic.MANAGE_AUDIO_FOCUS
import ru.stersh.retrosonic.MATERIAL_YOU
import ru.stersh.retrosonic.NEW_BLUR_AMOUNT
import ru.stersh.retrosonic.NEXT_SLEEP_TIMER_ELAPSED_REALTIME
import ru.stersh.retrosonic.PAUSE_HISTORY
import ru.stersh.retrosonic.PAUSE_ON_ZERO_VOLUME
import ru.stersh.retrosonic.PLAYBACK_PITCH
import ru.stersh.retrosonic.PLAYBACK_SPEED
import ru.stersh.retrosonic.PLAYLIST_GRID_SIZE
import ru.stersh.retrosonic.PLAYLIST_GRID_SIZE_LAND
import ru.stersh.retrosonic.PLAYLIST_SORT_ORDER
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.RECENTLY_PLAYED_CUTOFF
import ru.stersh.retrosonic.REMEMBER_LAST_TAB
import ru.stersh.retrosonic.SAF_SDCARD_URI
import ru.stersh.retrosonic.SCREEN_ON_LYRICS
import ru.stersh.retrosonic.SHOW_LYRICS
import ru.stersh.retrosonic.SLEEP_TIMER_FINISH_SONG
import ru.stersh.retrosonic.SONG_GRID_SIZE
import ru.stersh.retrosonic.SONG_GRID_SIZE_LAND
import ru.stersh.retrosonic.SONG_GRID_STYLE
import ru.stersh.retrosonic.SONG_SORT_ORDER
import ru.stersh.retrosonic.START_DIRECTORY
import ru.stersh.retrosonic.SWIPE_ANYWHERE_NOW_PLAYING
import ru.stersh.retrosonic.SWIPE_DOWN_DISMISS
import ru.stersh.retrosonic.TAB_TEXT_MODE
import ru.stersh.retrosonic.TOGGLE_ADD_CONTROLS
import ru.stersh.retrosonic.TOGGLE_FULL_SCREEN
import ru.stersh.retrosonic.TOGGLE_HEADSET
import ru.stersh.retrosonic.TOGGLE_HOME_BANNER
import ru.stersh.retrosonic.TOGGLE_SUGGESTIONS
import ru.stersh.retrosonic.TOGGLE_VOLUME
import ru.stersh.retrosonic.WALLPAPER_ACCENT
import ru.stersh.retrosonic.extensions.getIntRes
import ru.stersh.retrosonic.extensions.getStringOrDefault
import ru.stersh.retrosonic.fragments.AlbumCoverStyle
import ru.stersh.retrosonic.fragments.GridStyle
import ru.stersh.retrosonic.fragments.folder.FoldersFragment
import ru.stersh.retrosonic.helper.SortOrder
import ru.stersh.retrosonic.model.CategoryInfo
import ru.stersh.retrosonic.transform.CascadingPageTransformer
import ru.stersh.retrosonic.transform.DepthTransformation
import ru.stersh.retrosonic.transform.HingeTransformation
import ru.stersh.retrosonic.transform.HorizontalFlipTransformation
import ru.stersh.retrosonic.transform.NormalPageTransformer
import ru.stersh.retrosonic.transform.VerticalFlipTransformation
import ru.stersh.retrosonic.transform.VerticalStackTransformer
import ru.stersh.retrosonic.util.theme.ThemeMode
import ru.stersh.retrosonic.views.TopAppBarLayout
import java.io.File

object PreferenceUtil {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getContext())

    val defaultCategories = listOf(
        CategoryInfo(CategoryInfo.Category.Home, true),
        CategoryInfo(CategoryInfo.Category.Songs, true),
        CategoryInfo(CategoryInfo.Category.Albums, true),
        CategoryInfo(CategoryInfo.Category.Artists, true),
        CategoryInfo(CategoryInfo.Category.Playlists, true),
        CategoryInfo(CategoryInfo.Category.Genres, false),
        CategoryInfo(CategoryInfo.Category.Folder, false),
        CategoryInfo(CategoryInfo.Category.Search, false),
    )

    var libraryCategory: List<CategoryInfo>
        get() {
            val gson = Gson()
            val collectionType = object : TypeToken<List<CategoryInfo>>() {}.type

            val data = sharedPreferences.getStringOrDefault(
                LIBRARY_CATEGORIES,
                gson.toJson(defaultCategories, collectionType),
            )
            return try {
                Gson().fromJson(data, collectionType)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                return defaultCategories
            }
        }
        set(value) {
            val collectionType = object : TypeToken<List<CategoryInfo?>?>() {}.type
            sharedPreferences.edit {
                putString(LIBRARY_CATEGORIES, Gson().toJson(value, collectionType))
            }
        }

    fun registerOnSharedPreferenceChangedListener(
        listener: OnSharedPreferenceChangeListener,
    ) = sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

    fun unregisterOnSharedPreferenceChangedListener(
        changeListener: OnSharedPreferenceChangeListener,
    ) = sharedPreferences.unregisterOnSharedPreferenceChangeListener(changeListener)

    val baseTheme get() = sharedPreferences.getStringOrDefault(GENERAL_THEME, "auto")

    fun getGeneralThemeValue(isSystemDark: Boolean): ThemeMode {
        val themeMode: String =
            sharedPreferences.getStringOrDefault(GENERAL_THEME, "auto")
        return if (isBlackMode && isSystemDark && themeMode != "light") {
            ThemeMode.BLACK
        } else {
            if (isBlackMode && themeMode == "dark") {
                ThemeMode.BLACK
            } else {
                when (themeMode) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    "auto" -> ThemeMode.AUTO
                    else -> ThemeMode.AUTO
                }
            }
        }
    }

    val languageCode: String get() = sharedPreferences.getString(LANGUAGE_NAME, "auto") ?: "auto"

    var safSdCardUri
        get() = sharedPreferences.getStringOrDefault(SAF_SDCARD_URI, "")
        set(value) = sharedPreferences.edit {
            putString(SAF_SDCARD_URI, value)
        }

    private val autoDownloadImagesPolicy
        get() = sharedPreferences.getStringOrDefault(
            AUTO_DOWNLOAD_IMAGES_POLICY,
            "only_wifi",
        )

    var albumArtistsOnly
        get() = sharedPreferences.getBoolean(
            ALBUM_ARTISTS_ONLY,
            false,
        )
        set(value) = sharedPreferences.edit { putBoolean(ALBUM_ARTISTS_ONLY, value) }

    var albumDetailSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_DETAIL_SONG_SORT_ORDER,
            SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST,
        )
        set(value) = sharedPreferences.edit { putString(ALBUM_DETAIL_SONG_SORT_ORDER, value) }

    var artistDetailSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_DETAIL_SONG_SORT_ORDER,
            SortOrder.ArtistSongSortOrder.SONG_A_Z,
        )
        set(value) = sharedPreferences.edit { putString(ARTIST_DETAIL_SONG_SORT_ORDER, value) }

    var songSortOrder
        get() = sharedPreferences.getStringOrDefault(
            SONG_SORT_ORDER,
            SortOrder.SongSortOrder.SONG_A_Z,
        )
        set(value) = sharedPreferences.edit {
            putString(SONG_SORT_ORDER, value)
        }

    var albumSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_SORT_ORDER,
            SortOrder.AlbumSortOrder.ALBUM_A_Z,
        )
        set(value) = sharedPreferences.edit {
            putString(ALBUM_SORT_ORDER, value)
        }

    var artistSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_SORT_ORDER,
            SortOrder.ArtistSortOrder.ARTIST_A_Z,
        )
        set(value) = sharedPreferences.edit {
            putString(ARTIST_SORT_ORDER, value)
        }

    val albumSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ALBUM_SONG_SORT_ORDER,
            SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST,
        )

    val artistSongSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_SONG_SORT_ORDER,
            SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST,
        )

    val artistAlbumSortOrder
        get() = sharedPreferences.getStringOrDefault(
            ARTIST_ALBUM_SORT_ORDER,
            SortOrder.ArtistAlbumSortOrder.ALBUM_A_Z,
        )

    var playlistSortOrder
        get() = sharedPreferences.getStringOrDefault(
            PLAYLIST_SORT_ORDER,
            SortOrder.PlaylistSortOrder.PLAYLIST_A_Z,
        )
        set(value) = sharedPreferences.edit {
            putString(PLAYLIST_SORT_ORDER, value)
        }

    val genreSortOrder
        get() = sharedPreferences.getStringOrDefault(
            GENRE_SORT_ORDER,
            SortOrder.GenreSortOrder.GENRE_A_Z,
        )

    val isVolumeVisibilityMode
        get() = sharedPreferences.getBoolean(
            TOGGLE_VOLUME,
            false,
        )

    private val isBlackMode
        get() = sharedPreferences.getBoolean(
            BLACK_THEME,
            false,
        )

    val isExtraControls
        get() = sharedPreferences.getBoolean(
            TOGGLE_ADD_CONTROLS,
            false,
        )

    val isHomeBanner
        get() = sharedPreferences.getBoolean(
            TOGGLE_HOME_BANNER,
            false,
        )
    var isClassicNotification
        get() = sharedPreferences.getBoolean(CLASSIC_NOTIFICATION, false)
        set(value) = sharedPreferences.edit { putBoolean(CLASSIC_NOTIFICATION, value) }

    val isScreenOnEnabled get() = sharedPreferences.getBoolean(KEEP_SCREEN_ON, false)

    val isSongInfo get() = sharedPreferences.getBoolean(EXTRA_SONG_INFO, false)

    val isPauseOnZeroVolume get() = sharedPreferences.getBoolean(PAUSE_ON_ZERO_VOLUME, false)

    var isSleepTimerFinishMusic
        get() = sharedPreferences.getBoolean(
            SLEEP_TIMER_FINISH_SONG,
            false,
        )
        set(value) = sharedPreferences.edit {
            putBoolean(SLEEP_TIMER_FINISH_SONG, value)
        }

    val isExpandPanel get() = sharedPreferences.getBoolean(EXPAND_NOW_PLAYING_PANEL, false)

    val isHeadsetPlugged
        get() = sharedPreferences.getBoolean(
            TOGGLE_HEADSET,
            false,
        )

    val isAlbumArtOnLockScreen
        get() = sharedPreferences.getBoolean(
            ALBUM_ART_ON_LOCK_SCREEN,
            false,
        )

    val isAudioDucking
        get() = sharedPreferences.getBoolean(
            AUDIO_DUCKING,
            true,
        )

    val isBluetoothSpeaker
        get() = sharedPreferences.getBoolean(
            BLUETOOTH_PLAYBACK,
            false,
        )

    val isBlurredAlbumArt
        get() = sharedPreferences.getBoolean(
            BLURRED_ALBUM_ART,
            false,
        )

    val blurAmount get() = sharedPreferences.getInt(NEW_BLUR_AMOUNT, 25)

    val isCarouselEffect
        get() = sharedPreferences.getBoolean(
            CAROUSEL_EFFECT,
            false,
        )

    var isColoredAppShortcuts
        get() = sharedPreferences.getBoolean(
            COLORED_APP_SHORTCUTS,
            true,
        )
        set(value) = sharedPreferences.edit {
            putBoolean(COLORED_APP_SHORTCUTS, value)
        }

    var isColoredNotification
        get() = sharedPreferences.getBoolean(
            COLORED_NOTIFICATION,
            true,
        )
        set(value) = sharedPreferences.edit {
            putBoolean(COLORED_NOTIFICATION, value)
        }

    var isDesaturatedColor
        get() = sharedPreferences.getBoolean(
            DESATURATED_COLOR,
            false,
        )
        set(value) = sharedPreferences.edit {
            putBoolean(DESATURATED_COLOR, value)
        }

    val isGapLessPlayback
        get() = sharedPreferences.getBoolean(
            GAP_LESS_PLAYBACK,
            false,
        )

    val isAdaptiveColor
        get() = sharedPreferences.getBoolean(
            ADAPTIVE_COLOR_APP,
            false,
        )

    val isFullScreenMode
        get() = sharedPreferences.getBoolean(
            TOGGLE_FULL_SCREEN,
            false,
        )

    val isAudioFocusEnabled
        get() = sharedPreferences.getBoolean(
            MANAGE_AUDIO_FOCUS,
            false,
        )

    val isLockScreen get() = sharedPreferences.getBoolean(LOCK_SCREEN, false)

    @Suppress("deprecation")
    fun isAllowedToDownloadMetadata(context: Context): Boolean {
        return when (autoDownloadImagesPolicy) {
            "always" -> true
            "only_wifi" -> {
                val connectivityManager = context.getSystemService<ConnectivityManager>()
                if (VersionUtils.hasMarshmallow()) {
                    val network = connectivityManager?.activeNetwork
                    val capabilities = connectivityManager?.getNetworkCapabilities(network)
                    capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                } else {
                    val netInfo = connectivityManager?.activeNetworkInfo
                    netInfo != null && netInfo.type == ConnectivityManager.TYPE_WIFI && netInfo.isConnectedOrConnecting
                }
            }
            "never" -> false
            else -> false
        }
    }

    var lyricsOption
        get() = sharedPreferences.getInt(LYRICS_OPTIONS, 1)
        set(value) = sharedPreferences.edit {
            putInt(LYRICS_OPTIONS, value)
        }

    var songGridStyle: GridStyle
        get() {
            val id: Int = sharedPreferences.getInt(SONG_GRID_STYLE, 0)
            // We can directly use "first" kotlin extension function here but
            // there maybe layout id stored in this so to avoid a crash we use
            // "firstOrNull"
            return GridStyle.values().firstOrNull { gridStyle ->
                gridStyle.id == id
            } ?: GridStyle.Grid
        }
        set(value) = sharedPreferences.edit {
            putInt(SONG_GRID_STYLE, value.id)
        }

    var albumGridStyle: GridStyle
        get() {
            val id: Int = sharedPreferences.getInt(ALBUM_GRID_STYLE, 0)
            return GridStyle.values().firstOrNull { gridStyle ->
                gridStyle.id == id
            } ?: GridStyle.Grid
        }
        set(value) = sharedPreferences.edit {
            putInt(ALBUM_GRID_STYLE, value.id)
        }

    var artistGridStyle: GridStyle
        get() {
            val id: Int = sharedPreferences.getInt(ARTIST_GRID_STYLE, 3)
            return GridStyle.values().firstOrNull { gridStyle ->
                gridStyle.id == id
            } ?: GridStyle.Circular
        }
        set(value) = sharedPreferences.edit {
            putInt(ARTIST_GRID_STYLE, value.id)
        }

    val filterLength get() = sharedPreferences.getInt(FILTER_SONG, 20)

    var lastVersion
        // This was stored as an integer before now it's a long, so avoid a ClassCastException
        get() = try {
            sharedPreferences.getLong(LAST_CHANGELOG_VERSION, 0)
        } catch (e: ClassCastException) {
            sharedPreferences.edit { remove(LAST_CHANGELOG_VERSION) }
            0
        }
        set(value) = sharedPreferences.edit {
            putLong(LAST_CHANGELOG_VERSION, value)
        }

    var lastSleepTimerValue
        get() = sharedPreferences.getInt(
            LAST_SLEEP_TIMER_VALUE,
            30,
        )
        set(value) = sharedPreferences.edit {
            putInt(LAST_SLEEP_TIMER_VALUE, value)
        }

    var nextSleepTimerElapsedRealTime
        get() = sharedPreferences.getInt(
            NEXT_SLEEP_TIMER_ELAPSED_REALTIME,
            -1,
        )
        set(value) = sharedPreferences.edit {
            putInt(NEXT_SLEEP_TIMER_ELAPSED_REALTIME, value)
        }

    fun themeResFromPrefValue(themePrefValue: String): Int {
        return when (themePrefValue) {
            "light" -> R.style.Theme_RetroMusic_Light
            "dark" -> R.style.Theme_RetroMusic
            else -> R.style.Theme_RetroMusic
        }
    }

    val homeArtistGridStyle: Int
        get() {
            val position = sharedPreferences.getStringOrDefault(
                HOME_ARTIST_GRID_STYLE,
                "0",
            ).toInt()
            val layoutRes =
                App.getContext().resources.obtainTypedArray(R.array.pref_home_grid_style_layout)
                    .use {
                        it.getResourceId(position, 0)
                    }
            return if (layoutRes == 0) {
                R.layout.item_artist
            } else {
                layoutRes
            }
        }

    val homeAlbumGridStyle: Int
        get() {
            val position = sharedPreferences.getStringOrDefault(
                HOME_ALBUM_GRID_STYLE,
                "4",
            ).toInt()
            val layoutRes = App.getContext()
                .resources.obtainTypedArray(R.array.pref_home_grid_style_layout).use {
                    it.getResourceId(position, 0)
                }
            return if (layoutRes == 0) {
                R.layout.item_image
            } else {
                layoutRes
            }
        }

    val tabTitleMode: Int
        get() {
            return when (
                sharedPreferences.getStringOrDefault(
                    TAB_TEXT_MODE,
                    "0",
                ).toInt()
            ) {
                0 -> BottomNavigationView.LABEL_VISIBILITY_AUTO
                1 -> BottomNavigationView.LABEL_VISIBILITY_LABELED
                2 -> BottomNavigationView.LABEL_VISIBILITY_SELECTED
                3 -> BottomNavigationView.LABEL_VISIBILITY_UNLABELED
                else -> BottomNavigationView.LABEL_VISIBILITY_LABELED
            }
        }

    var songGridSize
        get() = sharedPreferences.getInt(
            SONG_GRID_SIZE,
            App.getContext().getIntRes(R.integer.default_list_columns),
        )
        set(value) = sharedPreferences.edit {
            putInt(SONG_GRID_SIZE, value)
        }

    var songGridSizeLand
        get() = sharedPreferences.getInt(
            SONG_GRID_SIZE_LAND,
            App.getContext().getIntRes(R.integer.default_grid_columns_land),
        )
        set(value) = sharedPreferences.edit {
            putInt(SONG_GRID_SIZE_LAND, value)
        }

    var albumGridSize: Int
        get() = sharedPreferences.getInt(
            ALBUM_GRID_SIZE,
            App.getContext().getIntRes(R.integer.default_grid_columns),
        )
        set(value) = sharedPreferences.edit {
            putInt(ALBUM_GRID_SIZE, value)
        }

    var albumGridSizeLand
        get() = sharedPreferences.getInt(
            ALBUM_GRID_SIZE_LAND,
            App.getContext().getIntRes(R.integer.default_grid_columns_land),
        )
        set(value) = sharedPreferences.edit {
            putInt(ALBUM_GRID_SIZE_LAND, value)
        }

    var artistGridSize
        get() = sharedPreferences.getInt(
            ARTIST_GRID_SIZE,
            App.getContext().getIntRes(R.integer.default_grid_columns),
        )
        set(value) = sharedPreferences.edit {
            putInt(ARTIST_GRID_SIZE, value)
        }

    var artistGridSizeLand
        get() = sharedPreferences.getInt(
            ARTIST_GRID_SIZE_LAND,
            App.getContext().getIntRes(R.integer.default_grid_columns_land),
        )
        set(value) = sharedPreferences.edit {
            putInt(ALBUM_GRID_SIZE_LAND, value)
        }

    var playlistGridSize
        get() = sharedPreferences.getInt(
            PLAYLIST_GRID_SIZE,
            App.getContext().getIntRes(R.integer.default_grid_columns),
        )
        set(value) = sharedPreferences.edit {
            putInt(PLAYLIST_GRID_SIZE, value)
        }

    var playlistGridSizeLand
        get() = sharedPreferences.getInt(
            PLAYLIST_GRID_SIZE_LAND,
            App.getContext().getIntRes(R.integer.default_grid_columns_land),
        )
        set(value) = sharedPreferences.edit {
            putInt(PLAYLIST_GRID_SIZE, value)
        }

    var albumCoverStyle: AlbumCoverStyle
        get() {
            val id: Int = sharedPreferences.getInt(ALBUM_COVER_STYLE, 0)
            for (albumCoverStyle in AlbumCoverStyle.values()) {
                if (albumCoverStyle.id == id) {
                    return albumCoverStyle
                }
            }
            return AlbumCoverStyle.Card
        }
        set(value) = sharedPreferences.edit { putInt(ALBUM_COVER_STYLE, value.id) }

    val albumCoverTransform: ViewPager.PageTransformer
        get() {
            val style = sharedPreferences.getStringOrDefault(
                ALBUM_COVER_TRANSFORM,
                "0",
            ).toInt()
            return when (style) {
                0 -> NormalPageTransformer()
                1 -> CascadingPageTransformer()
                2 -> DepthTransformation()
                3 -> HorizontalFlipTransformation()
                4 -> VerticalFlipTransformation()
                5 -> HingeTransformation()
                6 -> VerticalStackTransformer()
                else -> ViewPager.PageTransformer { _, _ -> }
            }
        }

    var startDirectory: File
        get() {
            val folderPath = FoldersFragment.defaultStartDirectory.path
            val filePath: String = sharedPreferences.getStringOrDefault(START_DIRECTORY, folderPath)
            return File(filePath)
        }
        set(value) = sharedPreferences.edit {
            putString(
                START_DIRECTORY,
                FileUtil.safeGetCanonicalPath(value),
            )
        }

    fun getRecentlyPlayedCutoffTimeMillis(): Long {
        val calendarUtil = CalendarUtil()
        val interval: Long = when (sharedPreferences.getString(RECENTLY_PLAYED_CUTOFF, "")) {
            "today" -> calendarUtil.elapsedToday
            "this_week" -> calendarUtil.elapsedWeek
            "past_seven_days" -> calendarUtil.getElapsedDays(7)
            "past_three_months" -> calendarUtil.getElapsedMonths(3)
            "this_year" -> calendarUtil.elapsedYear
            "this_month" -> calendarUtil.elapsedMonth
            else -> calendarUtil.elapsedMonth
        }
        return System.currentTimeMillis() - interval
    }

    val lastAddedCutoff: Long
        get() {
            val calendarUtil = CalendarUtil()
            val interval =
                when (sharedPreferences.getStringOrDefault(LAST_ADDED_CUTOFF, "this_month")) {
                    "today" -> calendarUtil.elapsedToday
                    "this_week" -> calendarUtil.elapsedWeek
                    "past_three_months" -> calendarUtil.getElapsedMonths(3)
                    "this_year" -> calendarUtil.elapsedYear
                    "this_month" -> calendarUtil.elapsedMonth
                    else -> calendarUtil.elapsedMonth
                }
            return (System.currentTimeMillis() - interval) / 1000
        }

    val homeSuggestions: Boolean
        get() = sharedPreferences.getBoolean(
            TOGGLE_SUGGESTIONS,
            true,
        )

    val pauseHistory: Boolean
        get() = sharedPreferences.getBoolean(
            PAUSE_HISTORY,
            false,
        )

    var audioFadeDuration
        get() = sharedPreferences
            .getInt(AUDIO_FADE_DURATION, 0)
        set(value) = sharedPreferences.edit { putInt(AUDIO_FADE_DURATION, value) }

    var showLyrics: Boolean
        get() = sharedPreferences.getBoolean(SHOW_LYRICS, false)
        set(value) = sharedPreferences.edit { putBoolean(SHOW_LYRICS, value) }

    val rememberLastTab: Boolean
        get() = sharedPreferences.getBoolean(REMEMBER_LAST_TAB, true)

    var lastTab: Int
        get() = sharedPreferences
            .getInt(LAST_USED_TAB, 0)
        set(value) = sharedPreferences.edit { putInt(LAST_USED_TAB, value) }

    val crossFadeDuration
        get() = sharedPreferences
            .getInt(CROSS_FADE_DURATION, 0)

    val isCrossfadeEnabled get() = crossFadeDuration > 0

    val materialYou
        get() = sharedPreferences.getBoolean(MATERIAL_YOU, VersionUtils.hasS())

    val isCustomFont
        get() = sharedPreferences.getBoolean(CUSTOM_FONT, false)

    val lyricsType: CoverLyricsType
        get() = if (sharedPreferences.getString(LYRICS_TYPE, "0") == "0") {
            CoverLyricsType.REPLACE_COVER
        } else {
            CoverLyricsType.OVER_COVER
        }

    var playbackSpeed
        get() = sharedPreferences
            .getFloat(PLAYBACK_SPEED, 1F)
        set(value) = sharedPreferences.edit { putFloat(PLAYBACK_SPEED, value) }

    var playbackPitch
        get() = sharedPreferences
            .getFloat(PLAYBACK_PITCH, 1F)
        set(value) = sharedPreferences.edit { putFloat(PLAYBACK_PITCH, value) }

    val appBarMode: TopAppBarLayout.AppBarMode
        get() = if (sharedPreferences.getString(APPBAR_MODE, "1") == "0") {
            TopAppBarLayout.AppBarMode.COLLAPSING
        } else {
            TopAppBarLayout.AppBarMode.SIMPLE
        }

    val wallpaperAccent
        get() = sharedPreferences.getBoolean(
            WALLPAPER_ACCENT,
            VersionUtils.hasOreoMR1() && !VersionUtils.hasS(),
        )

    val lyricsScreenOn
        get() = sharedPreferences.getBoolean(SCREEN_ON_LYRICS, false)

    val circlePlayButton
        get() = sharedPreferences.getBoolean(CIRCLE_PLAY_BUTTON, false)

    val swipeAnywhereToChangeSong
        get() = sharedPreferences.getBoolean(SWIPE_ANYWHERE_NOW_PLAYING, true)

    val swipeDownToDismiss
        get() = sharedPreferences.getBoolean(SWIPE_DOWN_DISMISS, true)
}

enum class CoverLyricsType {
    REPLACE_COVER, OVER_COVER
}
