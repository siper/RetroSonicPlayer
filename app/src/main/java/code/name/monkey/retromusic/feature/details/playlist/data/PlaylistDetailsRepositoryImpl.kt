package code.name.monkey.retromusic.feature.details.playlist.data

import code.name.monkey.retromusic.feature.details.playlist.domain.PlaylistDetails
import code.name.monkey.retromusic.feature.details.playlist.domain.PlaylistDetailsRepository
import code.name.monkey.retromusic.feature.details.playlist.domain.PlaylistSong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stersh.apisonic.models.PlaylistResponse
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider

class PlaylistDetailsRepositoryImpl(private val apiSonicProvider: ApiSonicProvider) : PlaylistDetailsRepository {
    override fun getPlaylistDetails(id: String): Flow<PlaylistDetails> {
        return flow {
            apiSonicProvider
                .getApiSonic()
                .getPlaylist(id)
                .toDomain()
                .also { emit(it) }
        }
    }

    override fun getPlaylistSongs(id: String): Flow<List<PlaylistSong>> {
        return flow {
            apiSonicProvider
                .getApiSonic()
                .getPlaylist(id)
                .entry
                .map { it.toDomain() }
                .also { emit(it) }
        }
    }

    private suspend fun PlaylistResponse.Entry.toDomain(): PlaylistSong {
        return PlaylistSong(
            id = id,
            albumId = albumId,
            album = album,
            artist = artist,
            artistId = artistId,
            title = title,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
            duration = duration * 1000L,
            year = year,
            trackNumber = track
        )
    }

    private suspend fun PlaylistResponse.Playlist.toDomain(): PlaylistDetails {
        return PlaylistDetails(
            title = name,
            songCount = songCount,
            duration = duration * 1000L,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt)
        )
    }
}