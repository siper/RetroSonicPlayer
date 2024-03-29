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
package ru.stersh.retrosonic.feature.player.controls.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.player.controls.PlayerControls
import ru.stersh.retrosonic.player.metadata.CurrentSongInfoStore
import ru.stersh.retrosonic.player.metadata.SongInfo
import ru.stersh.retrosonic.player.progress.PlayerProgress
import ru.stersh.retrosonic.player.progress.PlayerProgressStore
import ru.stersh.retrosonic.player.state.PlayStateStore

class PlayerPlaybackControlsViewModel(
    private val playerControls: PlayerControls,
    private val playStateStore: PlayStateStore,
    private val currentSongInfoStore: CurrentSongInfoStore,
    private val playerProgressStore: PlayerProgressStore,
) : ViewModel() {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: Flow<Boolean>
        get() = _isPlaying

    private val _songInfo = MutableStateFlow<SongInfoUi?>(null)
    val songInfo: Flow<SongInfoUi>
        get() = _songInfo.filterNotNull()

    private val _progress = MutableStateFlow<PlayerProgressUi?>(null)
    val progress: Flow<PlayerProgressUi>
        get() = _progress.filterNotNull()

    init {
        viewModelScope.launch {
            playStateStore
                .isPlaying()
                .collect { _isPlaying.value = it }
        }
        viewModelScope.launch {
            currentSongInfoStore
                .getCurrentSongInfo()
                .map { it?.toPresentation() }
                .collect { _songInfo.value = it }
        }
        viewModelScope.launch {
            playerProgressStore
                .playerProgress()
                .map { it?.toPresentation() }
                .collect { _progress.value = it }
        }
    }

    fun play() = playerControls.play()

    fun next() = playerControls.next()

    fun previous() = playerControls.previous()

    fun pause() = playerControls.pause()

    fun seek(time: Long) = playerControls.seek(time)

    private fun SongInfo.toPresentation(): SongInfoUi {
        return SongInfoUi(title, artist)
    }

    private fun PlayerProgress.toPresentation(): PlayerProgressUi {
        return PlayerProgressUi(
            total = totalTime,
            current = currentTime,
            totalMs = totalTimeMs,
            currentMs = currentTimeMs,
        )
    }
}
