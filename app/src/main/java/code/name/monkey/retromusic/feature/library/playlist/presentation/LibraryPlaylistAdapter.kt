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
package code.name.monkey.retromusic.feature.library.playlist.presentation

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
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.adapter.base.AbsMultiSelectAdapter
import code.name.monkey.retromusic.adapter.base.MediaEntryViewHolder
import code.name.monkey.retromusic.extensions.dipToPix
import code.name.monkey.retromusic.glide.GlideApp
import code.name.monkey.retromusic.helper.SortOrder.PlaylistSortOrder
import code.name.monkey.retromusic.util.MusicUtil
import code.name.monkey.retromusic.util.PreferenceUtil
import me.zhanghai.android.fastscroll.PopupTextProvider

class LibraryPlaylistAdapter(
    override val activity: FragmentActivity,
    private var itemLayoutRes: Int,
    private val onClick: (view: View, id: String) -> Unit
) : AbsMultiSelectAdapter<LibraryPlaylistAdapter.ViewHolder, LibraryPlaylistUi>(
    activity,
    R.menu.menu_playlists_selection
), PopupTextProvider {

    private var dataSet: List<LibraryPlaylistUi> = emptyList()

    init {
        setHasStableIds(true)
    }

    fun swapDataSet(dataSet: List<LibraryPlaylistUi>) {
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return dataSet[position].id.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(itemLayoutRes, parent, false)
        return createViewHolder(view)
    }

    private fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view)
    }

    override fun getPopupText(position: Int): String {
        val sectionName: String = when (PreferenceUtil.playlistSortOrder) {
            PlaylistSortOrder.PLAYLIST_A_Z,
            PlaylistSortOrder.PLAYLIST_Z_A -> dataSet[position].title
            PlaylistSortOrder.PLAYLIST_SONG_COUNT,
            PlaylistSortOrder.PLAYLIST_SONG_COUNT_DESC -> dataSet[position].songCount.toString()
            else -> {
                return ""
            }
        }
        return MusicUtil.getSectionName(sectionName)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val playlist = dataSet[position]
        holder.itemView.isActivated = isChecked(playlist)
        holder.title?.text = playlist.title
        holder.text?.text = MusicUtil.getPlaylistInfoString(context, playlist.songCount, playlist.duration)
        holder.menu?.isGone = isChecked(playlist)
        if (holder.imageContainer != null) {
            holder.imageContainer?.transitionName = playlist.id
        } else {
            holder.image?.transitionName = playlist.id
        }
        if (itemLayoutRes == R.layout.item_list) {
            holder.image?.setPadding(activity.dipToPix(8F).toInt())
            holder.image?.setImageDrawable(getIconRes())
        } else {
            GlideApp
                .with(context)
                .load(playlist.coverArtUrl)
                .playlistOptions()
                .into(holder.image!!)
        }
    }

    private fun getIconRes(): Drawable = TintHelper.createTintedDrawable(
        activity,
        R.drawable.ic_playlist_play,
        ATHUtil.resolveColor(activity, android.R.attr.colorControlNormal)
    )

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getIdentifier(position: Int): LibraryPlaylistUi {
        return dataSet[position]
    }

    override fun getName(model: LibraryPlaylistUi): String {
        return model.title
    }

    override fun onMultipleItemAction(menuItem: MenuItem, selection: List<LibraryPlaylistUi>) {
        when (menuItem.itemId) {
            else -> {
//                SongsMenuHelper.handleMenuClick(
//                    activity,
//                    getSongList(selection),
//                    menuItem.itemId
//                )
            }
        }
    }

    inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {
        init {
            menu?.setOnClickListener { view ->
                val popupMenu = PopupMenu(activity, view)
                popupMenu.inflate(R.menu.menu_item_playlist)
                popupMenu.setOnMenuItemClickListener { item ->
//                    PlaylistMenuHelper.handleMenuClick(activity, dataSet[layoutPosition], item)
                    return@setOnMenuItemClickListener true
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
                val transitionView = imageContainer ?: return
                onClick.invoke(transitionView, dataSet[layoutPosition].id)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            toggleChecked(layoutPosition)
            return true
        }
    }

    companion object {
        val TAG: String = LibraryPlaylistAdapter::class.java.simpleName
    }
}
