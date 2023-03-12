package code.name.monkey.retromusic.glide.playlistPreview

import ru.stersh.apisonic.room.playlist.PlaylistEntity
import ru.stersh.apisonic.room.playlist.PlaylistWithSongs
import code.name.monkey.retromusic.db.toSongs
import code.name.monkey.retromusic.model.Song

class PlaylistPreview(val playlistWithSongs: ru.stersh.apisonic.room.playlist.PlaylistWithSongs) {

    val playlistEntity: ru.stersh.apisonic.room.playlist.PlaylistEntity get() = playlistWithSongs.playlistEntity
    val songs: List<Song> get() = playlistWithSongs.songs.toSongs()

    override fun equals(other: Any?): Boolean {
        if (other is PlaylistPreview) {
            if (other.playlistEntity.playListId != playlistEntity.playListId) {
                return false
            }
            if (other.songs.size != songs.size) {
                return false
            }
            return true
        }
        return false
    }

    override fun hashCode(): Int {
        var result = playlistEntity.playListId.hashCode()
        result = 31 * result + playlistWithSongs.songs.size
        return result
    }
}