/*
 * Copyright (c) 2020 Retro Sonic contributors.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package ru.stersh.retrosonic.feature.player

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.stersh.retrosonic.feature.player.controls.presentation.PlayerPlaybackControlsViewModel
import ru.stersh.retrosonic.feature.player.cover.data.CoverArtUrlsRepositoryImpl
import ru.stersh.retrosonic.feature.player.cover.domain.CoverArtUrlsRepository
import ru.stersh.retrosonic.feature.player.cover.presentation.PlayerAlbumCoverViewModel
import ru.stersh.retrosonic.feature.player.main.presentation.PlayerMainViewModel
import ru.stersh.retrosonic.feature.player.mini.MiniPlayerViewModel

val playerFeatureModule = module {
    single<CoverArtUrlsRepository> { CoverArtUrlsRepositoryImpl(get()) }
    viewModel { PlayerMainViewModel(get(), get()) }
    viewModel { MiniPlayerViewModel(get(), get(), get(), get()) }
    viewModel { PlayerAlbumCoverViewModel(get(), get()) }
    viewModel { PlayerPlaybackControlsViewModel(get(), get(), get(), get()) }
}
