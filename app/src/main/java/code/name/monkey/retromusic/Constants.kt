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
package code.name.monkey.retromusic

import android.provider.BaseColumns
import android.provider.MediaStore

object Constants {
    const val PRO_VERSION_PRODUCT_ID = "pro_version"
    const val RATE_ON_GOOGLE_PLAY =
        "https://play.google.com/store/apps/details?id=code.name.monkey.retromusic"
    const val TRANSLATE = "https://crowdin.com/project/retromusicplayer"
    const val WEBSITE = "https://retromusic.app"
    const val GITHUB_PROJECT = "https://github.com/RetroMusicPlayer/RetroMusicPlayer"
    const val TELEGRAM_CHANGE_LOG = "https://t.me/retromusiclog"
    const val USER_PROFILE = "profile.jpg"
    const val USER_BANNER = "banner.jpg"
    const val APP_INSTAGRAM_LINK = "https://www.instagram.com/retromusicapp/"
    const val APP_TELEGRAM_LINK = "https://t.me/retromusicapp/"
    const val APP_TWITTER_LINK = "https://twitter.com/retromusicapp"
    const val FAQ_LINK = "https://github.com/RetroMusicPlayer/RetroMusicPlayer/blob/master/FAQ.md"
    const val PINTEREST = "https://in.pinterest.com/retromusicapp/"
    const val AUDIO_SCROBBLER_URL = "https://ws.audioscrobbler.com/2.0/"

    const val IS_MUSIC =
        MediaStore.Audio.AudioColumns.IS_MUSIC + "=1" + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''"

    const val DATA = "_data"

    @Suppress("Deprecation")
    val baseProjection = arrayOf(
        BaseColumns._ID, // 0
        MediaStore.Audio.AudioColumns.TITLE, // 1
        MediaStore.Audio.AudioColumns.TRACK, // 2
        MediaStore.Audio.AudioColumns.YEAR, // 3
        MediaStore.Audio.AudioColumns.DURATION, // 4
        DATA, // 5
        MediaStore.Audio.AudioColumns.DATE_MODIFIED, // 6
        MediaStore.Audio.AudioColumns.ALBUM_ID, // 7
        MediaStore.Audio.AudioColumns.ALBUM, // 8
        MediaStore.Audio.AudioColumns.ARTIST_ID, // 9
        MediaStore.Audio.AudioColumns.ARTIST, // 10
        MediaStore.Audio.AudioColumns.COMPOSER, // 11
        ALBUM_ARTIST // 12
    )
    const val NUMBER_OF_TOP_TRACKS = 99
}

