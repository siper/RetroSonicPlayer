package ru.stersh.retrosonic.player.queue.data

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import ru.stersh.apisonic.ApiSonic
import ru.stersh.apisonic.models.Song
import ru.stersh.retrosonic.player.android.MusicService
import ru.stersh.retrosonic.player.queue.domain.AudioSource
import ru.stersh.retrosonic.player.queue.domain.PlayerQueueAudioSourceManager
import ru.stersh.retrosonic.player.utils.MEDIA_ITEM_ALBUM_ID
import ru.stersh.retrosonic.player.utils.MEDIA_ITEM_DURATION
import ru.stersh.retrosonic.player.utils.mediaControllerFuture
import ru.stersh.retrosonic.player.utils.withPlayer

internal class PlayerQueueAudioSourceManagerImpl(
    private val context: Context,
    private val apiSonic: ApiSonic
) : PlayerQueueAudioSourceManager {
    private val mediaController = mediaControllerFuture(context, MusicService::class.java)
    private val mainExecutor = ContextCompat.getMainExecutor(context)

    override suspend fun playSource(source: AudioSource, shuffled: Boolean) {
        val newQueue = when (source) {
            is AudioSource.Song -> listOf(getSong(source))
            is AudioSource.Album -> getSongs(source)
            is AudioSource.Artist -> getSongs(source)
        }
        mediaController.withPlayer(mainExecutor) {
            if (shuffled) {
                setMediaItems(newQueue.shuffled())
            } else {
                setMediaItems(newQueue)
            }
            prepare()
            play()
        }
    }

    override suspend fun addSource(source: AudioSource, shuffled: Boolean) {
        val newSongs = when (source) {
            is AudioSource.Song -> listOf(getSong(source))
            is AudioSource.Album -> getSongs(source)
            is AudioSource.Artist -> getSongs(source)
        }
        mediaController.withPlayer(mainExecutor) {
            if (shuffled) {
                addMediaItems(newSongs.shuffled())
            } else {
                addMediaItems(newSongs)
            }
            prepare()
            play()
        }
    }

    private suspend fun getSong(source: AudioSource.Song): MediaItem {
        return apiSonic
            .getSong(source.id)
            .toMediaItem()
    }

    private suspend fun getSongs(source: AudioSource.Album): List<MediaItem> {
        val songs = apiSonic
            .getAlbum(source.id)
            .song
            ?.takeIf { it.isNotEmpty() }
            ?: return emptyList()

        return songs.map { song ->
            song.toMediaItem()
        }
    }

    private suspend fun getSongs(source: AudioSource.Artist): List<MediaItem> = coroutineScope {
        val albums = apiSonic
            .getArtist(source.id)
            .albums
            ?.takeIf { it.isNotEmpty() }
            ?: return@coroutineScope emptyList()

        val songs = albums.flatMap { getSongs(AudioSource.Album(it.id)) }

        return@coroutineScope albums
            .map {
                async { getSongs(AudioSource.Album(it.id)) }
            }
            .awaitAll()
            .flatten()
    }

    private fun Song.toMediaItem(): MediaItem {
        val songUri = apiSonic.downloadUrl(id).toUri()
        val metadata = MediaMetadata
            .Builder()
            .setTitle(title)
            .setArtist(artist)
            .setExtras(
                bundleOf(
                    MEDIA_ITEM_ALBUM_ID to albumId,
                    MEDIA_ITEM_DURATION to duration * 1000
                )
            )
            .setArtworkUri(apiSonic.getCoverArtUrl(coverArt).toUri())
            .build()
        val requestMetadata = MediaItem
            .RequestMetadata
            .Builder()
            .setMediaUri(songUri)
            .build()
        return MediaItem
            .Builder()
            .setMediaId(id)
            .setMediaMetadata(metadata)
            .setRequestMetadata(requestMetadata)
            .setUri(songUri)
            .build()
    }
}