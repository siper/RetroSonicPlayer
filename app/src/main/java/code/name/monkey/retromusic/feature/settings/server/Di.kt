package code.name.monkey.retromusic.feature.settings.server

import code.name.monkey.retromusic.feature.settings.server.data.ServerSettingsRepositoryImpl
import code.name.monkey.retromusic.feature.settings.server.domain.ServerSettingsRepository
import code.name.monkey.retromusic.feature.settings.server.presentation.ServerSettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val serverSettingsFeatureModule = module {
    single<ServerSettingsRepository> { ServerSettingsRepositoryImpl(get(), get()) }
    viewModel { (id: Long?) ->
        ServerSettingsViewModel(id, get())
    }
}