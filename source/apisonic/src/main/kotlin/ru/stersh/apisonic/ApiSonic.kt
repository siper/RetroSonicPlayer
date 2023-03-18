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
package ru.stersh.apisonic

import android.net.Uri
import okhttp3.ResponseBody
import ru.stersh.apisonic.interfaces.Api
import ru.stersh.apisonic.models.Album
import ru.stersh.apisonic.models.AlbumList
import ru.stersh.apisonic.models.AlbumList2
import ru.stersh.apisonic.models.Artist
import ru.stersh.apisonic.models.ArtistInfo
import ru.stersh.apisonic.models.Artists
import ru.stersh.apisonic.models.Directory
import ru.stersh.apisonic.models.EmptyResponse
import ru.stersh.apisonic.models.Genre
import ru.stersh.apisonic.models.Indexes
import ru.stersh.apisonic.models.License
import ru.stersh.apisonic.models.MusicFolder
import ru.stersh.apisonic.models.NowPlayingEntry
import ru.stersh.apisonic.models.PlayQueueResponse
import ru.stersh.apisonic.models.PlaylistResponse
import ru.stersh.apisonic.models.Playlists
import ru.stersh.apisonic.models.ScanStatus
import ru.stersh.apisonic.models.SearchResult
import ru.stersh.apisonic.models.SearchResult2
import ru.stersh.apisonic.models.SearchResult3
import ru.stersh.apisonic.models.Song
import ru.stersh.apisonic.models.Starred
import ru.stersh.apisonic.models.Starred2
import ru.stersh.apisonic.models.Video
import ru.stersh.apisonic.models.VideoInfo
import ru.stersh.apisonic.network.AuthenticationInterceptor
import ru.stersh.apisonic.network.NetworkFactory

