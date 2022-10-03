package code.name.monkey.retromusic.feature.library.playlist

import code.name.monkey.retromusic.feature.library.playlist.data.LibraryPlaylistRepositoryImpl
import code.name.monkey.retromusic.feature.library.playlist.domain.LibraryPlaylistRepository
import code.name.monkey.retromusic.feature.library.playlist.presentation.LibraryPlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryPlaylistFeatureModule = module {
    single<LibraryPlaylistRepository> { LibraryPlaylistRepositoryImpl(get()) }
    viewModel { LibraryPlaylistViewModel(get()) }
}