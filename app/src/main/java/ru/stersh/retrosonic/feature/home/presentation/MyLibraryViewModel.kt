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
package ru.stersh.retrosonic.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.feature.home.domain.UserRepository

internal class MyLibraryViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _username = MutableStateFlow<String?>(null)
    val username: Flow<String>
        get() = _username.filterNotNull()

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: Flow<String>
        get() = _avatarUrl.filterNotNull()

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
    }
}
