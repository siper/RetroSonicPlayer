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
import android.media.MediaScannerConnection
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import code.name.monkey.appthemehelper.util.VersionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.stersh.retrosonic.EXTRA_PLAYLIST
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.extensions.colorButtons
import ru.stersh.retrosonic.extensions.createNewFile
import ru.stersh.retrosonic.extensions.extraNotNull
import ru.stersh.retrosonic.extensions.materialDialog
import ru.stersh.retrosonic.extensions.showToast
import ru.stersh.retrosonic.helper.M3UWriter
import ru.stersh.retrosonic.util.PlaylistsUtil

class SavePlaylistDialog : DialogFragment() {
    companion object {
        fun create(playlistWithSongs: ru.stersh.apisonic.room.playlist.PlaylistWithSongs): SavePlaylistDialog {
            return SavePlaylistDialog().apply {
                arguments = bundleOf(
                    EXTRA_PLAYLIST to playlistWithSongs,
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val playlistWithSongs = extraNotNull<ru.stersh.apisonic.room.playlist.PlaylistWithSongs>(EXTRA_PLAYLIST).value

        if (VersionUtils.hasR()) {
            createNewFile(
                "audio/mpegurl",
                playlistWithSongs.playlistEntity.playlistName,
            ) { outputStream, data ->
                try {
                    if (outputStream != null) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            M3UWriter.writeIO(
                                outputStream,
                                playlistWithSongs,
                            )
                            withContext(Dispatchers.Main) {
                                showToast(
                                    requireContext().getString(
                                        R.string.saved_playlist_to,
                                        data?.lastPathSegment,
                                    ),
                                    Toast.LENGTH_LONG,
                                )
                                dismiss()
                            }
                        }
                    }
                } catch (e: Exception) {
                    showToast(
                        "Something went wrong : " + e.message,
                    )
                }
            }
        } else {
            lifecycleScope.launch(Dispatchers.IO) {
                val file = PlaylistsUtil.savePlaylistWithSongs(playlistWithSongs)
                MediaScannerConnection.scanFile(
                    requireActivity(),
                    arrayOf<String>(file.path),
                    null,
                ) { _, _ ->
                }
                withContext(Dispatchers.Main) {
                    showToast(
                        getString(R.string.saved_playlist_to, file),
                        Toast.LENGTH_LONG,
                    )
                    dismiss()
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return materialDialog(R.string.save_playlist_title)
            .setView(R.layout.loading)
            .create().colorButtons()
    }
}
