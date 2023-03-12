package code.name.monkey.retromusic.feature.home.data

import code.name.monkey.retromusic.feature.home.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.apisonic.room.serversettings.ServerSettingsDao

internal class UserRepositoryImpl(
    private val serverSettingsDao: ServerSettingsDao,
    private val apiSonicProvider: ApiSonicProvider
) : UserRepository {
    override fun getUsername(): Flow<String> {
        return serverSettingsDao
            .flowActive()
            .mapNotNull { it?.username }
    }

    override fun getAvatarUrl(): Flow<String> {
        return getUsername().flatMapLatest { username ->
            flow {
                apiSonicProvider
                    .getApiSonic()
                    .getAvatarUrl(username)
                    .also { emit(it) }
            }
        }
    }
}