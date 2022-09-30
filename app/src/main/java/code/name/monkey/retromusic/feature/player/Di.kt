package code.name.monkey.retromusic.feature.player

import code.name.monkey.retromusic.feature.player.controls.presentation.PlayerPlaybackControlsViewModel
import code.name.monkey.retromusic.feature.player.cover.data.CoverArtUrlsRepositoryImpl
import code.name.monkey.retromusic.feature.player.cover.domain.CoverArtUrlsRepository
import code.name.monkey.retromusic.feature.player.cover.presentation.PlayerAlbumCoverViewModel
import code.name.monkey.retromusic.feature.player.mini.MiniPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerFeatureModule = module {
    single<CoverArtUrlsRepository> { CoverArtUrlsRepositoryImpl(get()) }
    viewModel { MiniPlayerViewModel(get(), get(), get(), get()) }
    viewModel { PlayerAlbumCoverViewModel(get(), get()) }
    viewModel { PlayerPlaybackControlsViewModel(get(), get(), get(), get()) }
}