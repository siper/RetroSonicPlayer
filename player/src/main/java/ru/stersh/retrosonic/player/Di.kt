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
package ru.stersh.retrosonic.player

import org.koin.dsl.module
import ru.stersh.retrosonic.player.android.ApiSonicPlayQueueSyncer
import ru.stersh.retrosonic.player.controls.PlayerControls
import ru.stersh.retrosonic.player.controls.data.PlayerControlsImpl
import ru.stersh.retrosonic.player.metadata.CurrentSongInfoStore
import ru.stersh.retrosonic.player.metadata.impl.CurrentSongInfoStoreImpl
import ru.stersh.retrosonic.player.progress.PlayerProgressStore
import ru.stersh.retrosonic.player.progress.impl.PlayerProgressStoreImpl
import ru.stersh.retrosonic.player.queue.PlayerQueueAudioSourceManager
import ru.stersh.retrosonic.player.queue.PlayerQueueManager
import ru.stersh.retrosonic.player.queue.impl.PlayerQueueAudioSourceManagerImpl
import ru.stersh.retrosonic.player.queue.impl.PlayerQueueManagerImpl
import ru.stersh.retrosonic.player.state.PlayStateStore
import ru.stersh.retrosonic.player.state.data.PlayStateStoreImpl

val playerModule = module {
    single<PlayerQueueAudioSourceManager> { PlayerQueueAudioSourceManagerImpl(get(), get()) }
    single<PlayerControls> { PlayerControlsImpl(get(), get()) }
    single<PlayStateStore> { PlayStateStoreImpl(get()) }
    single<PlayerProgressStore> { PlayerProgressStoreImpl(get(), get()) }
    single<CurrentSongInfoStore> { CurrentSongInfoStoreImpl(get()) }
    single<PlayerQueueManager> { PlayerQueueManagerImpl(get()) }
    single { ApiSonicPlayQueueSyncer(get()) }
}
