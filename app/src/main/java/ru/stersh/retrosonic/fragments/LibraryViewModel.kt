/*
 * Copyright (c) 2020 Retro Sonic contributors.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 */
package ru.stersh.retrosonic.fragments

import android.animation.ValueAnimator
import android.content.Context
import androidx.core.animation.doOnEnd
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.RECENT_ALBUMS
import ru.stersh.retrosonic.RECENT_ARTISTS
import ru.stersh.retrosonic.TOP_ALBUMS
import ru.stersh.retrosonic.TOP_ARTISTS
import ru.stersh.retrosonic.extensions.showToast
import ru.stersh.retrosonic.fragments.ReloadType.Albums
import ru.stersh.retrosonic.fragments.ReloadType.Artists
import ru.stersh.retrosonic.fragments.ReloadType.Genres
import ru.stersh.retrosonic.fragments.ReloadType.HomeSections
import ru.stersh.retrosonic.fragments.ReloadType.Playlists
import ru.stersh.retrosonic.fragments.ReloadType.Songs
import ru.stersh.retrosonic.fragments.ReloadType.Suggestions
import ru.stersh.retrosonic.fragments.search.Filter
import ru.stersh.retrosonic.model.Album
import ru.stersh.retrosonic.model.Artist
import ru.stersh.retrosonic.model.Contributor
import ru.stersh.retrosonic.model.Genre
import ru.stersh.retrosonic.model.Home
import ru.stersh.retrosonic.model.Playlist
import ru.stersh.retrosonic.model.Song
import ru.stersh.retrosonic.repository.Repository
import ru.stersh.retrosonic.toSong
import ru.stersh.retrosonic.toSongEntity
import ru.stersh.retrosonic.util.DensityUtil
import ru.stersh.retrosonic.util.PreferenceUtil
import java.io.File

