package code.name.monkey.retromusic.feature.details.playlist.domain

import kotlinx.coroutines.flow.Flow

interface PlaylistDetailsRepository {
    fun getPlaylistDetails(id: String): Flow<PlaylistDetails>
    fun getPlaylistSongs(id: String): Flow<List<PlaylistSong>>
}