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
package ru.stersh.retrosonic.feature.settings.servers.data

import kotlinx.coroutines.flow.Flow
import ru.stersh.apisonic.room.serversettings.ServerSettingsDao
import ru.stersh.retrosonic.core.extensions.mapItems
import ru.stersh.retrosonic.feature.settings.servers.domain.Server
import ru.stersh.retrosonic.feature.settings.servers.domain.ServersRepository

internal class ServersRepositoryImpl(private val serverSettingsDao: ServerSettingsDao) : ServersRepository {
    override fun getServers(): Flow<List<Server>> {
        return serverSettingsDao
            .flowAll()
            .mapItems {
                Server(
                    id = it.id,
                    title = it.title,
                    url = it.url,
                    isActive = it.isActive,
                )
            }
    }

    override suspend fun setActive(id: Long) {
        serverSettingsDao.setActive(id)
    }

    override suspend fun delete(id: Long) {
        serverSettingsDao.delete(id)
    }
}
