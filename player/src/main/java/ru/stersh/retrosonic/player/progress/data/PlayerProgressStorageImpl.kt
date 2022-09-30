package ru.stersh.retrosonic.player.progress.data

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.C
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.player.android.MusicService
import ru.stersh.retrosonic.player.progress.domain.PlayerProgress
import ru.stersh.retrosonic.player.progress.domain.PlayerProgressStorage
import ru.stersh.retrosonic.player.utils.mediaControllerFuture
import ru.stersh.retrosonic.player.utils.withPlayer
import java.text.SimpleDateFormat
import java.util.*

internal class PlayerProgressStorageImpl(private val context: Context) : PlayerProgressStorage {
    private val executor = ContextCompat.getMainExecutor(context)
    private val minuteTimeFormat = SimpleDateFormat("mm:ss")
    private val hourTimeFormat = SimpleDateFormat("hh:mm:ss")

    override fun playerProgress(): Flow<PlayerProgress?> = callbackFlow {
        val mediaControllerFuture = mediaControllerFuture(context, MusicService::class.java)

        var player: Player? = null
        var callback: Player.Listener? = null

        mediaControllerFuture.withPlayer(executor) {
            trySend(progress)

            launch {
                while (true) {
                    delay(SEND_PROGRESS_DELAY)
                    trySend(progress)
                }
            }

            val sessionCallback = object : Player.Listener {
                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    trySend(progress)
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
    }.distinctUntilChanged()

    private val Player.progress: PlayerProgress?
        get() {
            val totalTimeMs = duration.takeIf { it != C.TIME_UNSET } ?: return null
            val currentTimeMs = currentPosition
            return PlayerProgress(
                totalTimeMs = totalTimeMs,
                currentTimeMs = currentTimeMs,
                totalTime = formatTime(totalTimeMs),
                currentTime = formatTime(currentTimeMs)
            )
        }

    private fun formatTime(time: Long): String {
        return if (time >= ONE_HOUR_MS) {
            hourTimeFormat.format(Date(time))
        } else {
            minuteTimeFormat.format(Date(time))
        }
    }

    companion object {
        private const val ONE_HOUR_MS = 3600000L
        private const val SEND_PROGRESS_DELAY = 500L
    }
}