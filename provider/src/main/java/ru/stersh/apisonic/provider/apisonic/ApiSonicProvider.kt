package ru.stersh.apisonic.provider.apisonic

import ru.stersh.apisonic.ApiSonic

interface ApiSonicProvider {
    suspend fun getApiSonic(): ApiSonic
}