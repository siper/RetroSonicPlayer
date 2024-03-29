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
package ru.stersh.retrosonic.feature.details.playlist

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.stersh.retrosonic.feature.details.playlist.data.PlaylistDetailsRepositoryImpl
import ru.stersh.retrosonic.feature.details.playlist.domain.PlaylistDetailsRepository
import ru.stersh.retrosonic.feature.details.playlist.presentation.PlaylistDetailsViewModel

val detailsPlaylistFeatureModule = module {
    single<PlaylistDetailsRepository> { PlaylistDetailsRepositoryImpl(get()) }
    viewModel { (id: String) ->
        PlaylistDetailsViewModel(id, get(), get())
    }
}
