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
package ru.stersh.retrosonic.feature.details.playlist.presentation

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import me.zhanghai.android.fastscroll.PopupTextProvider
import ru.stersh.retrosonic.EXTRA_ALBUM_ID
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.adapter.base.AbsMultiSelectAdapter
import ru.stersh.retrosonic.adapter.base.MediaEntryViewHolder
import ru.stersh.retrosonic.adapter.song.SongAdapter
import ru.stersh.retrosonic.glide.GlideApp
import ru.stersh.retrosonic.glide.RetroMusicColoredTarget
import ru.stersh.retrosonic.helper.SortOrder
import ru.stersh.retrosonic.util.MusicUtil
import ru.stersh.retrosonic.util.PreferenceUtil
import ru.stersh.retrosonic.util.RetroUtil
import ru.stersh.retrosonic.util.color.MediaNotificationProcessor

class PlaylistDetailsSongAdapter(
    activity: FragmentActivity,
    private val onClick: (songId: String) -> Unit,
) : AbsMultiSelectAdapter<PlaylistDetailsSongAdapter.ViewHolder, PlaylistSongUi>(
    activity,
    R.menu.menu_media_selection,
),
    PopupTextProvider {

    init {
        this.setHasStableIds(true)
        this.setMultiSelectMenuRes(R.menu.menu_playlists_songs_selection)
    }

    private var dataSet: List<PlaylistSongUi> = emptyList()

    private var showSectionName = true

    init {
        this.setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistDetailsSongAdapter.ViewHolder {
        return LayoutInflater.from(activity).inflate(R.layout.item_queue, parent, false).let { ViewHolder(it) }
    }

    override fun getItemId(position: Int): Long {
        return if (position != 0) {
            dataSet[position - 1].id.hashCode().toLong()
        } else {
            -1
        }
    }

    override fun getIdentifier(position: Int): PlaylistSongUi? {
        var positionFinal = position
        positionFinal--
        return if (positionFinal < 0) null else dataSet[positionFinal]
    }

    override fun getItemCount(): Int = dataSet.size

    fun swapDataSet(dataSet: List<PlaylistSongUi>) {
        this.dataSet = ArrayList(dataSet)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = dataSet[position]
        val isChecked = isChecked(song)
        holder.itemView.isActivated = isChecked
        holder.menu?.isGone = isChecked
        holder.title?.text = song.title
        holder.text?.text = song.artist
        holder.text2?.text = song.album
        loadAlbumCover(song, holder)
        val landscape = RetroUtil.isLandscape
        if ((PreferenceUtil.songGridSize > 2 && !landscape) || (PreferenceUtil.songGridSizeLand > 5 && landscape)) {
            holder.menu?.isVisible = false
        }
    }

    private fun setColors(color: MediaNotificationProcessor, holder: ViewHolder) {
        if (holder.paletteColorContainer != null) {
            holder.title?.setTextColor(color.primaryTextColor)
            holder.text?.setTextColor(color.secondaryTextColor)
            holder.paletteColorContainer?.setBackgroundColor(color.backgroundColor)
            holder.menu?.imageTintList = ColorStateList.valueOf(color.primaryTextColor)
        }
        holder.mask?.backgroundTintList = ColorStateList.valueOf(color.primaryTextColor)
    }

    private fun loadAlbumCover(song: PlaylistSongUi, holder: ViewHolder) {
        if (holder.image == null) {
            return
        }
        GlideApp
            .with(activity)
            .asBitmapPalette()
            .load(song.coverArtUrl)
            .into(object : RetroMusicColoredTarget(holder.image!!) {
                override fun onColorReady(colors: MediaNotificationProcessor) {
                    setColors(colors, holder)
                }
            })
    }

    override fun getName(model: PlaylistSongUi): String {
        return model.title
    }

    override fun onMultipleItemAction(menuItem: MenuItem, selection: List<PlaylistSongUi>) {
        when (menuItem.itemId) {
            R.id.action_remove_from_playlist -> {
//                RemoveSongFromPlaylistDialog.create(
//                selection.toSongsEntity(
//                    playlist
//                )
//                )
//                    .show(activity.supportFragmentManager, "REMOVE_FROM_PLAYLIST")
            }
            else -> true // SongsMenuHelper.handleMenuClick(activity, selection, menuItem.itemId)
        }
    }

    override fun getPopupText(position: Int): String {
        val sectionName: String? = when (PreferenceUtil.songSortOrder) {
            SortOrder.SongSortOrder.SONG_DEFAULT -> return MusicUtil.getSectionName(dataSet[position].title, true)
            SortOrder.SongSortOrder.SONG_A_Z, SortOrder.SongSortOrder.SONG_Z_A -> dataSet[position].title
            SortOrder.SongSortOrder.SONG_ALBUM -> dataSet[position].album
            SortOrder.SongSortOrder.SONG_ARTIST -> dataSet[position].artist
            SortOrder.SongSortOrder.SONG_YEAR -> return MusicUtil.getYearString(dataSet[position].year)
            else -> {
                return ""
            }
        }
        return MusicUtil.getSectionName(sectionName)
    }

    open inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {

        protected open var songMenuRes = R.menu.menu_item_playlist_song
        protected open val song: PlaylistSongUi
            get() = dataSet[layoutPosition]

        init {
            dragView?.isVisible = true
//            menu?.setOnClickListener(object : SongMenuHelper.OnClickSongMenu(activity) {
//                override val song: PlaylistSongUi
//                    get() = this@ViewHolder.song
//
//                override val menuRes: Int
//                    get() = songMenuRes
//
//                override fun onMenuItemClick(item: MenuItem): Boolean {
//                    return onSongMenuItemClick(item) || super.onMenuItemClick(item)
//                }
//            })
        }

        protected open fun onSongMenuItemClick(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_remove_from_playlist -> {
//                    RemoveSongFromPlaylistDialog.create(song.toSongEntity(playlist.playListId))
//                        .show(activity.supportFragmentManager, "REMOVE_FROM_PLAYLIST")
                    return true
                }
            }
            if (image != null && image!!.isVisible) {
                when (item.itemId) {
                    R.id.action_go_to_album -> {
                        activity.findNavController(R.id.fragment_container)
                            .navigate(
                                R.id.albumDetailsFragment,
                                bundleOf(EXTRA_ALBUM_ID to song.albumId),
                            )
                        return true
                    }
                }
            }
            return false
        }

        override fun onClick(v: View?) {
            if (isInQuickSelectMode) {
                toggleChecked(layoutPosition)
            } else {
                onClick.invoke(song.id)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            toggleChecked(layoutPosition)
            return true
        }
    }

    companion object {
        val TAG: String = SongAdapter::class.java.simpleName
    }
}
