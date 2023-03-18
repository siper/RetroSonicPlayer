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
package ru.stersh.retrosonic.feature.settings.server.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.feature.settings.server.domain.ServerSettingsRepository
import timber.log.Timber

internal class ServerSettingsViewModel(
    private val serverSettingsId: Long?,
    private val serverSettingsRepository: ServerSettingsRepository,
) : ViewModel() {
    private val _title = MutableStateFlow<String?>(null)
    val title: Flow<String?>
        get() = _title

    private val _address = MutableStateFlow<String?>(null)
    val address: Flow<String?>
        get() = _address

    private val _username = MutableStateFlow<String?>(null)
    val username: Flow<String?>
        get() = _username

    private val _password = MutableStateFlow<String?>(null)
    val password: Flow<String?>
        get() = _password

    private val _isActive = MutableStateFlow<Boolean>(true)
    val isActive: Flow<Boolean>
        get() = _isActive

    private val _useLegacyAuth = MutableStateFlow<Boolean>(false)
    val useLegacyAuth: Flow<Boolean>
        get() = _useLegacyAuth

    private val _isFirstServer = MutableStateFlow<Boolean>(false)
    val isFirstServer: Flow<Boolean>
        get() = _isFirstServer

    private val _submitButtonState = MutableStateFlow<SubmitButtonStateUi>(SubmitButtonStateUi.TEST)
    val submitButtonState: Flow<SubmitButtonStateUi>
        get() = _submitButtonState

    private val _errors = Channel<String>()
    val errors: Flow<String>
        get() = _errors.receiveAsFlow()

    private val _messages = Channel<MessageUi>()
    val messages: Flow<MessageUi>
        get() = _messages.receiveAsFlow()

    init {
        loadSettings()
        loadFirstServer()
    }

    fun saveServer(serverSettings: ServerSettingsUi) = viewModelScope.launch {
        serverSettingsRepository.saveSettings(serverSettings.toDomain(serverSettingsId))
    }

    fun resetButtonState() {
        _submitButtonState.value = SubmitButtonStateUi.TEST
    }

    fun testServer(serverSettings: ServerSettingsUi) = viewModelScope.launch {
        _submitButtonState.value = SubmitButtonStateUi.TESTING
        runCatching { serverSettingsRepository.testServerSettings(serverSettings.toDomain(serverSettingsId)) }.fold(
            onSuccess = {
                _submitButtonState.value = SubmitButtonStateUi.SAVE
                _messages.send(MessageUi.SUCCESS_TEST_SERVER)
            },
            onFailure = {
                Timber.w(it)
                _submitButtonState.value = SubmitButtonStateUi.TEST
                val message = it.message ?: return@fold
                _errors.send(message)
            },
        )
    }

    private fun loadFirstServer() = viewModelScope.launch {
        _isFirstServer.value = serverSettingsRepository.isFirstServer(serverSettingsId)
    }

    private fun loadSettings() = viewModelScope.launch {
        if (serverSettingsId == null) {
            return@launch
        }
        val serverSettings = serverSettingsRepository.getSettings(serverSettingsId) ?: return@launch
        _title.value = serverSettings.title
        _address.value = serverSettings.address
        _username.value = serverSettings.username
        _password.value = serverSettings.password
        _isActive.value = serverSettings.isActive
    }
}
