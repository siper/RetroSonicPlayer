package ru.stersh.apisonic.room

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val roomModule = module {

    single {
        Room.databaseBuilder(androidContext(), RetroDatabase::class.java, "retrosonic.db")
            .build()
    }

    single {
        get<RetroDatabase>().playlistDao()
    }

    single {
        get<RetroDatabase>().playCountDao()
    }

    single {
        get<RetroDatabase>().historyDao()
    }

    single { get<RetroDatabase>().serverSettingsDao() }
}