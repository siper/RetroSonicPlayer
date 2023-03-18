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
package ru.stersh.retrosonic.feature.player.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.player.controls.PlayerControls
import ru.stersh.retrosonic.player.metadata.CurrentSongInfoStore

internal class PlayerMainViewModel(
    private val currentSongInfoStore: CurrentSongInfoStore,
    private val playerControls: PlayerControls,
) : ViewModel() {
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: Flow<Boolean>
        get() = _isFavorite

    init {
        viewModelScope.launch {
            currentSongInfoStore
                .getCurrentSongInfo()
                .collect { _isFavorite.value = it?.favorite == true }
        }
    }

    fun toggleFavorite(favorite: Boolean) = viewModelScope.launch {
        playerControls.toggleFavorite(favorite)
    }
}
