package ru.stersh.retrosonic.player.metadata.domain

import kotlinx.coroutines.flow.Flow

interface CurrentSongInfoStorage {
    fun getCurrentSongInfo(): Flow<SongInfo?>
}