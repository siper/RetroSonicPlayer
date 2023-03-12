package code.name.monkey.retromusic.feature.settings.server.presentation

import code.name.monkey.retromusic.feature.settings.server.domain.ServerSettings

internal fun ServerSettingsUi.toDomain(id: Long?): ServerSettings {
    return ServerSettings(
        title = title,
        address = address,
        username = username,
        password = password,
        id = id,
        isActive = isActive
    )
}