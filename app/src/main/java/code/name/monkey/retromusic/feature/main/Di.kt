package code.name.monkey.retromusic.feature.main

import code.name.monkey.retromusic.feature.main.presentation.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainFeatureModule = module {
    viewModel { MainViewModel(get()) }
}