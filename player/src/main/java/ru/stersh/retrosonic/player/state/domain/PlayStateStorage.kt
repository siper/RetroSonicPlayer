package ru.stersh.retrosonic.player.state.domain

import kotlinx.coroutines.flow.Flow

interface PlayStateStorage {
    fun isPlaying(): Flow<Boolean>
}