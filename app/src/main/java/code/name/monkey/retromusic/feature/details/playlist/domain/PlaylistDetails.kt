package code.name.monkey.retromusic.feature.details.playlist.domain

data class PlaylistDetails(
    val title: String,
    val coverArtUrl: String,
    val songCount: Int,
    val duration: Long
)
