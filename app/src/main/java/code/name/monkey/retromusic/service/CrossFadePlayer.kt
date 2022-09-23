package code.name.monkey.retromusic.service

import android.animation.Animator
import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.PowerManager
import code.name.monkey.appthemehelper.util.VersionUtils.hasMarshmallow
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.extensions.showToast
import code.name.monkey.retromusic.helper.MusicPlayerRemote
import code.name.monkey.retromusic.service.AudioFader.Companion.createFadeAnimator
import code.name.monkey.retromusic.service.playback.Playback.PlaybackCallbacks
import code.name.monkey.retromusic.util.PreferenceUtil
import code.name.monkey.retromusic.util.logE
import kotlinx.coroutines.*
import ru.stersh.apisonic.ApiSonic

/** @author Prathamesh M */

/*
* To make Crossfade work we need two MediaPlayer's
* Basically, we switch back and forth between those two mp's
* e.g. When song is about to end (Reaches Crossfade duration) we let current mediaplayer
* play but with decreasing volume and start the player with the next song with increasing volume
* and vice versa for upcoming song and so on.
*/
class CrossFadePlayer(context: Context, apiSonic: ApiSonic) : LocalPlayback(context, apiSonic) {

    private var currentPlayer: CurrentPlayer = CurrentPlayer.NOT_SET
    private var player1 = MediaPlayer()
    private var player2 = MediaPlayer()
    private var durationListener = DurationListener()
    private var mIsInitialized = false
    private var hasDataSource: Boolean = false /* Whether first player has DataSource */
    private var crossFadeAnimator: Animator? = null
    override var callbacks: PlaybackCallbacks? = null
    private var crossFadeDuration = PreferenceUtil.crossFadeDuration
    var isCrossFading = false

    init {
        player1.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        player2.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        currentPlayer = CurrentPlayer.PLAYER_ONE
    }

    override fun start(): Boolean {
        super.start()
        durationListener.start()
        resumeFade()
        return try {
            getCurrentPlayer()?.start()
            if (isCrossFading) {
                getNextPlayer()?.start()
            }
            true
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            false
        }
    }

    override fun release() {
        stop()
        cancelFade()
        getCurrentPlayer()?.release()
        getNextPlayer()?.release()
        durationListener.cancel()
    }

    override fun stop() {
        super.stop()
        getCurrentPlayer()?.reset()
        mIsInitialized = false
    }

    override fun pause(): Boolean {
        super.pause()
        durationListener.stop()
        pauseFade()
        getCurrentPlayer()?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
        getNextPlayer()?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
        return true
    }

    override fun seek(whereto: Int): Int {
        getNextPlayer()?.stop()
        return try {
            getCurrentPlayer()?.seekTo(whereto)
            whereto
        } catch (e: java.lang.IllegalStateException) {
            e.printStackTrace()
            -1
        }
    }

    override fun setVolume(vol: Float): Boolean {
        cancelFade()
        return try {
            getCurrentPlayer()?.setVolume(vol, vol)
            true
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            false
        }
    }

    override val isInitialized: Boolean
        get() = mIsInitialized

    override val isPlaying: Boolean
        get() = mIsInitialized && getCurrentPlayer()?.isPlaying == true

    override fun setDataSource(
        songId: String,
        force: Boolean,
        completion: (success: Boolean) -> Unit,
    ) {
        if (force) hasDataSource = false
        mIsInitialized = false
        /* We've already set DataSource if initialized is true in setNextDataSource */
        if (!hasDataSource) {
            getCurrentPlayer()?.let {
                setDataSourceImpl(it, getSongUrlFromId(songId)) { success ->
                    mIsInitialized = success
                    completion(success)
                }
            }
            hasDataSource = true
        } else {
            completion(true)
            mIsInitialized = true
        }
    }

    override fun setNextDataSource(path: String?) {}

    override fun setAudioSessionId(sessionId: Int): Boolean {
        return try {
            getCurrentPlayer()?.audioSessionId = sessionId
            true
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            false
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            false
        }
    }

    override val audioSessionId: Int
        get() = getCurrentPlayer()?.audioSessionId!!

    /**
     * Gets the duration of the file.
     *
     * @return The duration in milliseconds
     */
    override fun duration(): Int {
        return if (!mIsInitialized) {
            -1
        } else try {
            getCurrentPlayer()?.duration!!
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            -1
        }
    }

