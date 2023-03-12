package ru.stersh.retrosonic.player.state.data

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import ru.stersh.retrosonic.player.android.MusicService
import ru.stersh.retrosonic.player.state.PlayStateStore
import ru.stersh.retrosonic.player.utils.mediaControllerFuture
import ru.stersh.retrosonic.player.utils.withPlayer

internal class PlayStateStoreImpl(private val context: Context) : PlayStateStore {
    private val mainExecutor = ContextCompat.getMainExecutor(context)

    override fun isPlaying(): Flow<Boolean> = callbackFlow {
        val mediaControllerFuture = mediaControllerFuture(context, MusicService::class.java)

        var player: Player? = null
        var callback: Player.Listener? = null

        mediaControllerFuture.withPlayer(mainExecutor) {
            trySend(isPlaying)

            val sessionCallback = object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    trySend(isPlaying)
                }
            }
            addListener(sessionCallback)

            player = this
            callback = sessionCallback
        }

        awaitClose {
            callback?.let { player?.removeListener(it) }
            callback = null
            player = null
        }
    }
}