package code.name.monkey.retromusic.feature.settings.server.domain

internal interface ServerSettingsRepository {
    suspend fun getSettings(settingsId: Long): ServerSettings?
    suspend fun saveSettings(settings: ServerSettings)
    suspend fun isFirstServer(id: Long?): Boolean
}