package ru.stersh.apisonic.provider.apisonic.impl

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.stersh.apisonic.ApiSonic
import ru.stersh.apisonic.LogLevel
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.apisonic.provider.apisonic.NoActiveServerSettingsFound
import ru.stersh.apisonic.room.serversettings.ServerSettingsDao
import ru.stersh.apisonic.room.serversettings.ServerSettingsEntity

internal class ApiSonicProviderImpl(private val serverSettingsDao: ServerSettingsDao) : ApiSonicProvider {

    private val mutex = Mutex()
    private var apiSonic: ApiSonic? = null

    override suspend fun getApiSonic(): ApiSonic = mutex.withLock {
        val currentServerSettings = serverSettingsDao.getActive() ?: throw NoActiveServerSettingsFound()
        return@withLock if (isSameSettings(currentServerSettings)) {
            requireNotNull(apiSonic)
        } else {
            apiSonic = createNewApi(currentServerSettings)
            requireNotNull(apiSonic)
        }
    }

    private fun createNewApi(serverSettings: ServerSettingsEntity): ApiSonic {
        return ApiSonic(
            url = serverSettings.url,
            username = serverSettings.username,
            password = serverSettings.password,
            apiVersion = API_VERSION,
            clientId = CLIENT_ID,
            useLegacyAuth = serverSettings.useLegacyAuth,
            logLevel = LogLevel.BODY
        )
    }

    private fun isSameSettings(serverSettingsEntity: ServerSettingsEntity): Boolean {
        val currentApiSonic = apiSonic ?: return false
        return currentApiSonic.username == serverSettingsEntity.username
                && currentApiSonic.password == serverSettingsEntity.password
                && currentApiSonic.url == serverSettingsEntity.url
                && currentApiSonic.useLegacyAuth == serverSettingsEntity.useLegacyAuth
    }

    companion object {
        private const val API_VERSION = "1.14.0"
        private const val CLIENT_ID = "RetroSonic"
    }
}