class ApiSonic(
    val url: String,
    val username: String,
    val password: String,
    val apiVersion: String,
    val clientId: String,
    val useLegacyAuth: Boolean,
    logLevel: LogLevel = LogLevel.NONE,
) {
    private val authenticationInterceptor = AuthenticationInterceptor(
        username,
        password,
        apiVersion,
        clientId,
        useLegacyAuth,
    )
    private val network = NetworkFactory(authenticationInterceptor, logLevel)

    private val api = network.createApi(Api::class.java, url)

    private fun buildUrlManually(path: String, queryMap: Map<String, Any?>): String {
        val uriBuilder = Uri
            .parse(url)
            .buildUpon()
            .appendPath("rest")
            .appendPath(path)

        queryMap.forEach { entry ->
            if (entry.value != null) {
                uriBuilder.appendQueryParameter(entry.key, entry.value.toString())
            }
        }

        uriBuilder.appendAuth()

        return uriBuilder
            .build()
            .toString()
    }

    private fun Uri.Builder.appendAuth() {
        appendQueryParameter("u", username)
        appendQueryParameter("c", clientId)
        appendQueryParameter("v", apiVersion)
        appendQueryParameter("f", "json")

        if (useLegacyAuth) {
            appendQueryParameter("p", password)
        } else {
            val salt = Security.generateSalt()
            val token = Security.getToken(salt, password)
            appendQueryParameter("s", salt)
            appendQueryParameter("t", token)
        }
    }

    suspend fun ping(): EmptyResponse = api.ping().subsonicResponse

    suspend fun getLicense(): License = api.getLicense().subsonicResponse.license

    suspend fun savePlayQueue(
        id: List<String>,
        current: String? = null,
        position: Long? = null
    ): EmptyResponse = api.savePlayQueue(id, current, position).subsonicResponse

    suspend fun getPlayQueue(): PlayQueueResponse = api.getPlayQueue().subsonicResponse

    suspend fun getArtists(): Artists = api.getArtists().subsonicResponse.artists


    suspend fun getGenres(): List<Genre> = api.getGenres().subsonicResponse.genres.genres


    suspend fun getArtist(id: String): Artist = api.getArtist(id).subsonicResponse.artist


    suspend fun getAlbum(id: String): Album = api.getAlbum(id).subsonicResponse.album


    suspend fun getSong(id: String): Song = api.getSong(id).subsonicResponse.song


    suspend fun getVideos(): List<Video> = api.getVideos().subsonicResponse.videos.videos


    suspend fun getVideoInfo(id: String): VideoInfo =
        api.getVideoInfo(id).subsonicResponse.videoInfo


    suspend fun getArtistInfo(
        id: String,
        count: Int? = null,
        includeNotPresent: Boolean? = null,
    ): ArtistInfo = api.getArtistInfo(id, count, includeNotPresent).subsonicResponse.artistInfo


    suspend fun getArtistInfo2(
        id: String,
        count: Int? = null,
        includeNotPresent: Boolean? = null,
    ): ArtistInfo = api.getArtistInfo2(id, count, includeNotPresent).subsonicResponse.artistInfo2

    suspend fun getSimilarSongs(
        id: String,
        count: Int? = null,
    ): List<Song> {
        return api.getSimilarSongs(id, count).subsonicResponse.similarSongs.similarSongs
    }


    suspend fun getSimilarSongs2(
        id: String,
        count: Int? = null,
    ): List<Song> {
        return api.getSimilarSongs2(id, count).subsonicResponse.similarSongs.similarSongs
    }


    suspend fun getTopSongs(
        artist: String,
        count: Int? = null,
    ): List<Song> {
        return api.getTopSongs(artist, count).subsonicResponse.topSongs.topSongs
    }


    suspend fun getMusicFolders(): List<MusicFolder> {
        return api.getMusicFolders().subsonicResponse.musicFolders.musicFolders
    }


    suspend fun getIndexes(
        musicFolderId: String? = null,
        ifModifiedSince: Long? = null,
    ): Indexes {
        return api.getIndexes(musicFolderId, ifModifiedSince).subsonicResponse.indexes
    }


    suspend fun getMusicDirectory(
        id: String,
    ): Directory {
        return api.getMusicDirectory(id).subsonicResponse.directory
    }

    enum class ListType(val value: String) {
        RANDOM("random"),
        NEWEST("newest"),
        HIGHEST("highest"),
        FREQUENT("frequent"),
        RECENT("recent"),
        ALPHABETICAL_BY_NAME("alphabeticalByName"),
        ALPHABETICAL_BY_ARTIST("alphabeticalByArtist"),
        STARRED("starred"),
        BY_YEAR("byYear"),
        BY_GENRE("byGenre"),
    }


    suspend fun getAlbumList(
        type: ListType,
        size: Int? = null,
        offset: Int? = null,
        fromYear: Int? = null,
        toYear: Int? = null,
        genre: String? = null,
        musicFolderId: String? = null,
    ): List<AlbumList.Album> = api.getAlbumList(
        type.value,
        size,
        offset,
        fromYear,
        toYear,
        genre,
        musicFolderId,
    ).subsonicResponse.albumList.albums


    suspend fun getAlbumList2(
        type: ru.stersh.apisonic.models.ListType,
        size: Int? = null,
        offset: Int? = null,
        fromYear: Int? = null,
        toYear: Int? = null,
        genre: String? = null,
        musicFolderId: String? = null,
    ): List<AlbumList2.Album> = api.getAlbumList2(
        type.value,
        size,
        offset,
        fromYear,
        toYear,
        genre,
        musicFolderId,
    ).subsonicResponse.albumList2.albums


    suspend fun getRandomSongs(
        size: Int? = null,
        genre: String? = null,
        fromYear: Int? = null,
        toYear: Int? = null,
        musicFolderId: String? = null,
    ): List<Song> = api.getRandomSongs(
        size,
        genre,
        fromYear,
        toYear,
        musicFolderId,
    ).subsonicResponse.randomSongs.randomSongs


    suspend fun getSongsByGenre(
        genre: String,
        count: Int? = null,
        offset: Int? = null,
        musicFolderId: String? = null,
    ): List<Song> = api.getSongsByGenre(
        genre,
        count,
        offset,
        musicFolderId,
    ).subsonicResponse.songsByGenre.songsByGenre


    suspend fun getNowPlaying(): List<NowPlayingEntry> =
        api.getNowPlaying().subsonicResponse.nowPlaying.entries


    suspend fun getStarred(
        musicFolderId: String? = null,
    ): Starred = api.getStarred(musicFolderId).subsonicResponse.starred


    suspend fun getStarred2(
        musicFolderId: String? = null,
    ): Starred2 = api.getStarred2(musicFolderId).subsonicResponse.starred2


    @Deprecated("Deprecated since 1.4.0, use search2 instead.")
    suspend fun search(
        artist: String? = null,
        album: String? = null,
        title: String? = null,
        any: String? = null,
        count: Int? = null,
        offset: Int? = null,
        newerThan: Long? = null,
    ): SearchResult = api.search(
        artist,
        album,
        title,
        any,
        count,
        offset,
        newerThan,
    ).subsonicResponse.searchResult


    suspend fun search2(
        query: String,
        artistCount: Int? = null,
        artistOffset: Int? = null,
        albumCount: Int? = null,
        albumOffset: Int? = null,
        songCount: Int? = null,
        songOffset: Int? = null,
        musicFolderId: String? = null,
    ): SearchResult2 = api
        .search2(
            query,
            artistCount,
            artistOffset,
            albumCount,
            albumOffset,
            songCount,
            songOffset,
            musicFolderId,
        )
        .subsonicResponse
        .searchResult2


    suspend fun search3(
        query: String,
        artistCount: Int? = null,
        artistOffset: Int? = null,
        albumCount: Int? = null,
        albumOffset: Int? = null,
        songCount: Int? = null,
        songOffset: Int? = null,
        musicFolderId: String? = null,
    ): SearchResult3 = api
        .search3(
            query,
            artistCount,
            artistOffset,
            albumCount,
            albumOffset,
            songCount,
            songOffset,
            musicFolderId,
        )
        .subsonicResponse
        .searchResult3


    suspend fun startScan(): ScanStatus = api.startScan().subsonicResponse.scanStatus


    suspend fun getScanStatus(): ScanStatus = api.getScanStatus().subsonicResponse.scanStatus


    suspend fun starFileOrFolder(
        vararg id: String,
    ): EmptyResponse {
        return api.star(id = id.asList()).subsonicResponse
    }


    suspend fun starSong(
        vararg songId: String,
    ): EmptyResponse {
        return api.star(id = songId.asList()).subsonicResponse
    }


    suspend fun starAlbum(
        vararg albumId: String,
    ): EmptyResponse {
        return api.star(albumIds = albumId.asList()).subsonicResponse
    }


    suspend fun starArtist(
        vararg artistId: String,
    ): EmptyResponse {
        return api.star(artistIds = artistId.asList()).subsonicResponse
    }


    suspend fun unstarFileOrFolder(
        vararg id: String,
    ): EmptyResponse {
        return api.unstar(id = id.asList()).subsonicResponse
    }


    suspend fun unstarSong(vararg id: String): EmptyResponse =
        api.unstar(id = id.asList()).subsonicResponse


    suspend fun unstarAlbum(vararg albumId: String): EmptyResponse =
        api.unstar(albumIds = albumId.asList()).subsonicResponse


    suspend fun unstarArtist(vararg artistId: String): EmptyResponse =
        api.unstar(artistIds = artistId.asList()).subsonicResponse

    enum class Rating(val value: Int) {
        REMOVE_RATING(0),
        RATE_1_STAR(1),
        RATE_2_STAR(2),
        RATE_3_STAR(3),
        RATE_4_STAR(4),
        RATE_5_STAR(5),
    }


    suspend fun setRating(
        id: String,
        rating: Rating,
    ): EmptyResponse = api.setRating(id, rating.value).subsonicResponse


    suspend fun removeRating(
        id: String,
    ): EmptyResponse = api.setRating(id, Rating.REMOVE_RATING.value).subsonicResponse


    suspend fun scrobble(
        id: String,
        time: Long? = null,
        submission: Boolean? = null,
    ): EmptyResponse = api.scrobble(id, time, submission).subsonicResponse


    suspend fun getPlaylists(
        username: String? = null,
    ): List<Playlists.Playlist> =
        api.getPlaylists(username).subsonicResponse.playlists.playlists ?: emptyList()


    suspend fun getPlaylist(
        id: String,
    ): PlaylistResponse.Playlist = api.getPlaylist(id).subsonicResponse.playlist


    suspend fun createPlaylist(
        name: String,
        songIds: List<String>? = null,
    ): EmptyResponse =
        api.createPlaylist(playlistId = null, name = name, songIds = songIds).subsonicResponse


    suspend fun overridePlaylist(
        playlistId: String,
        songIds: List<String>? = null,
    ): EmptyResponse =
        api.createPlaylist(playlistId = playlistId, name = null, songIds = songIds).subsonicResponse


    suspend fun overridePlaylist(
        playlistId: String,
        name: String? = null,
        comment: String? = null,
        public: Boolean? = null,
        songIdsToAdd: List<String>? = null,
        songIndicesToRemove: List<String>? = null,
    ): EmptyResponse =
        api.updatePlaylist(
            playlistId,
            name,
            comment,
            public,
            songIdsToAdd,
            songIndicesToRemove,
        ).subsonicResponse

    // TODO Helper method to remove songIds from playlist, not index


    suspend fun deletePlaylist(
        playlistId: String,
    ): EmptyResponse =
        api.deletePlaylist(playlistId).subsonicResponse


    fun streamAudioUrl(
        id: String,
        maxBitRate: Int? = null,
        format: String? = null,
        estimateContentLength: Boolean? = null,
        transcode: Boolean = false,
    ): String = buildUrlManually(
        "stream",
        mapOf(
            "id" to id,
            "maxBitRate" to maxBitRate,
            "format" to if (transcode) "raw" else format,
            "estimateContentLength" to estimateContentLength,
        ),
    )


    fun streamVideoUrl(
        id: String,
        maxBitRate: Int? = null,
        format: String? = null,
        timeOffset: Int? = null,
        size: String? = null,
        estimateContentLength: Boolean? = null,
        converted: Boolean? = null,
        transcode: Boolean = false,
    ): String = buildUrlManually(
        "stream",
        mapOf(
            "id" to id,
            "maxBitRate" to maxBitRate,
            "format" to if (transcode) "raw" else format,
            "timeOffset" to timeOffset,
            "size" to size,
            "estimateContentLength" to estimateContentLength,
            "converted" to converted,
        ),
    )

    // TODO
    fun downloadUrl(
        id: String,
    ): String = buildUrlManually("download", mapOf("id" to id))


    fun hlsUrl(
        id: String,
        bitRate: List<String>? = null,
        audioTrack: String?,
    ): String = buildUrlManually(
        "hls",
        mapOf(
            "id" to id,
            "audioTrack" to audioTrack,
            *(bitRate?.map { "bitRate" to it }?.toTypedArray() ?: emptyArray()),
        ),
    )


    fun getCaptionsUrl(
        id: String,
        format: String? = null,
    ): String = buildUrlManually(
        "getCaptions",
        mapOf("id" to id, "format" to format),
    )


    fun getLyricsUrl(
        artist: String? = null,
        title: String? = null,
    ): String = buildUrlManually(
        "getLyrics",
        mapOf("artist" to artist, "title" to title),
    )


    fun getCoverArtUrl(
        id: String,
        size: Int? = null,
    ): String = buildUrlManually(
        "getCoverArt",
        mapOf<String, Any?>("id" to id, "size" to size),
    )


    fun getAvatarUrl(
        username: String,
    ): String = buildUrlManually(
        "getAvatar",
        mapOf("username" to username),
    )


    fun streamAudio(
        id: String,
        maxBitRate: Int? = null,
        format: String? = null,
        estimateContentLength: Boolean? = null,
        transcode: Boolean = false,
    ): ResponseBody = api.stream(
        id = id,
        maxBitRate = maxBitRate,
        format = if (transcode) "raw" else format,
        estimateContentLength = estimateContentLength,
        timeOffset = null,
        size = null,
        converted = null,
    )


    fun streamVideo(
        id: String,
        maxBitRate: Int? = null,
        format: String? = null,
        timeOffset: Int? = null,
        estimateContentLength: Boolean? = null,
        size: String? = null,
        converted: Boolean? = null,
        transcode: Boolean = false,
    ): ResponseBody = api.stream(
        id = id,
        maxBitRate = maxBitRate,
        format = if (transcode) "raw" else format,
        estimateContentLength = estimateContentLength,
        timeOffset = timeOffset,
        size = size,
        converted = converted,
    )


    suspend fun download(
        id: String,
    ): ResponseBody = api.download(id)


    suspend fun hls(
        id: String,
        bitRate: List<String>? = null,
        audioTrack: String?,
    ): ResponseBody = api.hls(id, bitRate, audioTrack)


    suspend fun getCaptions(
        id: String,
        format: String? = null,
    ): ResponseBody = api.getCaptions(id, format)


    suspend fun getLyrics(
        artist: String? = null,
        title: String? = null,
    ): ResponseBody = api.getLyrics(artist, title)


    suspend fun getCoverArt(
        id: String,
        size: Int? = null,
    ): ResponseBody = api.getCoverArt(id, size)


    suspend fun getAvatar(
        username: String,
    ): ResponseBody = api.getAvatar(username)
}
