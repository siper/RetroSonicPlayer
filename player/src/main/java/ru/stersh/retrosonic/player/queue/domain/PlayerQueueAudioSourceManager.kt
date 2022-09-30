package ru.stersh.retrosonic.player.queue.domain

interface PlayerQueueAudioSourceManager {
    suspend fun playSource(source: AudioSource, shuffled: Boolean = false)
    suspend fun addSource(source: AudioSource, shuffled: Boolean = false)
}