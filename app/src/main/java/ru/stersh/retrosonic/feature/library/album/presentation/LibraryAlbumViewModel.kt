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
package ru.stersh.retrosonic.feature.library.album.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.core.extensions.mapItems
import ru.stersh.retrosonic.feature.library.album.domain.LibraryAlbum
import ru.stersh.retrosonic.feature.library.album.domain.LibraryAlbumRepository

class LibraryAlbumViewModel(private val repository: LibraryAlbumRepository) : ViewModel() {
    private val _albums = MutableStateFlow<List<LibraryAlbumUi>>(emptyList())
    val albums: Flow<List<LibraryAlbumUi>>
        get() = _albums

    init {
        viewModelScope.launch {
            repository
                .getAlbums()
                .mapItems { it.toPresentation() }
                .collect {
                    _albums.value = it
                }
        }
    }

    private fun LibraryAlbum.toPresentation(): LibraryAlbumUi {
        return LibraryAlbumUi(id, title, artist, coverUrl, year)
    }
}
