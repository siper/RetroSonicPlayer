package code.name.monkey.retromusic.feature.library.playlist.data

import code.name.monkey.retromusic.feature.library.playlist.domain.LibraryPlaylist
import code.name.monkey.retromusic.feature.library.playlist.domain.LibraryPlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stersh.apisonic.models.Playlists
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider

class LibraryPlaylistRepositoryImpl(private val apiSonicProvider: ApiSonicProvider) : LibraryPlaylistRepository {

    override fun getPlaylists(): Flow<List<LibraryPlaylist>> = flow {
        apiSonicProvider
            .getApiSonic()
            .getPlaylists()
            .map { it.toDomain() }
            .also { emit(it) }
    }

    private suspend fun Playlists.Playlist.toDomain(): LibraryPlaylist {
        return LibraryPlaylist(
            id = id,
            title = name,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
            songCount = songCount,
            duration = duration * 1000L
        )
    }
}