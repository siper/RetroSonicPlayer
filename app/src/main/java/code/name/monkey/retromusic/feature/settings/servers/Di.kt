package code.name.monkey.retromusic.feature.settings.servers

import code.name.monkey.retromusic.feature.settings.servers.data.ServersRepositoryImpl
import code.name.monkey.retromusic.feature.settings.servers.domain.ServersRepository
import code.name.monkey.retromusic.feature.settings.servers.presentation.ServersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val serversFeatureModule = module {
    single<ServersRepository> { ServersRepositoryImpl(get()) }
    viewModel { ServersViewModel(get()) }
}