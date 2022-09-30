package ru.stersh.retrosonic.player.progress.domain

import kotlinx.coroutines.flow.Flow

interface PlayerProgressStorage {
    fun playerProgress(): Flow<PlayerProgress?>
}