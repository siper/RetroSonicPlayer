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
package ru.stersh.retrosonic.feature.settings.server.data

import androidx.room.withTransaction
import ru.stersh.apisonic.ApiSonic
import ru.stersh.apisonic.room.RetroDatabase
import ru.stersh.apisonic.room.serversettings.ServerSettingsDao
import ru.stersh.retrosonic.feature.settings.server.domain.ServerSettings
import ru.stersh.retrosonic.feature.settings.server.domain.ServerSettingsRepository

internal class ServerSettingsRepositoryImpl(
    private val serverSettingsDao: ServerSettingsDao,
    private val db: RetroDatabase,
) : ServerSettingsRepository {
    override suspend fun getSettings(settingsId: Long): ServerSettings? {
        return serverSettingsDao.getSettings(settingsId)?.toDomain()
    }

    override suspend fun saveSettings(settings: ServerSettings) {
        val entity = settings.toData()
        db.withTransaction {
            if (entity.isActive) {
                serverSettingsDao.deactivateAll()
            }
            serverSettingsDao.insert(entity)
        }
    }

    override suspend fun isFirstServer(id: Long?): Boolean {
        return if (id == null) {
            serverSettingsDao.getCount() == 0
        } else {
            serverSettingsDao.getCountExcept(id) < 1
        }
    }

    override suspend fun testServerSettings(settings: ServerSettings) {
        ApiSonic(
            url = settings.address,
            username = settings.username,
            password = settings.password,
            useLegacyAuth = settings.useLegacyAuth,
            apiVersion = API_VERSION,
            clientId = CLIENT_ID,
        ).ping()
    }

    companion object {
        private const val API_VERSION = "1.14.0"
        private const val CLIENT_ID = "RetroSonic"
    }
}
