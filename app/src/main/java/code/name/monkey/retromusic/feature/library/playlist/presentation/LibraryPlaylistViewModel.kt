package code.name.monkey.retromusic.feature.library.playlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import code.name.monkey.retromusic.feature.library.playlist.domain.LibraryPlaylist
import code.name.monkey.retromusic.feature.library.playlist.domain.LibraryPlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.stersh.retrosonic.core.extensions.mapItems

class LibraryPlaylistViewModel(private val libraryPlaylistRepository: LibraryPlaylistRepository) : ViewModel() {
    private val _playlists = MutableStateFlow<List<LibraryPlaylistUi>>(emptyList())
    val playlist: Flow<List<LibraryPlaylistUi>>
        get() = _playlists

    init {
        viewModelScope.launch {
            libraryPlaylistRepository
                .getPlaylists()
                .mapItems { it.toPresentation() }
                .collect { _playlists.value = it }
        }
    }

    private fun LibraryPlaylist.toPresentation(): LibraryPlaylistUi {
        return LibraryPlaylistUi(id, title, coverArtUrl, songCount, duration)
    }
}