package code.name.monkey.retromusic.feature.details.playlist

import code.name.monkey.retromusic.feature.details.playlist.data.PlaylistDetailsRepositoryImpl
import code.name.monkey.retromusic.feature.details.playlist.domain.PlaylistDetailsRepository
import code.name.monkey.retromusic.feature.details.playlist.presentation.PlaylistDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val detailsPlaylistFeatureModule = module {
    single<PlaylistDetailsRepository> { PlaylistDetailsRepositoryImpl(get()) }
    viewModel { (id: String) ->
        PlaylistDetailsViewModel(id, get(), get())
    }
}