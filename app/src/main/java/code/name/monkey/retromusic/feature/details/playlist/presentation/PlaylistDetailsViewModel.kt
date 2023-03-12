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
package code.name.monkey.retromusic.feature.details.playlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code.name.monkey.retromusic.feature.details.playlist.domain.PlaylistDetails
import code.name.monkey.retromusic.feature.details.playlist.domain.PlaylistDetailsRepository
import code.name.monkey.retromusic.feature.details.playlist.domain.PlaylistSong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.core.extensions.mapItems
import ru.stersh.retrosonic.player.queue.AudioSource
import ru.stersh.retrosonic.player.queue.PlayerQueueAudioSourceManager

class PlaylistDetailsViewModel(
    private val playlistId: String,
    private val playlistDetailsRepository: PlaylistDetailsRepository,
    private val playerQueueAudioSourceManager: PlayerQueueAudioSourceManager
) : ViewModel() {
    private val _playlistDetails = MutableStateFlow<PlaylistDetailsUi?>(null)
    val playlistDetails: Flow<PlaylistDetailsUi>
        get() = _playlistDetails.filterNotNull()

    private val _playlistSongs = MutableStateFlow<List<PlaylistSongUi>>(emptyList())
    val playlistSongs: Flow<List<PlaylistSongUi>>
        get() = _playlistSongs

    init {
        viewModelScope.launch {
            playlistDetailsRepository
                .getPlaylistDetails(playlistId)
                .map { it.toPresentation() }
                .collect { _playlistDetails.value = it }
        }
        viewModelScope.launch {
            playlistDetailsRepository
                .getPlaylistSongs(playlistId)
                .mapItems { it.toPresentation() }
                .collect { _playlistSongs.value = it }
        }
    }

    fun playAll() = viewModelScope.launch {
        playerQueueAudioSourceManager.playSource(AudioSource.Playlist(playlistId))
    }

    fun playShuffled() = viewModelScope.launch {
        playerQueueAudioSourceManager.playSource(AudioSource.Playlist(playlistId), true)
    }

    fun playSong(songId: String) = viewModelScope.launch {
        playerQueueAudioSourceManager.playSource(AudioSource.Song(songId))
    }

    private fun PlaylistDetails.toPresentation(): PlaylistDetailsUi {
        return PlaylistDetailsUi(title, coverArtUrl, songCount, duration)
    }

    private fun PlaylistSong.toPresentation(): PlaylistSongUi {
        return PlaylistSongUi(id, title, trackNumber, year, duration, coverArtUrl, albumId, album, artistId, artist)
    }
}
