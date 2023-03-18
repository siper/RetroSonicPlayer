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
package ru.stersh.retrosonic.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.text.parseAsHtml
import androidx.fragment.app.DialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import ru.stersh.retrosonic.EXTRA_SONG
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.extensions.colorButtons
import ru.stersh.retrosonic.extensions.extraNotNull
import ru.stersh.retrosonic.extensions.materialDialog
import ru.stersh.retrosonic.fragments.LibraryViewModel

class RemoveSongFromPlaylistDialog : DialogFragment() {
    private val libraryViewModel by sharedViewModel<LibraryViewModel>()

    companion object {
        fun create(song: ru.stersh.apisonic.room.playlist.SongEntity): RemoveSongFromPlaylistDialog {
            val list = mutableListOf<ru.stersh.apisonic.room.playlist.SongEntity>()
            list.add(song)
            return create(list)
        }

        fun create(songs: List<ru.stersh.apisonic.room.playlist.SongEntity>): RemoveSongFromPlaylistDialog {
            return RemoveSongFromPlaylistDialog().apply {
                arguments = bundleOf(
                    EXTRA_SONG to songs,
                )
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val songs = extraNotNull<List<ru.stersh.apisonic.room.playlist.SongEntity>>(EXTRA_SONG).value
        val pair = if (songs.size > 1) {
            Pair(
                R.string.remove_songs_from_playlist_title,
                String.format(getString(R.string.remove_x_songs_from_playlist), songs.size)
                    .parseAsHtml(),
            )
        } else {
            Pair(
                R.string.remove_song_from_playlist_title,
                String.format(
                    getString(R.string.remove_song_x_from_playlist),
                    songs[0].title,
                ).parseAsHtml(),
            )
        }
        return materialDialog(pair.first)
            .setMessage(pair.second)
            .setPositiveButton(R.string.remove_action) { _, _ ->
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            .colorButtons()
    }
}
