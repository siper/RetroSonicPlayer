package code.name.monkey.retromusic.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code.name.monkey.retromusic.feature.home.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

internal class MyLibraryViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _username = MutableStateFlow<String?>(null)
    val username: Flow<String>
        get() = _username.filterNotNull()

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: Flow<String>
        get() = _avatarUrl.filterNotNull()

    init {
        viewModelScope.launch {
            userRepository
                .getUsername()
                .collect {
                    _username.value = it
                }
        }
        viewModelScope.launch {
            userRepository
                .getAvatarUrl()
                .collect {
                    _avatarUrl.value = it
                }
        }
    }
}