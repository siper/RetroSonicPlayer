package code.name.monkey.retromusic.interfaces

import android.view.View
import ru.stersh.apisonic.room.playlist.PlaylistWithSongs

interface IPlaylistClickListener {
    fun onPlaylistClick(playlistWithSongs: ru.stersh.apisonic.room.playlist.PlaylistWithSongs, view: View)
}