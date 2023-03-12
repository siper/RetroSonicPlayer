package ru.stersh.retrosonic.player.metadata

import kotlinx.coroutines.flow.Flow

interface CurrentSongInfoStore {
    fun getCurrentSongInfo(): Flow<SongInfo?>
}