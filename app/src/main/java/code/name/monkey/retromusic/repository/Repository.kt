/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package code.name.monkey.retromusic.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import code.name.monkey.retromusic.*
import code.name.monkey.retromusic.db.fromHistoryToSongs
import code.name.monkey.retromusic.db.toSong
import code.name.monkey.retromusic.fragments.search.Filter
import code.name.monkey.retromusic.model.*
import code.name.monkey.retromusic.model.smartplaylist.NotPlayedPlaylist
import code.name.monkey.retromusic.network.LastFMService
import code.name.monkey.retromusic.network.Result
import code.name.monkey.retromusic.network.Result.Error
import code.name.monkey.retromusic.network.Result.Success
import code.name.monkey.retromusic.network.model.LastFmAlbum
import code.name.monkey.retromusic.network.model.LastFmArtist
import code.name.monkey.retromusic.util.logE

interface Repository {

    fun historySong(): List<ru.stersh.apisonic.room.history.HistoryEntity>
    fun favorites(): LiveData<List<ru.stersh.apisonic.room.playlist.SongEntity>>
    fun observableHistorySongs(): LiveData<List<Song>>
    fun albumById(albumId: Long): Album
    fun playlistSongs(playListId: Long): LiveData<List<ru.stersh.apisonic.room.playlist.SongEntity>>
    suspend fun fetchAlbums(): List<Album>
    suspend fun albumByIdAsync(albumId: Long): Album
    suspend fun allSongs(): List<Song>
    suspend fun fetchArtists(): List<Artist>
    suspend fun albumArtists(): List<Artist>
    suspend fun fetchLegacyPlaylist(): List<Playlist>
    suspend fun fetchGenres(): List<Genre>
    suspend fun search(query: String?, filter: Filter): MutableList<Any>
    suspend fun getPlaylistSongs(playlist: Playlist): List<Song>
    suspend fun getGenre(genreId: Long): List<Song>
    suspend fun artistInfo(name: String, lang: String?, cache: String?): Result<LastFmArtist>
    suspend fun albumInfo(artist: String, album: String): Result<LastFmAlbum>
    suspend fun artistById(artistId: Long): Artist
    suspend fun albumArtistByName(name: String): Artist
    suspend fun recentArtists(): List<Artist>
    suspend fun topArtists(): List<Artist>
    suspend fun topAlbums(): List<Album>
    suspend fun recentAlbums(): List<Album>
    suspend fun recentArtistsHome(): Home
    suspend fun topArtistsHome(): Home
    suspend fun topAlbumsHome(): Home
    suspend fun recentAlbumsHome(): Home
    suspend fun favoritePlaylistHome(): Home
    suspend fun suggestions(): List<Song>
    suspend fun genresHome(): Home
    suspend fun playlists(): Home
    suspend fun homeSections(): List<Home>
    suspend fun playlist(playlistId: Long): Playlist
    suspend fun fetchPlaylistWithSongs(): List<ru.stersh.apisonic.room.playlist.PlaylistWithSongs>
    suspend fun playlistSongs(playlistWithSongs: ru.stersh.apisonic.room.playlist.PlaylistWithSongs): List<Song>
    suspend fun insertSongs(songs: List<ru.stersh.apisonic.room.playlist.SongEntity>)
    suspend fun checkPlaylistExists(playlistName: String): List<ru.stersh.apisonic.room.playlist.PlaylistEntity>
    suspend fun createPlaylist(playlistEntity: ru.stersh.apisonic.room.playlist.PlaylistEntity): Long
    suspend fun fetchPlaylists(): List<ru.stersh.apisonic.room.playlist.PlaylistEntity>
    suspend fun deleteRoomPlaylist(playlists: List<ru.stersh.apisonic.room.playlist.PlaylistEntity>)
    suspend fun renameRoomPlaylist(playlistId: Long, name: String)
    suspend fun deleteSongsInPlaylist(songs: List<ru.stersh.apisonic.room.playlist.SongEntity>)
    suspend fun removeSongFromPlaylist(songEntity: ru.stersh.apisonic.room.playlist.SongEntity)
    suspend fun deletePlaylistSongs(playlists: List<ru.stersh.apisonic.room.playlist.PlaylistEntity>)
    suspend fun favoritePlaylist(): ru.stersh.apisonic.room.playlist.PlaylistEntity
    suspend fun isFavoriteSong(songEntity: ru.stersh.apisonic.room.playlist.SongEntity): List<ru.stersh.apisonic.room.playlist.SongEntity>
    suspend fun addSongToHistory(currentSong: Song)
    suspend fun songPresentInHistory(currentSong: Song): ru.stersh.apisonic.room.history.HistoryEntity?
    suspend fun updateHistorySong(currentSong: Song)
    suspend fun favoritePlaylistSongs(): List<ru.stersh.apisonic.room.playlist.SongEntity>
    suspend fun recentSongs(): List<Song>
    suspend fun topPlayedSongs(): List<Song>
    suspend fun insertSongInPlayCount(playCountEntity: ru.stersh.apisonic.room.playcount.PlayCountEntity)
    suspend fun updateSongInPlayCount(playCountEntity: ru.stersh.apisonic.room.playcount.PlayCountEntity)
    suspend fun deleteSongInPlayCount(playCountEntity: ru.stersh.apisonic.room.playcount.PlayCountEntity)
    suspend fun deleteSongInHistory(songId: Long)
    suspend fun clearSongHistory()
    suspend fun checkSongExistInPlayCount(songId: Long): List<ru.stersh.apisonic.room.playcount.PlayCountEntity>
    suspend fun playCountSongs(): List<ru.stersh.apisonic.room.playcount.PlayCountEntity>
    suspend fun deleteSongs(songs: List<Song>)
    suspend fun contributor(): List<Contributor>
    suspend fun searchArtists(query: String): List<Artist>
    suspend fun searchSongs(query: String): List<Song>
    suspend fun searchAlbums(query: String): List<Album>
    suspend fun isSongFavorite(songId: Long): Boolean
    fun getSongByGenre(genreId: Long): Song
    fun checkPlaylistExists(playListId: Long): LiveData<Boolean>
}

