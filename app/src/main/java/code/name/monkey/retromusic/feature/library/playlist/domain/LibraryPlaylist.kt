package code.name.monkey.retromusic.feature.library.playlist.domain

data class LibraryPlaylist(
    val id: String,
    val title: String,
    val coverArtUrl: String,
    val songCount: Int,
    val duration: Long
)