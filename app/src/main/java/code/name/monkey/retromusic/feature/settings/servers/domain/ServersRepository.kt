package code.name.monkey.retromusic.feature.settings.servers.domain

import kotlinx.coroutines.flow.Flow

internal interface ServersRepository {

    fun getServers(): Flow<List<Server>>

    suspend fun setActive(id: Long)

    suspend fun delete(id: Long)
}