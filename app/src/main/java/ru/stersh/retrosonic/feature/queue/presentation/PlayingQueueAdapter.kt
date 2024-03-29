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
package ru.stersh.retrosonic.feature.queue.presentation

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.draggable.annotation.DraggableItemStateFlags
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem
import me.zhanghai.android.fastscroll.PopupTextProvider
import ru.stersh.retrosonic.EXTRA_ALBUM_ID
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.adapter.base.AbsMultiSelectAdapter
import ru.stersh.retrosonic.adapter.base.MediaEntryViewHolder
import ru.stersh.retrosonic.glide.GlideApp
import ru.stersh.retrosonic.model.Song
import ru.stersh.retrosonic.util.MusicUtil
import ru.stersh.retrosonic.util.PreferenceUtil
import ru.stersh.retrosonic.util.RetroUtil
import ru.stersh.retrosonic.util.ViewUtil
import ru.stersh.retrosonic.util.color.MediaNotificationProcessor

/**
 * Created by hemanths on 13/08/17.
 */

class PlayingQueueAdapter(
    override val activity: FragmentActivity,
    private val onMoveItem: (from: Int, to: Int) -> Unit,
    private val onRemoveItem: (position: Int) -> Unit,
    private val onSongClick: (position: Int) -> Unit,
) : AbsMultiSelectAdapter<PlayingQueueAdapter.ViewHolder, QueueSongUi>(activity, R.menu.menu_media_selection),
    DraggableItemAdapter<PlayingQueueAdapter.ViewHolder>,
    SwipeableItemAdapter<PlayingQueueAdapter.ViewHolder>,
    PopupTextProvider {
    private var dataSet: MutableList<QueueSongUi> = mutableListOf()

    private var current: Int = 0

    init {
        this.setHasStableIds(true)
    }

    private var songToRemove: Song? = null

    override fun getItemViewType(position: Int): Int {
        return if (position == current) {
            VIEW_TYPE_PLAYING
        } else {
            VIEW_TYPE_DEFAULT
        }
    }

    fun setCurrent(current: Int) {
        this.current = current
        notifyDataSetChanged()
    }

    private fun setAlpha(holder: ViewHolder, alpha: Float) {
        holder.image?.alpha = alpha
        holder.title?.alpha = alpha
        holder.text?.alpha = alpha
        holder.paletteColorContainer?.alpha = alpha
        holder.dragView?.alpha = alpha
        holder.menu?.alpha = alpha
    }

    override fun getPopupText(position: Int): String {
        return MusicUtil.getSectionName(dataSet[position].title)
    }

    override fun onCheckCanStartDrag(holder: ViewHolder, position: Int, x: Int, y: Int): Boolean {
        return ViewUtil.hitTest(holder.imageText, x, y) || ViewUtil.hitTest(
            holder.dragView,
            x,
            y,
        )
    }

    override fun onGetItemDraggableRange(holder: ViewHolder, position: Int): ItemDraggableRange? {
        return null
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        onMoveItem.invoke(fromPosition, toPosition)
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
        return true
    }

    override fun onItemDragStarted(position: Int) {
        notifyDataSetChanged()
    }

    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
        notifyDataSetChanged()
    }

    fun setSongToRemove(song: Song) {
        songToRemove = song
    }

    override fun onSwipeItem(holder: ViewHolder, position: Int, result: Int): SwipeResultAction {
        return if (result == SwipeableItemConstants.RESULT_CANCELED) {
            SwipeResultActionDefault()
        } else {
            SwipedResultActionRemoveItem(position)
        }
    }

    override fun onGetSwipeReactionType(holder: ViewHolder, position: Int, x: Int, y: Int): Int {
        return if (onCheckCanStartDrag(holder, position, x, y)) {
            SwipeableItemConstants.REACTION_CAN_NOT_SWIPE_BOTH_H
        } else {
            SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H
        }
    }

    override fun onSwipeItemStarted(holder: ViewHolder, p1: Int) {
    }

    override fun onSetSwipeBackground(holder: ViewHolder, position: Int, result: Int) {
    }

    internal inner class SwipedResultActionRemoveItem(private val position: Int) : SwipeResultActionRemoveItem() {
        override fun onPerformAction() {
            onRemoveItem.invoke(position)
        }

        override fun onSlideAnimationEnd() {
            // initializeSnackBar(adapter, position, activity, isPlaying)
//            songToRemove = adapter.dataSet[position]
//            // If song removed was the playing song, then play the next song
//            if (isPlaying(songToRemove!!)) {
//                playNextSong()
//            }
//            // Swipe animation is much smoother when we do the heavy lifting after it's completed
//            adapter.setSongToRemove(songToRemove!!)
//            removeFromQueue(songToRemove!!)
        }
    }

    fun swapDataSet(dataSet: List<QueueSongUi>) {
        this.dataSet = ArrayList(dataSet)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return dataSet[position].id.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(activity).inflate(R.layout.item_queue, parent, false)
        return createViewHolder(view)
    }

    private fun createViewHolder(view: View): ViewHolder {
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = dataSet[position]
        val isChecked = isChecked(song)
        holder.itemView.isActivated = isChecked
        holder.menu?.isGone = isChecked
        holder.title?.text = getSongTitle(song)
        holder.text?.text = getSongText(song)
        holder.text2?.text = getSongText(song)
        loadAlbumCover(song, holder)
        val landscape = RetroUtil.isLandscape
        if ((PreferenceUtil.songGridSize > 2 && !landscape) || (PreferenceUtil.songGridSizeLand > 5 && landscape)) {
            holder.menu?.isVisible = false
        }
        holder.time?.text = MusicUtil.getReadableDurationString(song.duration)
        if (holder.itemViewType == VIEW_TYPE_PLAYING) {
            holder.dummyContainer?.setBackgroundColor(Color.parseColor("#0Dffffff"))
        } else {
            holder.dummyContainer?.setBackgroundColor(Color.TRANSPARENT)
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

    private fun loadAlbumCover(song: QueueSongUi, holder: ViewHolder) {
        if (holder.image == null) {
            return
        }
        if (song.coverArtUrl == null) {
            GlideApp
                .with(activity)
                .load(R.drawable.avd_album)
                .into(holder.image!!)
        } else {
            GlideApp
                .with(activity)
                .load(song.coverArtUrl)
                .into(holder.image!!)
        }
    }

    private fun getSongTitle(song: QueueSongUi): String {
        return song.title
    }

    private fun getSongText(song: QueueSongUi): String? {
        return song.artist
    }

    private fun getSongText2(song: QueueSongUi): String? {
        return song.album
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun getIdentifier(position: Int): QueueSongUi? {
        return dataSet[position]
    }

    override fun getName(model: QueueSongUi): String {
        return model.title
    }

    override fun onMultipleItemAction(menuItem: MenuItem, selection: List<QueueSongUi>) {
//        SongsMenuHelper.handleMenuClick(activity, selection, menuItem.itemId)
    }

    open inner class ViewHolder(itemView: View) : MediaEntryViewHolder(itemView) {
        protected open var songMenuRes: Int = R.menu.menu_item_playing_queue_song
        protected open val song: QueueSongUi
            get() = dataSet[layoutPosition]

        @DraggableItemStateFlags
        private var mDragStateFlags: Int = 0

        init {
            dragView?.isVisible = true
//            menu?.setOnClickListener(object : SongMenuHelper.OnClickSongMenu(activity) {
//                override val song: QueueSongUi
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
                R.id.action_remove_from_playing_queue -> {
//                    removeFromQueue(layoutPosition)
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
                onSongClick.invoke(layoutPosition)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            println("Long click")
            return toggleChecked(layoutPosition)
        }

        @DraggableItemStateFlags
        override fun getDragStateFlags(): Int {
            return mDragStateFlags
        }

        override fun setDragStateFlags(@DraggableItemStateFlags flags: Int) {
            mDragStateFlags = flags
        }

        override fun getSwipeableContainerView(): View {
            return dummyContainer!!
        }
    }

    companion object {
        val TAG: String = PlayingQueueAdapter::class.java.simpleName
        private const val VIEW_TYPE_DEFAULT = 0
        private const val VIEW_TYPE_PLAYING = 1
    }
}