    /**
     * Gets the current position in audio.
     * @return The position in milliseconds
     */
    override fun position(): Int {
        return if (!mIsInitialized) {
            -1
        } else try {
            getCurrentPlayer()?.currentPosition!!
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            -1
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (mp == getCurrentPlayer()) {
            callbacks?.onTrackEnded()
        }
    }

    private fun getCurrentPlayer(): MediaPlayer? {
        return when (currentPlayer) {
            CurrentPlayer.PLAYER_ONE -> {
                player1
            }
            CurrentPlayer.PLAYER_TWO -> {
                player2
            }
            CurrentPlayer.NOT_SET -> {
                null
            }
        }
    }

    private fun getNextPlayer(): MediaPlayer? {
        return when (currentPlayer) {
            CurrentPlayer.PLAYER_ONE -> {
                player2
            }
            CurrentPlayer.PLAYER_TWO -> {
                player1
            }
            CurrentPlayer.NOT_SET -> {
                null
            }
        }
    }

    private fun crossFade(fadeInMp: MediaPlayer, fadeOutMp: MediaPlayer) {
        isCrossFading = true
        crossFadeAnimator = createFadeAnimator(fadeInMp, fadeOutMp) {
            crossFadeAnimator = null
            durationListener.start()
            isCrossFading = false
        }
        crossFadeAnimator?.start()
    }

    private fun cancelFade() {
        crossFadeAnimator?.cancel()
        crossFadeAnimator = null
    }

    private fun pauseFade() {
        crossFadeAnimator?.pause()
    }

    private fun resumeFade() {
        if (crossFadeAnimator?.isPaused == true) {
            crossFadeAnimator?.resume()
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mIsInitialized = false
        mp?.release()
        player1 = MediaPlayer()
        player2 = MediaPlayer()
        mIsInitialized = true
        mp?.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
        context.showToast(R.string.unplayable_file)
        logE(what.toString() + extra)
        return false
    }

    enum class CurrentPlayer {
        PLAYER_ONE,
        PLAYER_TWO,
        NOT_SET
    }

    inner class DurationListener : CoroutineScope by crossFadeScope() {

        private var job: Job? = null

        fun start() {
            job?.cancel()
            job = launch {
                while (true) {
                    delay(250)
                    onDurationUpdated(position(), duration())
                }
            }
        }

        fun stop() {
            job?.cancel()
        }
    }

    fun onDurationUpdated(progress: Int, total: Int) {
        if (total > 0 && (total - progress).div(1000) == crossFadeDuration) {
            getNextPlayer()?.let { player ->
                val nextSong = MusicPlayerRemote.nextSongId
                // Switch to other player (Crossfade) only if next song exists
                if (nextSong != null) {
                    setDataSourceImpl(player, nextSong) { success ->
                        if (success) switchPlayer()
                    }
                }
            }
        }
    }

    private fun switchPlayer() {
        getNextPlayer()?.start()
        crossFade(getNextPlayer()!!, getCurrentPlayer()!!)
        currentPlayer =
            if (currentPlayer == CurrentPlayer.PLAYER_ONE || currentPlayer == CurrentPlayer.NOT_SET) {
                CurrentPlayer.PLAYER_TWO
            } else {
                CurrentPlayer.PLAYER_ONE
            }
        callbacks?.onTrackEndedWithCrossfade()
    }

    override fun setCrossFadeDuration(duration: Int) {
        crossFadeDuration = duration
    }

    override fun setPlaybackSpeedPitch(speed: Float, pitch: Float) {
        getCurrentPlayer()?.setPlaybackSpeedPitch(speed, pitch)
        if (getNextPlayer()?.isPlaying == true) {
            getNextPlayer()?.setPlaybackSpeedPitch(speed, pitch)
        }
    }

    companion object {
        val TAG: String = CrossFadePlayer::class.java.simpleName
    }
}

internal fun crossFadeScope(): CoroutineScope = CoroutineScope(Job() + Dispatchers.Main)

fun MediaPlayer.setPlaybackSpeedPitch(speed: Float, pitch: Float) {
    if (hasMarshmallow()) {
        val wasPlaying = isPlaying
        playbackParams = PlaybackParams().setSpeed(speed).setPitch(pitch)
        if (!wasPlaying) {
            pause()
        }
    }
}