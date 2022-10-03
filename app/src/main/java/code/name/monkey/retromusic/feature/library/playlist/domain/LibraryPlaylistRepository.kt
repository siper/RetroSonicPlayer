package code.name.monkey.retromusic.feature.library.playlist.domain

import kotlinx.coroutines.flow.Flow

interface LibraryPlaylistRepository {
    fun getPlaylists(): Flow<List<LibraryPlaylist>>
}