class RealRepository(
    private val context: Context,
    private val lastFMService: LastFMService,
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val genreRepository: GenreRepository,
    private val lastAddedRepository: LastAddedRepository,
    private val playlistRepository: PlaylistRepository,
    private val searchRepository: RealSearchRepository,
    private val topPlayedRepository: TopPlayedRepository,
    private val roomRepository: RoomRepository,
    private val localDataRepository: LocalDataRepository,
) : Repository {

    override suspend fun deleteSongs(songs: List<Song>) = roomRepository.deleteSongs(songs)

    override suspend fun contributor(): List<Contributor> = localDataRepository.contributors()

    override suspend fun searchSongs(query: String): List<Song> = songRepository.songs(query)

    override suspend fun searchAlbums(query: String): List<Album> = albumRepository.albums(query)

    override suspend fun isSongFavorite(songId: Long): Boolean =
        roomRepository.isSongFavorite(context, songId)

    override fun getSongByGenre(genreId: Long): Song = genreRepository.song(genreId)

    override suspend fun searchArtists(query: String): List<Artist> =
        artistRepository.artists(query)

    override suspend fun fetchAlbums(): List<Album> = albumRepository.albums()

    override suspend fun albumByIdAsync(albumId: Long): Album = albumRepository.album(albumId)

    override fun albumById(albumId: Long): Album = albumRepository.album(albumId)

    override suspend fun fetchArtists(): List<Artist> = artistRepository.artists()

    override suspend fun albumArtists(): List<Artist> = artistRepository.albumArtists()

    override suspend fun artistById(artistId: Long): Artist = artistRepository.artist(artistId)

    override suspend fun albumArtistByName(name: String): Artist =
        artistRepository.albumArtist(name)

    override suspend fun recentArtists(): List<Artist> = lastAddedRepository.recentArtists()

    override suspend fun recentAlbums(): List<Album> = lastAddedRepository.recentAlbums()

    override suspend fun topArtists(): List<Artist> = topPlayedRepository.topArtists()

    override suspend fun topAlbums(): List<Album> = topPlayedRepository.topAlbums()

    override suspend fun fetchLegacyPlaylist(): List<Playlist> = playlistRepository.playlists()

    override suspend fun fetchGenres(): List<Genre> = genreRepository.genres()

    override suspend fun allSongs(): List<Song> = songRepository.songs()

    override suspend fun search(query: String?, filter: Filter): MutableList<Any> =
        searchRepository.searchAll(context, query, filter)

    override suspend fun getPlaylistSongs(playlist: Playlist): List<Song> =
        if (playlist is AbsCustomPlaylist) {
            playlist.songs()
        } else {
            PlaylistSongsLoader.getPlaylistSongList(context, playlist.id)
        }

    override suspend fun getGenre(genreId: Long): List<Song> = genreRepository.songs(genreId)

    override suspend fun artistInfo(
        name: String,
        lang: String?,
        cache: String?,
    ): Result<LastFmArtist> {
        return try {
            Success(lastFMService.artistInfo(name, lang, cache))
        } catch (e: Exception) {
            logE(e)
            Error(e)
        }
    }

    override suspend fun albumInfo(
        artist: String,
        album: String,
    ): Result<LastFmAlbum> {
        return try {
            val lastFmAlbum = lastFMService.albumInfo(artist, album)
            Success(lastFmAlbum)
        } catch (e: Exception) {
            logE(e)
            Error(e)
        }
    }

    override suspend fun homeSections(): List<Home> {
        val homeSections = mutableListOf<Home>()
        val sections: List<Home> = listOf(
            topArtistsHome(),
            topAlbumsHome(),
            recentArtistsHome(),
            recentAlbumsHome(),
            favoritePlaylistHome()
        )
        for (section in sections) {
            if (section.arrayList.isNotEmpty()) {
                homeSections.add(section)
            }
        }
        return homeSections
    }


    override suspend fun playlist(playlistId: Long) =
        playlistRepository.playlist(playlistId)

    override suspend fun fetchPlaylistWithSongs(): List<ru.stersh.apisonic.room.playlist.PlaylistWithSongs> =
        roomRepository.playlistWithSongs()

    override suspend fun playlistSongs(playlistWithSongs: ru.stersh.apisonic.room.playlist.PlaylistWithSongs): List<Song> =
        playlistWithSongs.songs.map {
            it.toSong()
        }

    override fun playlistSongs(playListId: Long): LiveData<List<ru.stersh.apisonic.room.playlist.SongEntity>> =
        roomRepository.getSongs(playListId)

    override suspend fun insertSongs(songs: List<ru.stersh.apisonic.room.playlist.SongEntity>) =
        roomRepository.insertSongs(songs)

    override suspend fun checkPlaylistExists(playlistName: String): List<ru.stersh.apisonic.room.playlist.PlaylistEntity> =
        roomRepository.checkPlaylistExists(playlistName)

    override fun checkPlaylistExists(playListId: Long): LiveData<Boolean> =
        roomRepository.checkPlaylistExists(playListId)

    override suspend fun createPlaylist(playlistEntity: ru.stersh.apisonic.room.playlist.PlaylistEntity): Long =
        roomRepository.createPlaylist(playlistEntity)

    override suspend fun fetchPlaylists(): List<ru.stersh.apisonic.room.playlist.PlaylistEntity> = roomRepository.playlists()

    override suspend fun deleteRoomPlaylist(playlists: List<ru.stersh.apisonic.room.playlist.PlaylistEntity>) =
        roomRepository.deletePlaylistEntities(playlists)

    override suspend fun renameRoomPlaylist(playlistId: Long, name: String) =
        roomRepository.renamePlaylistEntity(playlistId, name)

    override suspend fun deleteSongsInPlaylist(songs: List<ru.stersh.apisonic.room.playlist.SongEntity>) =
        roomRepository.deleteSongsInPlaylist(songs)

    override suspend fun removeSongFromPlaylist(songEntity: ru.stersh.apisonic.room.playlist.SongEntity) =
        roomRepository.removeSongFromPlaylist(songEntity)

    override suspend fun deletePlaylistSongs(playlists: List<ru.stersh.apisonic.room.playlist.PlaylistEntity>) =
        roomRepository.deletePlaylistSongs(playlists)

    override suspend fun favoritePlaylist(): ru.stersh.apisonic.room.playlist.PlaylistEntity =
        roomRepository.favoritePlaylist(context.getString(R.string.favorites))

    override suspend fun isFavoriteSong(songEntity: ru.stersh.apisonic.room.playlist.SongEntity): List<ru.stersh.apisonic.room.playlist.SongEntity> =
        roomRepository.isFavoriteSong(songEntity)

    override suspend fun addSongToHistory(currentSong: Song) =
        roomRepository.addSongToHistory(currentSong)

    override suspend fun songPresentInHistory(currentSong: Song): ru.stersh.apisonic.room.history.HistoryEntity? =
        roomRepository.songPresentInHistory(currentSong)

    override suspend fun updateHistorySong(currentSong: Song) =
        roomRepository.updateHistorySong(currentSong)

    override suspend fun favoritePlaylistSongs(): List<ru.stersh.apisonic.room.playlist.SongEntity> =
        roomRepository.favoritePlaylistSongs(context.getString(R.string.favorites))

    override suspend fun recentSongs(): List<Song> = lastAddedRepository.recentSongs()

    override suspend fun topPlayedSongs(): List<Song> = topPlayedRepository.topTracks()

    override suspend fun insertSongInPlayCount(playCountEntity: ru.stersh.apisonic.room.playcount.PlayCountEntity) =
        roomRepository.insertSongInPlayCount(playCountEntity)

    override suspend fun updateSongInPlayCount(playCountEntity: ru.stersh.apisonic.room.playcount.PlayCountEntity) =
        roomRepository.updateSongInPlayCount(playCountEntity)

    override suspend fun deleteSongInPlayCount(playCountEntity: ru.stersh.apisonic.room.playcount.PlayCountEntity) =
        roomRepository.deleteSongInPlayCount(playCountEntity)

    override suspend fun deleteSongInHistory(songId: Long) =
        roomRepository.deleteSongInHistory(songId)

    override suspend fun clearSongHistory() {
        roomRepository.clearSongHistory()
    }

    override suspend fun checkSongExistInPlayCount(songId: Long): List<ru.stersh.apisonic.room.playcount.PlayCountEntity> =
        roomRepository.checkSongExistInPlayCount(songId)

    override suspend fun playCountSongs(): List<ru.stersh.apisonic.room.playcount.PlayCountEntity> =
        roomRepository.playCountSongs()

    override fun observableHistorySongs(): LiveData<List<Song>> =
        Transformations.map(roomRepository.observableHistorySongs()) {
            it.fromHistoryToSongs()
        }

    override fun historySong(): List<ru.stersh.apisonic.room.history.HistoryEntity> =
        roomRepository.historySongs()

    override fun favorites(): LiveData<List<ru.stersh.apisonic.room.playlist.SongEntity>> =
        roomRepository.favoritePlaylistLiveData(context.getString(R.string.favorites))

    override suspend fun suggestions(): List<Song> {
        return NotPlayedPlaylist().songs().shuffled().takeIf {
            it.size > 9
        } ?: emptyList()
    }

    override suspend fun genresHome(): Home {
        val genres = genreRepository.genres().shuffled()
        return Home(genres, GENRES, R.string.genres)
    }

    override suspend fun playlists(): Home {
        val playlist = playlistRepository.playlists()
        return Home(playlist, PLAYLISTS, R.string.playlists)
    }

    override suspend fun recentArtistsHome(): Home {
        val artists = lastAddedRepository.recentArtists().take(5)
        return Home(artists, RECENT_ARTISTS, R.string.recent_artists)
    }

    override suspend fun recentAlbumsHome(): Home {
        val albums = lastAddedRepository.recentAlbums().take(5)
        return Home(albums, RECENT_ALBUMS, R.string.recent_albums)
    }

    override suspend fun topAlbumsHome(): Home {
        val albums = topPlayedRepository.topAlbums().take(5)
        return Home(albums, TOP_ALBUMS, R.string.top_albums)
    }

    override suspend fun topArtistsHome(): Home {
        val artists = topPlayedRepository.topArtists().take(5)
        return Home(artists, TOP_ARTISTS, R.string.top_artists)
    }

    override suspend fun favoritePlaylistHome(): Home {
        val songs = favoritePlaylistSongs().map {
            it.toSong()
        }
        return Home(songs, FAVOURITES, R.string.favorites)
    }
}