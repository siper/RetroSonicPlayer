package ru.stersh.apisonic.provider

import org.koin.dsl.module
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.apisonic.provider.apisonic.impl.ApiSonicProviderImpl

val providerModule = module {
    single<ApiSonicProvider> { ApiSonicProviderImpl(get()) }
}