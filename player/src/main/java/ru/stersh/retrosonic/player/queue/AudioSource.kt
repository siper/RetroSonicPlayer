package ru.stersh.retrosonic.player.queue

sealed class AudioSource(open val id: String) {
    data class Song(override val id: String) : AudioSource(id)
    data class Album(override val id: String) : AudioSource(id)
    data class Artist(override val id: String) : AudioSource(id)
    data class Playlist(override val id: String) : AudioSource(id)
}
