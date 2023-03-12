package ru.stersh.retrosonic.player

import org.koin.dsl.module
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
    single<PlayerControls> { PlayerControlsImpl(get()) }
    single<PlayStateStore> { PlayStateStoreImpl(get()) }
    single<PlayerProgressStore> { PlayerProgressStoreImpl(get()) }
    single<CurrentSongInfoStore> { CurrentSongInfoStoreImpl(get()) }
    single<PlayerQueueManager> { PlayerQueueManagerImpl(get()) }
}