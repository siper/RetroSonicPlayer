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
package ru.stersh.retrosonic.adapter.playlist

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isGone
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentActivity
import code.name.monkey.appthemehelper.util.ATHUtil
import code.name.monkey.appthemehelper.util.TintHelper
import me.zhanghai.android.fastscroll.PopupTextProvider
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.adapter.base.AbsMultiSelectAdapter
import ru.stersh.retrosonic.adapter.base.MediaEntryViewHolder
import ru.stersh.retrosonic.extensions.dipToPix
import ru.stersh.retrosonic.glide.GlideApp
import ru.stersh.retrosonic.glide.playlistPreview.PlaylistPreview
import ru.stersh.retrosonic.helper.SortOrder.PlaylistSortOrder
import ru.stersh.retrosonic.helper.menu.PlaylistMenuHelper
import ru.stersh.retrosonic.helper.menu.SongsMenuHelper
import ru.stersh.retrosonic.interfaces.IPlaylistClickListener
import ru.stersh.retrosonic.model.Song
import ru.stersh.retrosonic.toSongs
import ru.stersh.retrosonic.util.MusicUtil
import ru.stersh.retrosonic.util.PreferenceUtil

class PlaylistAdapter(
    override val activity: FragmentActivity,
    var dataSet: List<ru.stersh.apisonic.room.playlist.PlaylistWithSongs>,
    private var itemLayoutRes: Int,
    private val listener: IPlaylistClickListener,
) : AbsMultiSelectAdapter<PlaylistAdapter.ViewHolder, ru.stersh.apisonic.room.playlist.PlaylistWithSongs>(
    activity,
    R.menu.menu_playlists_selection,
),
    PopupTextProvider {

    init {
        setHasStableIds(true)
    }

    fun swapDataSet(dataSet: List<ru.stersh.apisonic.room.playlist.PlaylistWithSongs>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return dataSet[position].playlistEntity.playListId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity).inflate(itemLayoutRes, parent, false)
        return createViewHolder(view)
    }

    private fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view)
    }

    private fun getPlaylistTitle(playlist: ru.stersh.apisonic.room.playlist.PlaylistEntity): String {
        return playlist.playlistName.ifEmpty { "-" }
    }

    private fun getPlaylistText(playlist: ru.stersh.apisonic.room.playlist.PlaylistWithSongs): String {
        return MusicUtil.getPlaylistInfoString(activity, playlist.songs.toSongs())
    }

    override fun getPopupText(position: Int): String {
        val sectionName: String = when (PreferenceUtil.playlistSortOrder) {
            PlaylistSortOrder.PLAYLIST_A_Z, PlaylistSortOrder.PLAYLIST_Z_A -> dataSet[position].playlistEntity.playlistName
            PlaylistSortOrder.PLAYLIST_SONG_COUNT, PlaylistSortOrder.PLAYLIST_SONG_COUNT_DESC -> dataSet[position].songs.size.toString()
            else -> {
                return ""
            }
        }
        return MusicUtil.getSectionName(sectionName)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = dataSet[position]
        holder.itemView.isActivated = isChecked(playlist)
        holder.title?.text = getPlaylistTitle(playlist.playlistEntity)
        holder.text?.text = getPlaylistText(playlist)
        holder.menu?.isGone = isChecked(playlist)
        if (itemLayoutRes == R.layout.item_list) {
            holder.image?.setPadding(activity.dipToPix(8F).toInt())
            holder.image?.setImageDrawable(getIconRes())
        } else {
            GlideApp.with(activity)
                .load(
                    PlaylistPreview(playlist),
                )
                .playlistOptions()
                .into(holder.image!!)
        }
    }

    private fun getIconRes(): Drawable = TintHelper.createTintedDrawable(
        activity,
        R.drawable.ic_playlist_play,
        ATHUtil.resolveColor(activity, android.R.attr.colorControlNormal),
    )

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getIdentifier(position: Int): ru.stersh.apisonic.room.playlist.PlaylistWithSongs {
        return dataSet[position]
    }

    override fun getName(model: ru.stersh.apisonic.room.playlist.PlaylistWithSongs): String {
        return model.playlistEntity.playlistName
    }

    override fun onMultipleItemAction(menuItem: MenuItem, selection: List<ru.stersh.apisonic.room.playlist.PlaylistWithSongs>) {
        when (menuItem.itemId) {
            else -> SongsMenuHelper.handleMenuClick(
                activity,
                getSongList(selection),
                menuItem.itemId,
            )
        }
    }

    private fun getSongList(playlists: List<ru.stersh.apisonic.room.playlist.PlaylistWithSongs>): List<Song> {
        val songs = mutableListOf<Song>()
        playlists.forEach {
            songs.addAll(it.songs.toSongs())
        }
        return songs
    }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {
        init {
            menu?.setOnClickListener { view ->
                val popupMenu = PopupMenu(activity, view)
                popupMenu.inflate(R.menu.menu_item_playlist)
                popupMenu.setOnMenuItemClickListener { item ->
                    PlaylistMenuHelper.handleMenuClick(activity, dataSet[layoutPosition], item)
                }
                popupMenu.show()
            }

            imageTextContainer?.apply {
                cardElevation = 0f
                setCardBackgroundColor(Color.TRANSPARENT)
            }
        }

        override fun onClick(v: View?) {
            if (isInQuickSelectMode) {
                toggleChecked(layoutPosition)
            } else {
                itemView.transitionName = "playlist"
                listener.onPlaylistClick(dataSet[layoutPosition], itemView)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            toggleChecked(layoutPosition)
            return true
        }
    }

    companion object {
        val TAG: String = PlaylistAdapter::class.java.simpleName
    }
}
