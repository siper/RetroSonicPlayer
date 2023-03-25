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
package ru.stersh.retrosonic.feature.mylibrary.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.feature.mylibrary.domain.Album
import ru.stersh.retrosonic.feature.mylibrary.domain.DiscoverAlbumsRepository
import ru.stersh.retrosonic.feature.mylibrary.domain.UserRepository

internal class MyLibraryViewModel(
    private val userRepository: UserRepository,
    private val discoverAlbumsRepository: DiscoverAlbumsRepository
) : ViewModel() {

    private val _username = MutableStateFlow<String?>(null)
    val username: Flow<String>
        get() = _username.filterNotNull()

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: Flow<String>
        get() = _avatarUrl.filterNotNull()

    private val _items = MutableStateFlow<List<MyLibraryItemUi>>(emptyList())
    val items: Flow<List<MyLibraryItemUi>>
        get() = _items

    init {
        viewModelScope.launch {
            userRepository
                .getUsername()
                .collect {
                    _username.value = it
                }
        }
        viewModelScope.launch {
            userRepository
                .getAvatarUrl()
                .collect {
                    _avatarUrl.value = it
                }
        }
        viewModelScope.launch {
            coroutineScope {
                val data = mutableListOf<MyLibraryItemUi>()
                val recentlyAdded = async { discoverAlbumsRepository.getRecentlyAdded() }
                val recentlyPlayed = async { discoverAlbumsRepository.getRecentlyPlayed() }
                val mostPlayed = async { discoverAlbumsRepository.getMostPlayed() }
                val random = async { discoverAlbumsRepository.getRandom() }
                val recentlyAddedData = recentlyAdded.await()
                if (recentlyAddedData.isNotEmpty()) {
                    data.add(SectionUi(R.string.discover_albums_recently_added_title))
                    data.addAll(recentlyAddedData.map { it.toUi() })
                }
                val recentlyPlayedData = recentlyPlayed.await()
                if (recentlyPlayedData.isNotEmpty()) {
                    data.add(SectionUi(R.string.discover_albums_recently_played_title))
                    data.addAll(recentlyPlayedData.map { it.toUi() })
                }
                val mostPlayedData = mostPlayed.await()
                if (mostPlayedData.isNotEmpty()) {
                    data.add(SectionUi(R.string.discover_albums_most_played_title))
                    data.addAll(mostPlayedData.map { it.toUi() })
                }
                val randomData = random.await()
                if (randomData.isNotEmpty()) {
                    data.add(SectionUi(R.string.discover_albums_random_title))
                    data.addAll(randomData.map { it.toUi() })
                }
                _items.value = data.toList()
            }
        }
    }

    private fun Album.toUi(): AlbumUi {
        return AlbumUi(
            id = id,
            title = title,
            coverUrl = coverUrl
        )
    }
}
