package code.name.monkey.retromusic.feature.queue.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.core.extensions.mapItems
import ru.stersh.retrosonic.player.queue.PlayerQueueManager
import ru.stersh.retrosonic.player.utils.MEDIA_ITEM_ALBUM_ID
import ru.stersh.retrosonic.player.utils.MEDIA_ITEM_DURATION

class PlayingQueueViewModel(private val playerQueueManager: PlayerQueueManager) : ViewModel() {
    private val _queue = MutableStateFlow<List<QueueSongUi>>(emptyList())
    val queue: Flow<List<QueueSongUi>>
        get() = _queue

    private val _position = MutableStateFlow<Int>(0)
    val position: Flow<Int>
        get() = _position

    init {
        viewModelScope.launch {
            playerQueueManager
                .getQueue()
                .mapItems { it.toPresentation() }
                .collect {
                    _queue.value = it
                }
        }
        viewModelScope.launch {
            playerQueueManager
                .currentPlayingItemPosition()
                .collect {
                    _position.value = it
                }
        }
    }

    fun clearQueue() = playerQueueManager.clearQueue()

    fun playSongOnPosition(position: Int) = playerQueueManager.playPosition(position)

    fun moveSong(from: Int, to: Int) = playerQueueManager.moveSong(from, to)

    fun removeSong(position: Int) = playerQueueManager.removeSong(position)

    private fun MediaItem.toPresentation(): QueueSongUi {
        return QueueSongUi(
            id = mediaId,
            title = mediaMetadata.title.toString(),
            artist = mediaMetadata.artist?.toString(),
            album = mediaMetadata.albumTitle?.toString(),
            coverArtUrl = mediaMetadata.artworkUri?.toString(),
            duration = requireNotNull(mediaMetadata.extras?.getLong(MEDIA_ITEM_DURATION)),
            albumId = mediaMetadata.extras?.getString(MEDIA_ITEM_ALBUM_ID)
        )
    }
}