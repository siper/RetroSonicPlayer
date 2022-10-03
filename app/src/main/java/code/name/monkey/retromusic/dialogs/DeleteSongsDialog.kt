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
package code.name.monkey.retromusic.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.text.parseAsHtml
import androidx.fragment.app.DialogFragment
import code.name.monkey.appthemehelper.util.VersionUtils
import code.name.monkey.retromusic.EXTRA_SONG
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.activities.saf.SAFGuideActivity
import code.name.monkey.retromusic.extensions.extraNotNull
import code.name.monkey.retromusic.extensions.materialDialog
import code.name.monkey.retromusic.fragments.LibraryViewModel
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.util.MusicUtil
import code.name.monkey.retromusic.util.SAFUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class DeleteSongsDialog : DialogFragment() {
    lateinit var libraryViewModel: LibraryViewModel

    companion object {
        fun create(songId: String): DeleteSongsDialog {
            val list = ArrayList<String>()
            list.add(songId)
            return create(list)
        }

        fun create(songIds: List<String>): DeleteSongsDialog {
            return DeleteSongsDialog().apply {
                arguments = bundleOf(
                    EXTRA_SONG to ArrayList(songIds)
                )
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        libraryViewModel = activity?.getViewModel() as LibraryViewModel
        val songs = extraNotNull<List<String>>(EXTRA_SONG).value
        if (VersionUtils.hasR()) {
            val deleteResultLauncher =
                registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
//                        if ((songs.size == 1) && MusicPlayerRemote.isPlaying(songs[0])) {
//                            MusicPlayerRemote.playNextSong()
//                        }
//                        MusicPlayerRemote.removeFromQueue(songs)
                        reloadTabs()
                    }
                    dismiss()
                }
            // TODO: Delete
//            val pendingIntent = MediaStore.createDeleteRequest(requireActivity().contentResolver, songs.map {
//                    MusicUtil.getSongFileUri(it)
//                })
//            deleteResultLauncher.launch(
//                IntentSenderRequest.Builder(pendingIntent.intentSender).build()
//            )
            return super.onCreateDialog(savedInstanceState)
        } else {
            val pair = Pair(
                R.string.delete_songs_title,
                String.format(getString(R.string.delete_x_songs), songs.size).parseAsHtml()
            )
            // TODO: delete dialog
//            val pair = if (songs.size > 1) {
//                Pair(
//                    R.string.delete_songs_title,
//                    String.format(getString(R.string.delete_x_songs), songs.size).parseAsHtml()
//                )
//            } else {
//                Pair(
//                    R.string.delete_song_title,
//                    String.format(getString(R.string.delete_song_x), songs[0].title).parseAsHtml()
//                )
//            }

            return materialDialog()
                .title(pair.first)
                .message(text = pair.second)
                .noAutoDismiss()
                .negativeButton(android.R.string.cancel)
                {
                    dismiss()
                }
                .positiveButton(R.string.action_delete)
                {
//                    if ((songs.size == 1) && MusicPlayerRemote.isPlaying(songs[0])) {
//                        MusicPlayerRemote.playNextSong()
//                    }
//                    if (!SAFUtil.isSAFRequiredForSongs(songs)) {
//                        CoroutineScope(Dispatchers.IO).launch {
//                            dismiss()
//                            MusicUtil.deleteTracks(requireContext(), songs)
//                            reloadTabs()
//                        }
//                    } else {
//                        if (SAFUtil.isSDCardAccessGranted(requireActivity())) {
//                            deleteSongs(songs)
//                        } else {
//                            startActivityForResult(
//                                Intent(requireActivity(), SAFGuideActivity::class.java),
//                                SAFGuideActivity.REQUEST_CODE_SAF_GUIDE
//                            )
//                        }
//                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SAFGuideActivity.REQUEST_CODE_SAF_GUIDE -> {
                SAFUtil.openTreePicker(this)
            }
            SAFUtil.REQUEST_SAF_PICK_TREE,
            SAFUtil.REQUEST_SAF_PICK_FILE -> {
                if (resultCode == Activity.RESULT_OK) {
                    SAFUtil.saveTreeUri(requireActivity(), data)
                    val songs = extraNotNull<List<Song>>(EXTRA_SONG).value
                    deleteSongs(songs)
                }
            }
        }
    }

    fun deleteSongs(songs: List<Song>) {
        CoroutineScope(Dispatchers.IO).launch {
            dismiss()
            MusicUtil.deleteTracks(requireActivity(), songs, null, null)
            reloadTabs()
        }
    }

    private fun reloadTabs() {


    }
}
