package code.name.monkey.retromusic.feature.settings.server.data

import androidx.room.withTransaction
import code.name.monkey.retromusic.feature.settings.server.domain.ServerSettings
import code.name.monkey.retromusic.feature.settings.server.domain.ServerSettingsRepository
import ru.stersh.apisonic.ApiSonic
import ru.stersh.apisonic.room.RetroDatabase
import ru.stersh.apisonic.room.serversettings.ServerSettingsDao

internal class ServerSettingsRepositoryImpl(
    private val serverSettingsDao: ServerSettingsDao,
    private val db: RetroDatabase
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