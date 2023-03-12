package code.name.monkey.retromusic.repository

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.db.toHistoryEntity
import code.name.monkey.retromusic.helper.SortOrder.PlaylistSortOrder.Companion.PLAYLIST_A_Z
import code.name.monkey.retromusic.helper.SortOrder.PlaylistSortOrder.Companion.PLAYLIST_SONG_COUNT
import code.name.monkey.retromusic.helper.SortOrder.PlaylistSortOrder.Companion.PLAYLIST_SONG_COUNT_DESC
import code.name.monkey.retromusic.helper.SortOrder.PlaylistSortOrder.Companion.PLAYLIST_Z_A
import code.name.monkey.retromusic.model.Song
import code.name.monkey.retromusic.util.PreferenceUtil


interface RoomRepository {
    fun historySongs(): List<ru.stersh.apisonic.room.history.HistoryEntity>
    fun favoritePlaylistLiveData(favorite: String): LiveData<List<ru.stersh.apisonic.room.playlist.SongEntity>>
    fun observableHistorySongs(): LiveData<List<ru.stersh.apisonic.room.history.HistoryEntity>>
    fun getSongs(playListId: Long): LiveData<List<ru.stersh.apisonic.room.playlist.SongEntity>>
    suspend fun createPlaylist(playlistEntity: ru.stersh.apisonic.room.playlist.PlaylistEntity): Long
    suspend fun checkPlaylistExists(playlistName: String): List<ru.stersh.apisonic.room.playlist.PlaylistEntity>
    suspend fun playlists(): List<ru.stersh.apisonic.room.playlist.PlaylistEntity>
    suspend fun playlistWithSongs(): List<ru.stersh.apisonic.room.playlist.PlaylistWithSongs>
    suspend fun insertSongs(songs: List<ru.stersh.apisonic.room.playlist.SongEntity>)
    suspend fun deletePlaylistEntities(playlistEntities: List<ru.stersh.apisonic.room.playlist.PlaylistEntity>)
    suspend fun renamePlaylistEntity(playlistId: Long, name: String)
    suspend fun deleteSongsInPlaylist(songs: List<ru.stersh.apisonic.room.playlist.SongEntity>)
    suspend fun deletePlaylistSongs(playlists: List<ru.stersh.apisonic.room.playlist.PlaylistEntity>)
    suspend fun favoritePlaylist(favorite: String): ru.stersh.apisonic.room.playlist.PlaylistEntity
    suspend fun isFavoriteSong(songEntity: ru.stersh.apisonic.room.playlist.SongEntity): List<ru.stersh.apisonic.room.playlist.SongEntity>
    suspend fun removeSongFromPlaylist(songEntity: ru.stersh.apisonic.room.playlist.SongEntity)
    suspend fun addSongToHistory(currentSong: Song)
    suspend fun songPresentInHistory(song: Song): ru.stersh.apisonic.room.history.HistoryEntity?
    suspend fun updateHistorySong(song: Song)
    suspend fun favoritePlaylistSongs(favorite: String): List<ru.stersh.apisonic.room.playlist.SongEntity>
    suspend fun insertSongInPlayCount(playCountEntity: ru.stersh.apisonic.room.playcount.PlayCountEntity)
    suspend fun updateSongInPlayCount(playCountEntity: ru.stersh.apisonic.room.playcount.PlayCountEntity)
    suspend fun deleteSongInPlayCount(playCountEntity: ru.stersh.apisonic.room.playcount.PlayCountEntity)
    suspend fun deleteSongInHistory(songId: Long)
    suspend fun clearSongHistory()
    suspend fun checkSongExistInPlayCount(songId: Long): List<ru.stersh.apisonic.room.playcount.PlayCountEntity>
    suspend fun playCountSongs(): List<ru.stersh.apisonic.room.playcount.PlayCountEntity>
    suspend fun deleteSongs(songs: List<Song>)
    suspend fun isSongFavorite(context: Context, songId: Long): Boolean
    fun checkPlaylistExists(playListId: Long): LiveData<Boolean>
}

