package code.name.monkey.retromusic.feature.player.cover.data

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import code.name.monkey.retromusic.feature.player.cover.domain.CoverArtUrlList
import code.name.monkey.retromusic.feature.player.cover.domain.CoverArtUrlsRepository
import ru.stersh.retrosonic.player.android.MusicService
import ru.stersh.retrosonic.player.utils.mediaControllerFuture
import ru.stersh.retrosonic.player.utils.mediaItems
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CoverArtUrlsRepositoryImpl(private val context: Context) : CoverArtUrlsRepository {

    override fun getCoverArtUrls(): Flow<CoverArtUrlList> = callbackFlow {
        val mediaControllerFuture = mediaControllerFuture(context, MusicService::class.java)

        var player: Player? = null
        var callback: Player.Listener? = null

        mediaControllerFuture.addListener(
            {
                val sessionPlayer = mediaControllerFuture.get()
                player = sessionPlayer
                var lastItems = getNewCoverArtUrlList(sessionPlayer)

                trySend(lastItems)

                val sessionCallback = object : Player.Listener {
                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                        val newItems = getNewCoverArtUrlList(sessionPlayer)
                        if (lastItems != newItems) {
                            lastItems = newItems
                            trySend(newItems)
                        }
                    }
                }
                callback = sessionCallback
                sessionPlayer.addListener(sessionCallback)
            },
            ContextCompat.getMainExecutor(context)
        )

        awaitClose {
            val c = callback ?: return@awaitClose
            player?.removeListener(c)
        }
    }

    private fun getNewCoverArtUrlList(player: Player): CoverArtUrlList {
        return CoverArtUrlList(
            list = player.mediaItems.map { it.mediaMetadata.artworkUri.toString() },
            selectedPosition = player.currentMediaItemIndex
        )
    }
}