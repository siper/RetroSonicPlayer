package code.name.monkey.retromusic.feature.settings.server.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code.name.monkey.retromusic.feature.settings.server.domain.ServerSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class ServerSettingsViewModel(
    private val serverSettingsId: Long?,
    private val serverSettingsRepository: ServerSettingsRepository
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
    private val _isActive = MutableStateFlow<Boolean>(false)
    val isActive: Flow<Boolean>
        get() = _isActive
    private val _isFirstServer = MutableStateFlow<Boolean>(false)
    val isFirstServer: Flow<Boolean>
        get() = _isFirstServer

    init {
        loadSettings()
        loadFirstServer()
    }

    fun saveServer(serverSettings: ServerSettingsUi) = viewModelScope.launch {
        serverSettingsRepository.saveSettings(serverSettings.toDomain(serverSettingsId))
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