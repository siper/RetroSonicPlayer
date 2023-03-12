package ru.stersh.retrosonic.player.controls.data

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import ru.stersh.retrosonic.player.android.MusicService
import ru.stersh.retrosonic.player.controls.PlayerControls
import ru.stersh.retrosonic.player.utils.mediaControllerFuture

internal class PlayerControlsImpl(private val context: Context) : PlayerControls {
    private val mediaControllerFuture = mediaControllerFuture(context, MusicService::class.java)
    private val mainExecutor = ContextCompat.getMainExecutor(context)

    override fun play() = withPlayer {
        play()
    }

    override fun pause() = withPlayer {
        pause()
    }

    override fun next() = withPlayer {
        seekToNextMediaItem()
    }

    override fun previous() = withPlayer {
        if (duration >= 3000) {
            seekTo(0)
        } else {
            seekToPreviousMediaItem()
        }
    }

    override fun seek(time: Long) = withPlayer {
        seekTo(time)
    }

    private inline fun withPlayer(crossinline block: Player.() -> Unit) {
        mediaControllerFuture.addListener(
            {
                val player = mediaControllerFuture.get()
                block.invoke(player)
            },
            mainExecutor
        )
    }
}