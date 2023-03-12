package code.name.monkey.retromusic.feature.settings.servers.data

import code.name.monkey.retromusic.feature.settings.servers.domain.Server
import code.name.monkey.retromusic.feature.settings.servers.domain.ServersRepository
import kotlinx.coroutines.flow.Flow
import ru.stersh.apisonic.room.serversettings.ServerSettingsDao
import ru.stersh.retrosonic.core.extensions.mapItems

internal class ServersRepositoryImpl(private val serverSettingsDao: ServerSettingsDao) : ServersRepository {
    override fun getServers(): Flow<List<Server>> {
        return serverSettingsDao
            .flowAll()
            .mapItems {
                Server(
                    id = it.id,
                    title = it.title,
                    url = it.url,
                    isActive = it.isActive
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