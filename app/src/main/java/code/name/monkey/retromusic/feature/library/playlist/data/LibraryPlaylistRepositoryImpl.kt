package code.name.monkey.retromusic.feature.library.playlist.data

import code.name.monkey.retromusic.feature.library.playlist.domain.LibraryPlaylist
import code.name.monkey.retromusic.feature.library.playlist.domain.LibraryPlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stersh.apisonic.ApiSonic
import ru.stersh.apisonic.models.Playlists

class LibraryPlaylistRepositoryImpl(private val apiSonic: ApiSonic) : LibraryPlaylistRepository {

    override fun getPlaylists(): Flow<List<LibraryPlaylist>> = flow {
        apiSonic
            .getPlaylists()
            .map { it.toDomain() }
            .also { emit(it) }
    }

    private fun Playlists.Playlist.toDomain(): LibraryPlaylist {
        return LibraryPlaylist(
            id = id,
            title = name,
            coverArtUrl = apiSonic.getCoverArtUrl(coverArt),
            songCount = songCount,
            duration = duration * 1000L
        )
    }
}