class RealRoomRepository(
    private val playlistDao: ru.stersh.apisonic.room.playlist.PlaylistDao,
    private val playCountDao: ru.stersh.apisonic.room.playcount.PlayCountDao,
    private val historyDao: ru.stersh.apisonic.room.history.HistoryDao
) : RoomRepository {
    @WorkerThread
    override suspend fun createPlaylist(playlistEntity: ru.stersh.apisonic.room.playlist.PlaylistEntity): Long =
        playlistDao.createPlaylist(playlistEntity)

    @WorkerThread
    override suspend fun checkPlaylistExists(playlistName: String): List<ru.stersh.apisonic.room.playlist.PlaylistEntity> =
        playlistDao.playlist(playlistName)

    @WorkerThread
    override suspend fun playlists(): List<ru.stersh.apisonic.room.playlist.PlaylistEntity> = playlistDao.playlists()

    @WorkerThread
    override suspend fun playlistWithSongs(): List<ru.stersh.apisonic.room.playlist.PlaylistWithSongs> =
        when (PreferenceUtil.playlistSortOrder) {
            PLAYLIST_A_Z ->
                playlistDao.playlistsWithSongs().sortedBy {
                    it.playlistEntity.playlistName
                }
            PLAYLIST_Z_A -> playlistDao.playlistsWithSongs()
                .sortedByDescending {
                    it.playlistEntity.playlistName
                }
            PLAYLIST_SONG_COUNT -> playlistDao.playlistsWithSongs().sortedBy { it.songs.size }
            PLAYLIST_SONG_COUNT_DESC -> playlistDao.playlistsWithSongs()
                .sortedByDescending { it.songs.size }
            else -> playlistDao.playlistsWithSongs().sortedBy {
                it.playlistEntity.playlistName
            }
        }

    @WorkerThread
    override suspend fun insertSongs(songs: List<ru.stersh.apisonic.room.playlist.SongEntity>) {

        playlistDao.insertSongsToPlaylist(songs)
    }

    override fun getSongs(playListId: Long): LiveData<List<ru.stersh.apisonic.room.playlist.SongEntity>> =
        playlistDao.songsFromPlaylist(playListId)

    override fun checkPlaylistExists(playListId: Long): LiveData<Boolean> =
        playlistDao.checkPlaylistExists(playListId)

    override suspend fun deletePlaylistEntities(playlistEntities: List<ru.stersh.apisonic.room.playlist.PlaylistEntity>) =
        playlistDao.deletePlaylists(playlistEntities)

    override suspend fun renamePlaylistEntity(playlistId: Long, name: String) =
        playlistDao.renamePlaylist(playlistId, name)

    override suspend fun deleteSongsInPlaylist(songs: List<ru.stersh.apisonic.room.playlist.SongEntity>) {
        songs.forEach {
            playlistDao.deleteSongFromPlaylist(it.playlistCreatorId, it.id)
        }
    }

    override suspend fun deletePlaylistSongs(playlists: List<ru.stersh.apisonic.room.playlist.PlaylistEntity>) =
        playlists.forEach {
            playlistDao.deletePlaylistSongs(it.playListId)
        }

    override suspend fun favoritePlaylist(favorite: String): ru.stersh.apisonic.room.playlist.PlaylistEntity {
        val playlist: ru.stersh.apisonic.room.playlist.PlaylistEntity? = playlistDao.playlist(favorite).firstOrNull()
        return if (playlist != null) {
            playlist
        } else {
            createPlaylist(ru.stersh.apisonic.room.playlist.PlaylistEntity(playlistName = favorite))
            playlistDao.playlist(favorite).first()
        }
    }

    override suspend fun isFavoriteSong(songEntity: ru.stersh.apisonic.room.playlist.SongEntity): List<ru.stersh.apisonic.room.playlist.SongEntity> =
        playlistDao.isSongExistsInPlaylist(
            songEntity.playlistCreatorId,
            songEntity.id
        )

    override suspend fun removeSongFromPlaylist(songEntity: ru.stersh.apisonic.room.playlist.SongEntity) =
        playlistDao.deleteSongFromPlaylist(songEntity.playlistCreatorId, songEntity.id)

    override suspend fun addSongToHistory(currentSong: Song) =
        historyDao.insertSongInHistory(currentSong.toHistoryEntity(System.currentTimeMillis()))

    override suspend fun songPresentInHistory(song: Song): ru.stersh.apisonic.room.history.HistoryEntity? =
        historyDao.isSongPresentInHistory(song.id)

    override suspend fun updateHistorySong(song: Song) =
        historyDao.updateHistorySong(song.toHistoryEntity(System.currentTimeMillis()))

    override fun observableHistorySongs(): LiveData<List<ru.stersh.apisonic.room.history.HistoryEntity>> =
        historyDao.observableHistorySongs()

    override fun historySongs(): List<ru.stersh.apisonic.room.history.HistoryEntity> = historyDao.historySongs()

    override fun favoritePlaylistLiveData(favorite: String): LiveData<List<ru.stersh.apisonic.room.playlist.SongEntity>> =
        playlistDao.favoritesSongsLiveData(favorite)

    override suspend fun favoritePlaylistSongs(favorite: String): List<ru.stersh.apisonic.room.playlist.SongEntity> =
        if (playlistDao.playlist(favorite).isNotEmpty()) playlistDao.favoritesSongs(
            playlistDao.playlist(favorite).first().playListId
        ) else emptyList()

    override suspend fun insertSongInPlayCount(playCountEntity: ru.stersh.apisonic.room.playcount.PlayCountEntity) =
        playCountDao.insertSongInPlayCount(playCountEntity)

    override suspend fun updateSongInPlayCount(playCountEntity: ru.stersh.apisonic.room.playcount.PlayCountEntity) =
        playCountDao.updateSongInPlayCount(playCountEntity)

    override suspend fun deleteSongInPlayCount(playCountEntity: ru.stersh.apisonic.room.playcount.PlayCountEntity) =
        playCountDao.deleteSongInPlayCount(playCountEntity)

    override suspend fun deleteSongInHistory(songId: Long) {
        historyDao.deleteSongInHistory(songId)
    }

    override suspend fun clearSongHistory() {
        historyDao.clearHistory()
    }

    override suspend fun checkSongExistInPlayCount(songId: Long): List<ru.stersh.apisonic.room.playcount.PlayCountEntity> =
        playCountDao.checkSongExistInPlayCount(songId)

    override suspend fun playCountSongs(): List<ru.stersh.apisonic.room.playcount.PlayCountEntity> =
        playCountDao.playCountSongs()

    override suspend fun deleteSongs(songs: List<Song>) = songs.forEach {
        playCountDao.deleteSong(it.id)
    }

    override suspend fun isSongFavorite(context: Context, songId: Long): Boolean {
        return playlistDao.isSongExistsInPlaylist(
            playlistDao.playlist(context.getString(R.string.favorites)).firstOrNull()?.playListId
                ?: -1,
            songId
        ).isNotEmpty()
    }
}