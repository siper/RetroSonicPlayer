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
package ru.stersh.retrosonic.feature.settings.servers.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.core.extensions.mapItems
import ru.stersh.retrosonic.feature.settings.servers.domain.ServersRepository

internal class ServersViewModel(private val serversRepository: ServersRepository) : ViewModel() {

    private val _servers = MutableStateFlow<List<ServerUi>>(emptyList())
    val servers: Flow<List<ServerUi>>
        get() = _servers

    init {
        viewModelScope.launch {
            serversRepository
                .getServers()
                .mapItems {
                    ServerUi(
                        id = it.id,
                        title = it.title,
                        url = it.url,
                        isActive = it.isActive,
                    )
                }
                .collect {
                    _servers.value = it
                }
        }
    }

    fun setActive(id: Long) = viewModelScope.launch {
        serversRepository.setActive(id)
    }

    fun delete(id: Long) = viewModelScope.launch {
        serversRepository.delete(id)
    }
}
