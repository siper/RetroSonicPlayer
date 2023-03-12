package code.name.monkey.retromusic.feature.settings.servers.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code.name.monkey.retromusic.feature.settings.servers.domain.ServersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.core.extensions.mapItems


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
                        isActive = it.isActive
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