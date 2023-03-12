package code.name.monkey.retromusic

import code.name.monkey.retromusic.cast.RetroWebServer
import code.name.monkey.retromusic.feature.details.album.detailsAlbumFeatureModule
import code.name.monkey.retromusic.feature.details.artist.detailsArtistFeatureModule
import code.name.monkey.retromusic.feature.details.playlist.detailsPlaylistFeatureModule
import code.name.monkey.retromusic.feature.home.myLibraryFeatureModule
import code.name.monkey.retromusic.feature.library.album.libraryAlbumFeatureModule
import code.name.monkey.retromusic.feature.library.artist.libraryArtistFeatureModule
import code.name.monkey.retromusic.feature.library.playlist.libraryPlaylistFeatureModule
import code.name.monkey.retromusic.feature.main.mainFeatureModule
import code.name.monkey.retromusic.feature.player.playerFeatureModule
import code.name.monkey.retromusic.feature.queue.queueFeatureModule
import code.name.monkey.retromusic.feature.settings.server.serverSettingsFeatureModule
import code.name.monkey.retromusic.feature.settings.servers.serversFeatureModule
import code.name.monkey.retromusic.fragments.LibraryViewModel
import code.name.monkey.retromusic.fragments.genres.GenreDetailsViewModel
import code.name.monkey.retromusic.model.Genre
import code.name.monkey.retromusic.network.provideDefaultCache
import code.name.monkey.retromusic.network.provideLastFmRest
import code.name.monkey.retromusic.network.provideLastFmRetrofit
import code.name.monkey.retromusic.network.provideOkHttp
import code.name.monkey.retromusic.repository.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.stersh.apisonic.provider.providerModule
import ru.stersh.apisonic.room.roomModule
import ru.stersh.retrosonic.player.playerModule

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
    single {
        RetroWebServer(get())
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
            get()
        )
    } bind LastAddedRepository::class

    single {
        RealSearchRepository(
            get(),
            get(),
            get(),
            get(),
            get()
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
            genre
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
    providerModule
)