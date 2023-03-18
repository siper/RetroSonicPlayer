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
package ru.stersh.retrosonic.feature.player.cover.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.feature.player.cover.domain.CoverArtUrlList
import ru.stersh.retrosonic.feature.player.cover.domain.CoverArtUrlsRepository
import ru.stersh.retrosonic.player.queue.PlayerQueueManager

class PlayerAlbumCoverViewModel(
    private val coverArtUrlsRepository: CoverArtUrlsRepository,
    private val playerQueueManager: PlayerQueueManager,
) : ViewModel() {
    private val _coverArtUrls = MutableStateFlow<CoverArtUrlListUi?>(null)
    val coverArtUrls: Flow<CoverArtUrlListUi>
        get() = _coverArtUrls.filterNotNull()

    init {
        viewModelScope.launch {
            coverArtUrlsRepository
                .getCoverArtUrls()
                .map { it.toPresentation() }
                .collect { _coverArtUrls.value = it }
        }
    }

    fun playPosition(position: Int) {
        playerQueueManager.playPosition(position)
    }

    private fun CoverArtUrlList.toPresentation(): CoverArtUrlListUi {
        return CoverArtUrlListUi(list, selectedPosition)
    }
}
