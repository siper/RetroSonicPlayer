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
package ru.stersh.retrosonic.feature.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.player.queue.PlayerQueueManager

class MainViewModel(private val playerQueueManager: PlayerQueueManager) : ViewModel() {
    private val _showMiniPlayer = MutableStateFlow(false)
    val showMiniPlayer: Flow<Boolean>
        get() = _showMiniPlayer

    init {
        viewModelScope.launch {
            playerQueueManager
                .getQueue()
                .map { it.isNotEmpty() }
                .collect { _showMiniPlayer.value = it }
        }
    }
}
