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
package ru.stersh.retrosonic.feature.mylibrary

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.stersh.retrosonic.feature.mylibrary.data.DiscoverAlbumsRepositoryImpl
import ru.stersh.retrosonic.feature.mylibrary.data.UserRepositoryImpl
import ru.stersh.retrosonic.feature.mylibrary.domain.DiscoverAlbumsRepository
import ru.stersh.retrosonic.feature.mylibrary.domain.UserRepository
import ru.stersh.retrosonic.feature.mylibrary.presentation.MyLibraryViewModel

val myLibraryFeatureModule = module {
    factory<DiscoverAlbumsRepository> { DiscoverAlbumsRepositoryImpl(get()) }
    factory<UserRepository> { UserRepositoryImpl(get(), get()) }
    viewModel { MyLibraryViewModel(get(), get()) }
}
