package ru.stersh.retrosonic.player.progress

import kotlinx.coroutines.flow.Flow

interface PlayerProgressStore {
    fun playerProgress(): Flow<PlayerProgress?>
}