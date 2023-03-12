package ru.stersh.retrosonic.player.queue

interface PlayerQueueAudioSourceManager {
    suspend fun playSource(source: AudioSource, shuffled: Boolean = false)
    suspend fun addSource(source: AudioSource, shuffled: Boolean = false)
}