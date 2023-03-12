package code.name.monkey.retromusic.feature.player.cover.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code.name.monkey.retromusic.feature.player.cover.domain.CoverArtUrlList
import code.name.monkey.retromusic.feature.player.cover.domain.CoverArtUrlsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.player.queue.PlayerQueueManager

class PlayerAlbumCoverViewModel(
    private val coverArtUrlsRepository: CoverArtUrlsRepository,
    private val playerQueueManager: PlayerQueueManager
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