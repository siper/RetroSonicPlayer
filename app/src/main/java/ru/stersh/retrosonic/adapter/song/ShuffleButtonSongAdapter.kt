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
package ru.stersh.retrosonic.adapter.song

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.google.android.material.button.MaterialButton
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.extensions.accentColor
import ru.stersh.retrosonic.extensions.accentOutlineColor
import ru.stersh.retrosonic.model.Song
import ru.stersh.retrosonic.util.PreferenceUtil
import ru.stersh.retrosonic.util.RetroUtil

class ShuffleButtonSongAdapter(
    activity: FragmentActivity,
    dataSet: MutableList<Song>,
    itemLayoutRes: Int,
) : AbsOffsetSongAdapter(activity, dataSet, itemLayoutRes) {

    override fun createViewHolder(view: View): SongAdapter.ViewHolder {
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) OFFSET_ITEM else SONG
    }

    override fun onBindViewHolder(holder: SongAdapter.ViewHolder, position: Int) {
        if (holder.itemViewType == OFFSET_ITEM) {
            val viewHolder = holder as ViewHolder
            viewHolder.playAction?.let {
                it.setOnClickListener {
//                    MusicPlayerRemote.openQueue(dataSet, 0, true)
                }
                it.accentOutlineColor()
            }
            viewHolder.shuffleAction?.let {
                it.setOnClickListener {
//                    MusicPlayerRemote.openAndShuffleQueue(dataSet, true)
                }
                it.accentColor()
            }
        } else {
            super.onBindViewHolder(holder, position - 1)
            val landscape = RetroUtil.isLandscape
            if ((PreferenceUtil.songGridSize > 2 && !landscape) || (PreferenceUtil.songGridSizeLand > 5 && landscape)) {
                holder.menu?.isVisible = false
            }
        }
    }

    inner class ViewHolder(itemView: View) : AbsOffsetSongAdapter.ViewHolder(itemView) {
        val playAction: MaterialButton? = itemView.findViewById(R.id.playAction)
        val shuffleAction: MaterialButton? = itemView.findViewById(R.id.shuffleAction)

        override fun onClick(v: View?) {
            if (itemViewType == OFFSET_ITEM) {
//                MusicPlayerRemote.openAndShuffleQueue(dataSet, true)
                return
            }
            super.onClick(v)
        }
    }
}
