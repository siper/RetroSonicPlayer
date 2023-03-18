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

import ru.stersh.apisonic.room.serversettings.ServerSettingsEntity
import ru.stersh.retrosonic.feature.settings.server.domain.ServerSettings

internal fun ServerSettingsEntity.toDomain(): ServerSettings {
    return ServerSettings(
        id,
        title,
        url,
        username,
        password,
        isActive,
        useLegacyAuth,
    )
}

internal fun ServerSettings.toData(): ServerSettingsEntity {
    return ServerSettingsEntity(id ?: 0, title, address, username, password, isActive, useLegacyAuth)
}
