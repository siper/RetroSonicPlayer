package code.name.monkey.retromusic.feature.player.mini

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

class MiniPlayerViewModel(
    private val playerControls: PlayerControls,
    private val playStateStore: PlayStateStore,
    private val currentSongInfoStore: CurrentSongInfoStore,
    private val playerProgressStore: PlayerProgressStore
) : ViewModel() {

    private val _progress = MutableStateFlow<SongProgressUi?>(null)
    val progress: Flow<SongProgressUi>
        get() = _progress.filterNotNull()

    private val _info = MutableStateFlow<SongInfoUi?>(null)
    val info: Flow<SongInfoUi>
        get() = _info.filterNotNull()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: Flow<Boolean>
        get() = _isPlaying

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
                .collect { _info.value = it }
        }
        viewModelScope.launch {
            playerProgressStore
                .playerProgress()
                .map { it?.toPresentation() }
                .collect { _progress.value = it }
        }
    }

    fun next() = playerControls.next()

    fun previous() = playerControls.previous()

    fun play() = playerControls.play()

    fun pause() = playerControls.pause()

    private fun SongInfo.toPresentation(): SongInfoUi {
        return SongInfoUi(
            title = title,
            artist = artist,
            coverArtUrl = coverArtUrl
        )
    }

    private fun PlayerProgress.toPresentation(): SongProgressUi {
        return SongProgressUi(
            total = totalTimeMs,
            current = currentTimeMs
        )
    }
}