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
package ru.stersh.retrosonic.feature.library.artist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.core.extensions.mapItems
import ru.stersh.retrosonic.feature.library.artist.domain.LibraryArtist
import ru.stersh.retrosonic.feature.library.artist.domain.LibraryArtistRepository

class LibraryArtistViewModel(private val repository: LibraryArtistRepository) : ViewModel() {
    private val _artists = MutableStateFlow<List<LibraryArtistUi>>(emptyList())
    val artists: Flow<List<LibraryArtistUi>>
        get() = _artists

    init {
        viewModelScope.launch {
            repository
                .getArtists()
                .mapItems { it.toPresentation() }
                .collect { _artists.value = it }
        }
    }

    private fun LibraryArtist.toPresentation(): LibraryArtistUi {
        return LibraryArtistUi(id, title, coverArtUrl)
    }
}
