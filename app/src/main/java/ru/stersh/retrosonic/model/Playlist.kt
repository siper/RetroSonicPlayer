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
package ru.stersh.retrosonic.model

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import ru.stersh.retrosonic.repository.RealPlaylistRepository
import ru.stersh.retrosonic.util.MusicUtil

@Parcelize
open class Playlist(
    val id: Long,
    val name: String,
) : Parcelable, KoinComponent {

    companion object {
        val empty = Playlist(-1, "")
    }

    // this default implementation covers static playlists
    fun getSongs(): List<Song> {
        return RealPlaylistRepository(get()).playlistSongs(id)
    }

    open fun getInfoString(context: Context): String {
        val songCount = getSongs().size
        val songCountString = MusicUtil.getSongCountString(context, songCount)
        return MusicUtil.buildInfoString(
            songCountString,
            "",
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Playlist

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
