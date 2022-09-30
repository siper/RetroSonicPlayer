package ru.stersh.retrosonic.player

import org.koin.dsl.module
import ru.stersh.retrosonic.player.controls.data.PlayerControlsImpl
import ru.stersh.retrosonic.player.controls.domain.PlayerControls
import ru.stersh.retrosonic.player.metadata.data.CurrentSongInfoStorageImpl
import ru.stersh.retrosonic.player.metadata.domain.CurrentSongInfoStorage
import ru.stersh.retrosonic.player.progress.data.PlayerProgressStorageImpl
import ru.stersh.retrosonic.player.progress.domain.PlayerProgressStorage
import ru.stersh.retrosonic.player.queue.data.PlayerQueueAudioSourceManagerImpl
import ru.stersh.retrosonic.player.queue.data.PlayerQueueManagerImpl
import ru.stersh.retrosonic.player.queue.domain.PlayerQueueAudioSourceManager
import ru.stersh.retrosonic.player.queue.domain.PlayerQueueManager
import ru.stersh.retrosonic.player.state.data.PlayStateStorageImpl
import ru.stersh.retrosonic.player.state.domain.PlayStateStorage

val playerModule = module {
    single<PlayerQueueAudioSourceManager> { PlayerQueueAudioSourceManagerImpl(get(), get()) }
    single<PlayerControls> { PlayerControlsImpl(get()) }
    single<PlayStateStorage> { PlayStateStorageImpl(get()) }
    single<PlayerProgressStorage> { PlayerProgressStorageImpl(get()) }
    single<CurrentSongInfoStorage> { CurrentSongInfoStorageImpl(get()) }
    single<PlayerQueueManager> { PlayerQueueManagerImpl(get()) }
}