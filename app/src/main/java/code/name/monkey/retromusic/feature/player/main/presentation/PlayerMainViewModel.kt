package code.name.monkey.retromusic.feature.player.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.player.controls.PlayerControls
import ru.stersh.retrosonic.player.metadata.CurrentSongInfoStore

internal class PlayerMainViewModel(
    private val currentSongInfoStore: CurrentSongInfoStore,
    private val playerControls: PlayerControls
) : ViewModel() {
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: Flow<Boolean>
        get() = _isFavorite

    init {
        viewModelScope.launch {
            currentSongInfoStore
                .getCurrentSongInfo()
                .collect { _isFavorite.value = it?.favorite == true }
        }
    }

    fun toggleFavorite(favorite: Boolean) = viewModelScope.launch {
        playerControls.toggleFavorite(favorite)
    }
}