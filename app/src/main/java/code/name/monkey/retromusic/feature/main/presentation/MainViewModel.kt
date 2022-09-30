package code.name.monkey.retromusic.feature.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.player.queue.domain.PlayerQueueManager

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