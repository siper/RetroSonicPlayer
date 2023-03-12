package ru.stersh.retrosonic.player.state

import kotlinx.coroutines.flow.Flow

interface PlayStateStore {
    fun isPlaying(): Flow<Boolean>
}