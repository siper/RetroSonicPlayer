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
package ru.stersh.retrosonic.feature.library.playlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.core.extensions.mapItems
import ru.stersh.retrosonic.feature.library.playlist.domain.LibraryPlaylist
import ru.stersh.retrosonic.feature.library.playlist.domain.LibraryPlaylistRepository

class LibraryPlaylistViewModel(private val libraryPlaylistRepository: LibraryPlaylistRepository) : ViewModel() {
    private val _playlists = MutableStateFlow<List<LibraryPlaylistUi>>(emptyList())
    val playlist: Flow<List<LibraryPlaylistUi>>
        get() = _playlists

    init {
        viewModelScope.launch {
            libraryPlaylistRepository
                .getPlaylists()
                .mapItems { it.toPresentation() }
                .collect { _playlists.value = it }
        }
    }

    private fun LibraryPlaylist.toPresentation(): LibraryPlaylistUi {
        return LibraryPlaylistUi(id, title, coverArtUrl, songCount, duration)
    }
}
