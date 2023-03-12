/*
 * Copyright (c) 2020 Hemanth Savarla.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package ru.stersh.apisonic.room

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.stersh.apisonic.room.history.HistoryDao
import ru.stersh.apisonic.room.history.HistoryEntity
import ru.stersh.apisonic.room.playcount.PlayCountDao
import ru.stersh.apisonic.room.playcount.PlayCountEntity
import ru.stersh.apisonic.room.playlist.PlaylistDao
import ru.stersh.apisonic.room.playlist.PlaylistEntity
import ru.stersh.apisonic.room.playlist.SongEntity
import ru.stersh.apisonic.room.serversettings.ServerSettingsDao
import ru.stersh.apisonic.room.serversettings.ServerSettingsEntity

@Database(
    entities = [PlaylistEntity::class, SongEntity::class, HistoryEntity::class, PlayCountEntity::class, ServerSettingsEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RetroDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun playCountDao(): PlayCountDao
    abstract fun historyDao(): HistoryDao
    abstract fun serverSettingsDao(): ServerSettingsDao
}
