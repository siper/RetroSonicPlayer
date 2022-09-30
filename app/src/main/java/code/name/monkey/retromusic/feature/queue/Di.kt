package code.name.monkey.retromusic.feature.queue

import code.name.monkey.retromusic.feature.queue.presentation.PlayingQueueViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val queueFeatureModule = module {
    viewModel { PlayingQueueViewModel(get()) }
}