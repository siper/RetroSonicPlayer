/*
 * Copyright (c) 2020 Retro Sonic contributors.
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
package ru.stersh.apisonic.room.serversettings

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerSettingsDao {

    @Query("SELECT * FROM server_settings WHERE is_active = 1")
    fun flowActive(): Flow<ServerSettingsEntity?>

    @Query("SELECT COUNT(*) FROM server_settings WHERE id != :id")
    suspend fun getCountExcept(id: Long): Int

    @Query("SELECT COUNT(*) FROM server_settings")
    suspend fun getCount(): Int

    @Query("SELECT * FROM server_settings WHERE is_active = 1")
    suspend fun getActive(): ServerSettingsEntity?

    @Query("SELECT * FROM server_settings WHERE id = :settingsId")
    suspend fun getSettings(settingsId: Long): ServerSettingsEntity?

    @Query("UPDATE server_settings SET is_active = 1 WHERE id = :serverSettingsId")
    suspend fun activate(serverSettingsId: Long)

    @Query("UPDATE server_settings SET is_active = 0")
    suspend fun deactivateAll()

    @Query("DELETE FROM server_settings WHERE id = :id")
    suspend fun remove(id: Long)

    @Query("SELECT * FROM server_settings")
    fun flowAll(): Flow<List<ServerSettingsEntity>>

    @Query("SELECT * FROM server_settings")
    suspend fun getAll(): List<ServerSettingsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(serverSettings: ServerSettingsEntity): Long

    @Delete
    suspend fun delete(serverSettings: ServerSettingsEntity)

    @Transaction
    suspend fun delete(id: Long) {
        remove(id)
        val all = getAll()
        val active = all.firstOrNull { it.isActive }
        if (active == null) {
            val first = all.firstOrNull() ?: return
            setActive(first)
        }
    }

    @Transaction
    suspend fun setActive(serverSettingsId: Long) {
        deactivateAll()
        activate(serverSettingsId)
    }

    suspend fun setActive(serverSettings: ServerSettingsEntity) {
        setActive(serverSettings.id)
    }
}
