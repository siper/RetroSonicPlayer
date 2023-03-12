package ru.stersh.retrosonic.player.metadata

data class SongInfo(
    val id: String,
    val title: String?,
    val artist: String?,
    val album: String?,
    val coverArtUrl: String?
)