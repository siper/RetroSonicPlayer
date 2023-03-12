package code.name.monkey.retromusic.feature.settings.server.data

import androidx.room.withTransaction
import code.name.monkey.retromusic.feature.settings.server.domain.ServerSettings
import code.name.monkey.retromusic.feature.settings.server.domain.ServerSettingsRepository
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
}