class LibraryViewModel(
    private val repository: Repository,
) : ViewModel() {

    private val _paletteColor = MutableLiveData<Int>()
    private val home = MutableLiveData<List<Home>>()
    private val suggestions = MutableLiveData<List<Song>>()
    private val albums = MutableLiveData<List<Album>>()
    private val songs = MutableLiveData<List<Song>>()
    private val artists = MutableLiveData<List<Artist>>()
    private val playlists = MutableLiveData<List<ru.stersh.apisonic.room.playlist.PlaylistWithSongs>>()
    private val genres = MutableLiveData<List<Genre>>()
    private val searchResults = MutableLiveData<List<Any>>()
    private val fabMargin = MutableLiveData(0)
    private val songHistory = MutableLiveData<List<Song>>()
    private var previousSongHistory = ArrayList<ru.stersh.apisonic.room.history.HistoryEntity>()
    val paletteColor: LiveData<Int> = _paletteColor

    init {
        loadLibraryContent()
    }

    private fun loadLibraryContent() = viewModelScope.launch(IO) {
        fetchHomeSections()
        fetchSuggestions()
        fetchSongs()
        fetchAlbums()
        fetchArtists()
        fetchGenres()
        fetchPlaylists()
    }

    fun getSearchResult(): LiveData<List<Any>> = searchResults

    fun getSongs(): LiveData<List<Song>> = songs

    fun getArtists(): LiveData<List<Artist>> = artists

    fun getPlaylists(): LiveData<List<ru.stersh.apisonic.room.playlist.PlaylistWithSongs>> = playlists

    fun getGenre(): LiveData<List<Genre>> = genres

    fun getHome(): LiveData<List<Home>> = home

    fun getSuggestions(): LiveData<List<Song>> = suggestions

    fun getFabMargin(): LiveData<Int> = fabMargin

    private suspend fun fetchSongs() {
        songs.postValue(repository.allSongs())
    }

    private suspend fun fetchAlbums() {
        albums.postValue(repository.fetchAlbums())
    }

    private suspend fun fetchArtists() {
        if (PreferenceUtil.albumArtistsOnly) {
            artists.postValue(repository.albumArtists())
        } else {
            artists.postValue(repository.fetchArtists())
        }
    }

    private suspend fun fetchPlaylists() {
        playlists.postValue(repository.fetchPlaylistWithSongs())
    }

    private suspend fun fetchGenres() {
        genres.postValue(repository.fetchGenres())
    }

    private suspend fun fetchHomeSections() {
        home.postValue(repository.homeSections())
    }

    private suspend fun fetchSuggestions() {
        suggestions.postValue(repository.suggestions())
    }

    fun search(query: String?, filter: Filter) =
        viewModelScope.launch(IO) {
            val result = repository.search(query, filter)
            searchResults.postValue(result)
        }

    fun forceReload(reloadType: ReloadType) = viewModelScope.launch(IO) {
        when (reloadType) {
            Songs -> fetchSongs()
            Albums -> fetchAlbums()
            Artists -> fetchArtists()
            HomeSections -> fetchHomeSections()
            Playlists -> fetchPlaylists()
            Genres -> fetchGenres()
            Suggestions -> fetchSuggestions()
        }
    }

    fun updateColor(newColor: Int) {
        _paletteColor.postValue(newColor)
    }

    fun shuffleSongs() = viewModelScope.launch(IO) {
//        val songs = repository.allSongs()
//        withContext(Main) {
//            MusicPlayerRemote.openAndShuffleQueue(songs, true)
//        }
    }

    fun renameRoomPlaylist(playListId: Long, name: String) = viewModelScope.launch(IO) {
        repository.renameRoomPlaylist(playListId, name)
    }

    fun deleteSongsInPlaylist(songs: List<ru.stersh.apisonic.room.playlist.SongEntity>) {
        viewModelScope.launch(IO) {
            repository.deleteSongsInPlaylist(songs)
            forceReload(Playlists)
        }
    }

    fun deleteSongsFromPlaylist(playlists: List<ru.stersh.apisonic.room.playlist.PlaylistEntity>) = viewModelScope.launch(IO) {
        repository.deletePlaylistSongs(playlists)
    }

    fun deleteRoomPlaylist(playlists: List<ru.stersh.apisonic.room.playlist.PlaylistEntity>) = viewModelScope.launch(IO) {
        repository.deleteRoomPlaylist(playlists)
    }

    fun albumById(id: Long) = repository.albumById(id)
    suspend fun artistById(id: Long) = repository.artistById(id)
    suspend fun favoritePlaylist() = repository.favoritePlaylist()
    suspend fun isFavoriteSong(song: ru.stersh.apisonic.room.playlist.SongEntity) = repository.isFavoriteSong(song)
    suspend fun isSongFavorite(songId: Long) = repository.isSongFavorite(songId)
    suspend fun insertSongs(songs: List<ru.stersh.apisonic.room.playlist.SongEntity>) = repository.insertSongs(songs)
    suspend fun removeSongFromPlaylist(songEntity: ru.stersh.apisonic.room.playlist.SongEntity) =
        repository.removeSongFromPlaylist(songEntity)

    private suspend fun checkPlaylistExists(playlistName: String): List<ru.stersh.apisonic.room.playlist.PlaylistEntity> =
        repository.checkPlaylistExists(playlistName)

    private suspend fun createPlaylist(playlistEntity: ru.stersh.apisonic.room.playlist.PlaylistEntity): Long =
        repository.createPlaylist(playlistEntity)

    fun importPlaylists() = viewModelScope.launch(IO) {
        val playlists = repository.fetchLegacyPlaylist()
        playlists.forEach { playlist ->
            val playlistEntity = repository.checkPlaylistExists(playlist.name).firstOrNull()
            if (playlistEntity != null) {
                val songEntities = playlist.getSongs().map {
                    it.toSongEntity(playlistEntity.playListId)
                }
                repository.insertSongs(songEntities)
            } else {
                if (playlist != Playlist.empty) {
                    val playListId = createPlaylist(ru.stersh.apisonic.room.playlist.PlaylistEntity(playlistName = playlist.name))
                    val songEntities = playlist.getSongs().map {
                        it.toSongEntity(playListId)
                    }
                    repository.insertSongs(songEntities)
                }
            }
            forceReload(Playlists)
        }
    }

    fun recentSongs(): LiveData<List<Song>> = liveData(IO) {
        emit(repository.recentSongs())
    }

    fun playCountSongs(): LiveData<List<Song>> = liveData(IO) {
        repository.playCountSongs().forEach { song ->
            if (!File(song.data).exists() || song.id == -1L) {
                repository.deleteSongInPlayCount(song)
            }
        }
        emit(
            repository.playCountSongs().map {
                it.toSong()
            },
        )
    }

    fun artists(type: Int): LiveData<List<Artist>> = liveData(IO) {
        when (type) {
            TOP_ARTISTS -> emit(repository.topArtists())
            RECENT_ARTISTS -> {
                emit(repository.recentArtists())
            }
        }
    }

    fun albums(type: Int): LiveData<List<Album>> = liveData(IO) {
        when (type) {
            TOP_ALBUMS -> emit(repository.topAlbums())
            RECENT_ALBUMS -> {
                emit(repository.recentAlbums())
            }
        }
    }

    fun artist(artistId: Long): LiveData<Artist> = liveData(IO) {
        emit(repository.artistById(artistId))
    }

    fun fetchContributors(): LiveData<List<Contributor>> = liveData(IO) {
        emit(repository.contributor())
    }

    fun observableHistorySongs(): LiveData<List<Song>> {
        viewModelScope.launch(IO) {
            repository.historySong().forEach { song ->
                if (!File(song.data).exists() || song.id == -1L) {
                    repository.deleteSongInHistory(song.id)
                }
            }

            songHistory.postValue(
                repository.historySong().map {
                    it.toSong()
                },
            )
        }
        return songHistory
    }

    fun clearHistory() {
        viewModelScope.launch(IO) {
            previousSongHistory = repository.historySong() as ArrayList<ru.stersh.apisonic.room.history.HistoryEntity>

            repository.clearSongHistory()
        }
        songHistory.value = emptyList()
    }

    fun restoreHistory() {
        viewModelScope.launch(IO) {
            if (previousSongHistory.isNotEmpty()) {
                val history = ArrayList<Song>()
                for (song in previousSongHistory) {
                    repository.addSongToHistory(song.toSong())
                    history.add(song.toSong())
                }
                songHistory.postValue(history)
            }
        }
    }

    fun favorites() = repository.favorites()

    fun clearSearchResult() {
        searchResults.value = emptyList()
    }

    fun addToPlaylist(context: Context, playlistName: String, songs: List<Song>) {
        viewModelScope.launch(IO) {
            val playlists = checkPlaylistExists(playlistName)
            if (playlists.isEmpty()) {
                val playlistId: Long =
                    createPlaylist(ru.stersh.apisonic.room.playlist.PlaylistEntity(playlistName = playlistName))
                insertSongs(songs.map { it.toSongEntity(playlistId) })
                withContext(Main) {
                    context.showToast(
                        context.getString(
                            R.string.playlist_created_sucessfully,
                            playlistName,
                        ),
                    )
                }
            } else {
                val playlist = playlists.firstOrNull()
                if (playlist != null) {
                    insertSongs(
                        songs.map {
                            it.toSongEntity(playListId = playlist.playListId)
                        },
                    )
                }
            }
            forceReload(Playlists)
            withContext(Main) {
                context.showToast(
                    context.getString(
                        R.string.added_song_count_to_playlist,
                        songs.size,
                        playlistName,
                    ),
                )
            }
        }
    }

    fun setFabMargin(context: Context, bottomMargin: Int) {
        val currentValue = DensityUtil.dip2px(context, 16F) +
            bottomMargin
        ValueAnimator.ofInt(fabMargin.value!!, currentValue).apply {
            addUpdateListener {
                fabMargin.postValue(
                    (it.animatedValue as Int),
                )
            }
            doOnEnd {
                fabMargin.postValue(currentValue)
            }
            start()
        }
    }
}

enum class ReloadType {
    Songs,
    Albums,
    Artists,
    HomeSections,
    Playlists,
    Genres,
    Suggestions,
}
