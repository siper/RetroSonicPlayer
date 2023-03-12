package code.name.monkey.retromusic.feature.settings.server.data

import code.name.monkey.retromusic.feature.settings.server.domain.ServerSettings
import ru.stersh.apisonic.room.serversettings.ServerSettingsEntity

internal fun ServerSettingsEntity.toDomain(): ServerSettings {
    return ServerSettings(
        id, title, url, username, password, isActive
    )
}

internal fun ServerSettings.toData(): ServerSettingsEntity {
    return ServerSettingsEntity(id ?: 0, title, address, username, password, isActive)
}