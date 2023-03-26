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
package ru.stersh.retrosonic

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.stersh.apisonic.provider.providerModule
import ru.stersh.apisonic.room.roomModule
import ru.stersh.retrosonic.feature.details.album.detailsAlbumFeatureModule
import ru.stersh.retrosonic.feature.details.artist.detailsArtistFeatureModule
import ru.stersh.retrosonic.feature.details.playlist.detailsPlaylistFeatureModule
import ru.stersh.retrosonic.feature.library.album.libraryAlbumFeatureModule
import ru.stersh.retrosonic.feature.library.artist.libraryArtistFeatureModule
import ru.stersh.retrosonic.feature.library.playlist.libraryPlaylistFeatureModule
import ru.stersh.retrosonic.feature.main.mainFeatureModule
import ru.stersh.retrosonic.feature.mylibrary.myLibraryFeatureModule
import ru.stersh.retrosonic.feature.player.playerFeatureModule
import ru.stersh.retrosonic.feature.queue.queueFeatureModule
import ru.stersh.retrosonic.feature.settings.server.serverSettingsFeatureModule
import ru.stersh.retrosonic.feature.settings.servers.serversFeatureModule
import ru.stersh.retrosonic.fragments.LibraryViewModel
import ru.stersh.retrosonic.fragments.genres.GenreDetailsViewModel
import ru.stersh.retrosonic.model.Genre
import ru.stersh.retrosonic.network.provideDefaultCache
import ru.stersh.retrosonic.network.provideLastFmRest
import ru.stersh.retrosonic.network.provideLastFmRetrofit
import ru.stersh.retrosonic.network.provideOkHttp
import ru.stersh.retrosonic.player.playerModule
import ru.stersh.retrosonic.repository.AlbumRepository
import ru.stersh.retrosonic.repository.ArtistRepository
import ru.stersh.retrosonic.repository.GenreRepository
import ru.stersh.retrosonic.repository.LastAddedRepository
import ru.stersh.retrosonic.repository.LocalDataRepository
import ru.stersh.retrosonic.repository.PlaylistRepository
import ru.stersh.retrosonic.repository.RealAlbumRepository
import ru.stersh.retrosonic.repository.RealArtistRepository
import ru.stersh.retrosonic.repository.RealGenreRepository
import ru.stersh.retrosonic.repository.RealLastAddedRepository
import ru.stersh.retrosonic.repository.RealLocalDataRepository
import ru.stersh.retrosonic.repository.RealPlaylistRepository
import ru.stersh.retrosonic.repository.RealRepository
import ru.stersh.retrosonic.repository.RealRoomRepository
import ru.stersh.retrosonic.repository.RealSearchRepository
import ru.stersh.retrosonic.repository.RealSongRepository
import ru.stersh.retrosonic.repository.RealTopPlayedRepository
import ru.stersh.retrosonic.repository.Repository
import ru.stersh.retrosonic.repository.RoomRepository
import ru.stersh.retrosonic.repository.SongRepository
import ru.stersh.retrosonic.repository.TopPlayedRepository

val networkModule = module {

    factory {
        provideDefaultCache()
    }
    factory {
        provideOkHttp(get(), get())
    }
    single {
        provideLastFmRetrofit(get())
    }
    single {
        provideLastFmRest(get())
    }
}

private val mainModule = module {
    single {
        androidContext().contentResolver
    }
}
private val dataModule = module {
    single {
        RealRoomRepository(get(), get(), get())
    } bind RoomRepository::class

    single {
        RealRepository(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    } bind Repository::class

    single {
        RealSongRepository(get())
    } bind SongRepository::class

    single {
        RealGenreRepository(get(), get())
    } bind GenreRepository::class

    single {
        RealAlbumRepository(get())
    } bind AlbumRepository::class

    single {
        RealArtistRepository(get(), get())
    } bind ArtistRepository::class

    single {
        RealPlaylistRepository(get())
    } bind PlaylistRepository::class

    single {
        RealTopPlayedRepository(get(), get(), get(), get())
    } bind TopPlayedRepository::class

    single {
        RealLastAddedRepository(
            get(),
            get(),
            get(),
        )
    } bind LastAddedRepository::class

    single {
        RealSearchRepository(
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    single {
        RealLocalDataRepository(get())
    } bind LocalDataRepository::class
}

private val viewModules = module {

    viewModel {
        LibraryViewModel(get())
    }

    viewModel { (genre: Genre) ->
        GenreDetailsViewModel(
            get(),
            genre,
        )
    }
}

val appModules = listOf(
    mainModule,
    dataModule,
    viewModules,
    networkModule,
    roomModule,
    playerModule,
    libraryAlbumFeatureModule,
    detailsAlbumFeatureModule,
    libraryArtistFeatureModule,
    detailsArtistFeatureModule,
    playerFeatureModule,
    mainFeatureModule,
    queueFeatureModule,
    myLibraryFeatureModule,
    libraryPlaylistFeatureModule,
    detailsPlaylistFeatureModule,
    serverSettingsFeatureModule,
    serversFeatureModule,
    providerModule,
)
