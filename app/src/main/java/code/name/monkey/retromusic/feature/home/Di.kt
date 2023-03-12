package code.name.monkey.retromusic.feature.home

import code.name.monkey.retromusic.feature.home.data.UserRepositoryImpl
import code.name.monkey.retromusic.feature.home.domain.UserRepository
import code.name.monkey.retromusic.feature.home.presentation.MyLibraryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val myLibraryFeatureModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    viewModel { MyLibraryViewModel(get()) }
}