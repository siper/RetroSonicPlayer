package ru.stersh.retrosonic.player.queue.impl

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ru.stersh.retrosonic.player.android.MusicService
import ru.stersh.retrosonic.player.queue.PlayerQueueManager
import ru.stersh.retrosonic.player.utils.mediaControllerFuture
import ru.stersh.retrosonic.player.utils.mediaItems
import ru.stersh.retrosonic.player.utils.withPlayer

class PlayerQueueManagerImpl(private val context: Context) : PlayerQueueManager {
    private val mainExecutor = ContextCompat.getMainExecutor(context)

    override fun currentPlayingItemPosition(): Flow<Int> = callbackFlow {
        val controller = mediaControllerFuture(context, MusicService::class.java)

        var listener: Player.Listener? = null
        var player: Player? = null

        controller.withPlayer(mainExecutor) {
            trySend(currentMediaItemIndex)

            val sessionListener = object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    if (events.containsAny(
                            Player.EVENT_TIMELINE_CHANGED,
                            Player.EVENT_IS_PLAYING_CHANGED,
                            Player.EVENT_MEDIA_METADATA_CHANGED,
                            Player.EVENT_METADATA
                        )
                    ) {
                        trySend(currentMediaItemIndex)
                    }
                }
            }
            addListener(sessionListener)

            listener = sessionListener
            player = this
        }

        awaitClose {
            listener?.let { player?.removeListener(it) }
        }
    }

    override fun getQueue(): Flow<List<MediaItem>> = callbackFlow {
        val controller = mediaControllerFuture(context, MusicService::class.java)

        var listener: Player.Listener? = null
        var player: Player? = null

        controller.withPlayer(mainExecutor) {
            trySend(mediaItems)

            val sessionListener = object : Player.Listener {
                override fun onEvents(player: Player, events: Player.Events) {
                    if (events.containsAny(
                            Player.EVENT_TIMELINE_CHANGED,
                            Player.EVENT_IS_PLAYING_CHANGED,
                            Player.EVENT_MEDIA_METADATA_CHANGED,
                            Player.EVENT_METADATA
                        )
                    ) {
                        trySend(mediaItems)
                    }
                }
            }
            addListener(sessionListener)

            listener = sessionListener
            player = this
        }

        awaitClose {
            listener?.let { player?.removeListener(it) }
        }
    }

    override fun clearQueue() {
        mediaControllerFuture(context, MusicService::class.java).withPlayer(mainExecutor) {
            clearMediaItems()
        }
    }

    override fun playPosition(position: Int) {
        mediaControllerFuture(context, MusicService::class.java).withPlayer(mainExecutor) {
            seekTo(position, 0L)
        }
    }

    override fun moveSong(from: Int, to: Int) {
        mediaControllerFuture(context, MusicService::class.java).withPlayer(mainExecutor) {
            moveMediaItem(from, to)
        }
    }

    override fun removeSong(position: Int) {
        mediaControllerFuture(context, MusicService::class.java).withPlayer(mainExecutor) {
            removeMediaItem(position)
        }
    }
}