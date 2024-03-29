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
package ru.stersh.retrosonic.fragments.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.GestureDetector
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.viewpager.widget.ViewPager
import code.name.monkey.appthemehelper.util.VersionUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.dialogs.PlaybackSpeedDialog
import ru.stersh.retrosonic.dialogs.SleepTimerDialog
import ru.stersh.retrosonic.extensions.currentFragment
import ru.stersh.retrosonic.extensions.getTintedDrawable
import ru.stersh.retrosonic.extensions.hide
import ru.stersh.retrosonic.extensions.keepScreenOn
import ru.stersh.retrosonic.extensions.whichFragment
import ru.stersh.retrosonic.feature.main.presentation.MainActivity
import ru.stersh.retrosonic.feature.player.cover.presentation.PlayerAlbumCoverFragment
import ru.stersh.retrosonic.fragments.LibraryViewModel
import ru.stersh.retrosonic.interfaces.IPaletteColorHolder
import ru.stersh.retrosonic.util.NavigationUtil
import ru.stersh.retrosonic.util.PreferenceUtil
import kotlin.math.abs

abstract class AbsPlayerFragment(@LayoutRes layout: Int) :
    Fragment(layout),
    Toolbar.OnMenuItemClickListener,
    IPaletteColorHolder,
    PlayerAlbumCoverFragment.Callbacks {

    val libraryViewModel: LibraryViewModel by sharedViewModel()

    val mainActivity: MainActivity
        get() = activity as MainActivity

    private var playerAlbumCoverFragment: PlayerAlbumCoverFragment? = null

    override fun onMenuItemClick(
        item: MenuItem,
    ): Boolean {
//        val songId = MusicPlayerRemote.currentSongId ?: return false
        when (item.itemId) {
            R.id.action_playback_speed -> {
                PlaybackSpeedDialog.newInstance().show(childFragmentManager, "PLAYBACK_SETTINGS")
                return true
            }
            R.id.action_toggle_lyrics -> {
                PreferenceUtil.showLyrics = !PreferenceUtil.showLyrics
                showLyricsIcon(item)
                if (PreferenceUtil.lyricsScreenOn && PreferenceUtil.showLyrics) {
                    mainActivity.keepScreenOn(true)
                } else if (!PreferenceUtil.isScreenOnEnabled && !PreferenceUtil.showLyrics) {
                    mainActivity.keepScreenOn(false)
                }
                return true
            }
            R.id.action_go_to_lyrics -> {
                goToLyrics(requireActivity())
                return true
            }
            R.id.action_toggle_favorite -> {
//                toggleFavorite(songId)
                return true
            }
            R.id.action_share -> {
//                SongShareDialog.create(songId).show(childFragmentManager, "SHARE_SONG")
                return true
            }
            R.id.action_go_to_drive_mode -> {
                NavigationUtil.gotoDriveMode(requireActivity())
                return true
            }
            R.id.action_delete_from_device -> {
//                DeleteSongsDialog.create(songId).show(childFragmentManager, "DELETE_SONGS")
                return true
            }
            R.id.action_add_to_playlist -> {
//                TODO: add to playlist
//                lifecycleScope.launch(IO) {
//                    val playlists = get<RealRepository>().fetchPlaylists()
//                    withContext(Main) {
//                        AddToPlaylistDialog.create(playlists, songId)
//                            .show(childFragmentManager, "ADD_PLAYLIST")
//                    }
//                }
                return true
            }
            R.id.action_clear_playing_queue -> {
//                MusicPlayerRemote.clearQueue()
                return true
            }
            R.id.action_save_playing_queue -> {
//                CreatePlaylistDialog.create(ArrayList(MusicPlayerRemote.playingQueue))
//                    .show(childFragmentManager, "ADD_TO_PLAYLIST")
                return true
            }
            R.id.action_details -> {
//                SongDetailDialog.create(songId).show(childFragmentManager, "SONG_DETAIL")
                return true
            }
            R.id.action_go_to_album -> {
                // Hide Bottom Bar First, else Bottom Sheet doesn't collapse fully
                mainActivity.setBottomNavVisibility(false)
                mainActivity.collapsePanel()
//                requireActivity().findNavController(R.id.fragment_container).navigate(
//                    R.id.albumDetailsFragment,
//                    bundleOf(EXTRA_ALBUM_ID to songId)
//                )
                return true
            }
            R.id.action_go_to_artist -> {
                goToArtist(requireActivity())
                return true
            }
            R.id.now_playing -> {
                requireActivity().findNavController(R.id.fragment_container).navigate(
                    R.id.playing_queue_fragment,
                    null,
                    navOptions { launchSingleTop = true },
                )
                mainActivity.collapsePanel()
                return true
            }
            R.id.action_show_lyrics -> {
                goToLyrics(requireActivity())
                return true
            }
            R.id.action_equalizer -> {
                NavigationUtil.openEqualizer(requireActivity())
                return true
            }
            R.id.action_sleep_timer -> {
                SleepTimerDialog().show(parentFragmentManager, "SLEEP_TIMER")
                return true
            }
            R.id.action_go_to_genre -> {
//                TODO: go to genre
//                val retriever = MediaMetadataRetriever()
//                val trackUri =
//                    ContentUris.withAppendedId(
//                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                        songId
//                    )
//                retriever.setDataSource(activity, trackUri)
//                var genre: String? =
//                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
//                if (genre == null) {
//                    genre = "Not Specified"
//                }
//                showToast(genre)
                return true
            }
        }
        return false
    }

    private fun showLyricsIcon(item: MenuItem) {
        val icon =
            if (PreferenceUtil.showLyrics) R.drawable.ic_lyrics else R.drawable.ic_lyrics_outline
        val drawable = requireContext().getTintedDrawable(
            icon,
            toolbarIconColor(),
        )
        item.isChecked = PreferenceUtil.showLyrics
        item.icon = drawable
    }

    abstract fun playerToolbar(): Toolbar?

    abstract fun onShow()

    abstract fun onHide()

    abstract fun onBackPressed(): Boolean

    abstract fun toolbarIconColor(): Int

    protected open fun toggleFavorite(songId: String) {
//        TODO: fix favorite
//        lifecycleScope.launch(IO) {
//            val playlist: PlaylistEntity = libraryViewModel.favoritePlaylist()
//            val songEntity = songId.toSongEntity(playlist.playListId)
//            val isFavorite = libraryViewModel.isSongFavorite(songId.id)
//            if (isFavorite) {
//                libraryViewModel.removeSongFromPlaylist(songEntity)
//            } else {
//                libraryViewModel.insertSongs(listOf(songId.toSongEntity(playlist.playListId)))
//            }
//            libraryViewModel.forceReload(ReloadType.Playlists)
//            requireContext().sendBroadcast(Intent(MusicService.FAVORITE_STATE_CHANGED))
//        }
    }

    fun updateIsFavorite(animate: Boolean = false) {
        // TODO: favorite
//        lifecycleScope.launch(IO) {
//            val isFavorite: Boolean =
//                libraryViewModel.isSongFavorite(MusicPlayerRemote.currentSongId)
//            withContext(Main) {
//                val icon = if (animate && VersionUtils.hasMarshmallow()) {
//                    if (isFavorite) R.drawable.avd_favorite else R.drawable.avd_unfavorite
//                } else {
//                    if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
//                }
//                val drawable = requireContext().getTintedDrawable(
//                    icon,
//                    toolbarIconColor()
//                )
//                if (playerToolbar() != null) {
//                    playerToolbar()?.menu?.findItem(R.id.action_toggle_favorite)?.apply {
//                        setIcon(drawable)
//                        title =
//                            if (isFavorite) getString(R.string.action_remove_from_favorites)
//                            else getString(R.string.action_add_to_favorites)
//                        getIcon().also {
//                            if (it is AnimatedVectorDrawable) {
//                                it.start()
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PreferenceUtil.circlePlayButton) {
            requireContext().theme.applyStyle(R.style.CircleFABOverlay, true)
        } else {
            requireContext().theme.applyStyle(R.style.RoundedFABOverlay, true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (PreferenceUtil.isFullScreenMode &&
            view.findViewById<View>(R.id.status_bar) != null
        ) {
            view.findViewById<View>(R.id.status_bar).isVisible = false
        }
        playerAlbumCoverFragment = whichFragment(R.id.playerAlbumCoverFragment)
        playerAlbumCoverFragment?.setCallbacks(this)

        if (VersionUtils.hasMarshmallow()) {
            view.findViewById<RelativeLayout>(R.id.statusBarShadow)?.hide()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()

        playerToolbar()?.menu?.findItem(R.id.action_toggle_lyrics)?.apply {
            isChecked = PreferenceUtil.showLyrics
            showLyricsIcon(this)
        }
    }

    override fun onStart() {
        super.onStart()
        addSwipeDetector()
    }

    fun addSwipeDetector() {
        view?.setOnTouchListener(
            if (PreferenceUtil.swipeAnywhereToChangeSong) {
                SwipeDetector(
                    requireContext(),
                    playerAlbumCoverFragment?.viewPager,
                    requireView(),
                )
            } else {
                null
            },
        )
    }

    class SwipeDetector(val context: Context, val viewPager: ViewPager?, val view: View) :
        View.OnTouchListener {
        private var flingPlayBackController: GestureDetector = GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onScroll(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float,
                ): Boolean {
                    return when {
                        abs(distanceX) > abs(distanceY) -> {
                            // Disallow Intercept Touch Event so that parent(BottomSheet) doesn't consume the events
                            view.parent.requestDisallowInterceptTouchEvent(true)
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }
            },
        )

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            viewPager?.dispatchTouchEvent(event)
            return flingPlayBackController.onTouchEvent(event)
        }
    }

    companion object {
        val TAG: String = AbsPlayerFragment::class.java.simpleName
        const val VISIBILITY_ANIM_DURATION: Long = 300
    }
}

fun goToArtist(activity: Activity) {
    if (activity !is MainActivity) return
//    val song = MusicPlayerRemote.currentSongId
    activity.apply {
        // Remove exit transition of current fragment so
        // it doesn't exit with a weird transition
        currentFragment(R.id.fragment_container)?.exitTransition = null

        // Hide Bottom Bar First, else Bottom Sheet doesn't collapse fully
        setBottomNavVisibility(false)
        if (getBottomSheetBehavior().state == BottomSheetBehavior.STATE_EXPANDED) {
            collapsePanel()
        }
// TODO: artist details
//        findNavController(R.id.fragment_container).navigate(
//            R.id.artistDetailsFragment,
//            bundleOf(EXTRA_ARTIST_ID to song.artistId)
//        )
    }
}

fun goToAlbum(activity: Activity) {
    if (activity !is MainActivity) return
//    val song = MusicPlayerRemote.currentSongId
    // TODO: go to album
//    activity.apply {
//        currentFragment(R.id.fragment_container)?.exitTransition = null
//
//        //Hide Bottom Bar First, else Bottom Sheet doesn't collapse fully
//        setBottomNavVisibility(false)
//        if (getBottomSheetBehavior().state == BottomSheetBehavior.STATE_EXPANDED) {
//            collapsePanel()
//        }
//
//        findNavController(R.id.fragment_container).navigate(
//            R.id.albumDetailsFragment,
//            bundleOf(EXTRA_ALBUM_ID to song.albumId)
//        )
//    }
}

fun goToLyrics(activity: Activity) {
    if (activity !is MainActivity) return
    activity.apply {
        // Hide Bottom Bar First, else Bottom Sheet doesn't collapse fully
        setBottomNavVisibility(false)
        if (getBottomSheetBehavior().state == BottomSheetBehavior.STATE_EXPANDED) {
            collapsePanel()
        }

        findNavController(R.id.fragment_container).navigate(
            R.id.lyrics_fragment,
            null,
            navOptions { launchSingleTop = true },
        )
    }
}