const val EXTRA_PLAYLIST_TYPE = "type"
const val EXTRA_GENRE = "extra_genre"
const val EXTRA_PLAYLIST = "extra_playlist"
const val EXTRA_PLAYLIST_ID = "extra_playlist_id"
const val EXTRA_ALBUM_ID = "extra_album_id"
const val EXTRA_ARTIST_ID = "extra_artist_id"
const val EXTRA_SONG = "extra_songs"
const val EXTRA_PLAYLISTS = "extra_playlists"
const val LIBRARY_CATEGORIES = "library_categories"
const val EXTRA_SONG_INFO = "extra_song_info"
const val DESATURATED_COLOR = "desaturated_color"
const val BLACK_THEME = "black_theme"
const val KEEP_SCREEN_ON = "keep_screen_on"
const val TOGGLE_HOME_BANNER = "toggle_home_banner"
const val CAROUSEL_EFFECT = "carousel_effect"
const val COLORED_NOTIFICATION = "colored_notification"
const val CLASSIC_NOTIFICATION = "classic_notification"
const val GAP_LESS_PLAYBACK = "gapless_playback"
const val ALBUM_ART_ON_LOCK_SCREEN = "album_art_on_lock_screen"
const val BLURRED_ALBUM_ART = "blurred_album_art"
const val NEW_BLUR_AMOUNT = "new_blur_amount"
const val TOGGLE_HEADSET = "toggle_headset"
const val GENERAL_THEME = "general_theme"
const val ACCENT_COLOR = "accent_color"
const val SHOULD_COLOR_APP_SHORTCUTS = "should_color_app_shortcuts"
const val CIRCULAR_ALBUM_ART = "circular_album_art"
const val USER_NAME = "user_name"
const val TOGGLE_FULL_SCREEN = "toggle_full_screen"
const val TOGGLE_VOLUME = "toggle_volume"
const val ADAPTIVE_COLOR_APP = "adaptive_color_app"
const val HOME_ARTIST_GRID_STYLE = "home_artist_grid_style"
const val HOME_ALBUM_GRID_STYLE = "home_album_grid_style"
const val TOGGLE_ADD_CONTROLS = "toggle_add_controls"
const val ALBUM_COVER_STYLE = "album_cover_style_id"
const val ALBUM_COVER_TRANSFORM = "album_cover_transform"
const val TAB_TEXT_MODE = "tab_text_mode"
const val LANGUAGE_NAME = "language_name"
const val SLEEP_TIMER_FINISH_SONG = "sleep_timer_finish_song"
const val ALBUM_GRID_STYLE = "album_grid_style_home"
const val ARTIST_GRID_STYLE = "artist_grid_style_home"
const val SAF_SDCARD_URI = "saf_sdcard_uri"
const val SONG_SORT_ORDER = "song_sort_order"
const val SONG_GRID_SIZE = "song_grid_size"
const val GENRE_SORT_ORDER = "genre_sort_order"
const val BLUETOOTH_PLAYBACK = "bluetooth_playback"
const val INITIALIZED_BLACKLIST = "initialized_blacklist"
const val ARTIST_SORT_ORDER = "artist_sort_order"
const val ARTIST_ALBUM_SORT_ORDER = "artist_album_sort_order"
const val ALBUM_SORT_ORDER = "album_sort_order"
const val PLAYLIST_SORT_ORDER = "playlist_sort_order"
const val ALBUM_SONG_SORT_ORDER = "album_song_sort_order"
const val ARTIST_SONG_SORT_ORDER = "artist_song_sort_order"
const val ALBUM_GRID_SIZE = "album_grid_size"
const val ALBUM_GRID_SIZE_LAND = "album_grid_size_land"
const val SONG_GRID_SIZE_LAND = "song_grid_size_land"
const val ARTIST_GRID_SIZE = "artist_grid_size"
const val ARTIST_GRID_SIZE_LAND = "artist_grid_size_land"
const val PLAYLIST_GRID_SIZE = "playlist_grid_size"
const val PLAYLIST_GRID_SIZE_LAND = "playlist_grid_size_land"
const val COLORED_APP_SHORTCUTS = "colored_app_shortcuts"
const val AUDIO_DUCKING = "audio_ducking"
const val LAST_ADDED_CUTOFF = "last_added_interval"
const val LAST_SLEEP_TIMER_VALUE = "last_sleep_timer_value"
const val NEXT_SLEEP_TIMER_ELAPSED_REALTIME = "next_sleep_timer_elapsed_real_time"
const val IGNORE_MEDIA_STORE_ARTWORK = "ignore_media_store_artwork"
const val LAST_CHANGELOG_VERSION = "last_changelog_version"
const val AUTO_DOWNLOAD_IMAGES_POLICY = "auto_download_images_policy"
const val START_DIRECTORY = "start_directory"
const val RECENTLY_PLAYED_CUTOFF = "recently_played_interval"
const val LOCK_SCREEN = "lock_screen"
const val ALBUM_ARTISTS_ONLY = "album_artists_only"
const val ALBUM_ARTIST = "album_artist"
const val ALBUM_DETAIL_SONG_SORT_ORDER = "album_detail_song_sort_order"
const val ARTIST_DETAIL_SONG_SORT_ORDER = "artist_detail_song_sort_order"
const val LYRICS_OPTIONS = "lyrics_tab_position"
const val CHOOSE_EQUALIZER = "choose_equalizer"
const val EQUALIZER = "equalizer"
const val SONG_GRID_STYLE = "song_grid_style"
const val PAUSE_ON_ZERO_VOLUME = "pause_on_zero_volume"
const val FILTER_SONG = "filter_song"
const val EXPAND_NOW_PLAYING_PANEL = "expand_now_playing_panel"
const val EXTRA_ARTIST_NAME = "extra_artist_name"
const val TOGGLE_SUGGESTIONS = "toggle_suggestions"
const val AUDIO_FADE_DURATION = "audio_fade_duration"
const val CROSS_FADE_DURATION = "cross_fade_duration"
const val SHOW_LYRICS = "show_lyrics"
const val REMEMBER_LAST_TAB = "remember_last_tab"
const val LAST_USED_TAB = "last_used_tab"
const val MATERIAL_YOU = "material_you"
const val SNOWFALL = "snowfall"
const val LYRICS_TYPE = "lyrics_type"
const val PLAYBACK_SPEED = "playback_speed"
const val PLAYBACK_PITCH = "playback_pitch"
const val CUSTOM_FONT = "custom_font"
const val APPBAR_MODE = "appbar_mode"
const val WALLPAPER_ACCENT = "wallpaper_accent"
const val SCREEN_ON_LYRICS = "screen_on_lyrics"
const val CIRCLE_PLAY_BUTTON = "circle_play_button"
const val SWIPE_ANYWHERE_NOW_PLAYING = "swipe_anywhere_now_playing"
const val PAUSE_HISTORY = "pause_history"
const val MANAGE_AUDIO_FOCUS = "manage_audio_focus"
const val SWIPE_DOWN_DISMISS = "swipe_to_dismiss"