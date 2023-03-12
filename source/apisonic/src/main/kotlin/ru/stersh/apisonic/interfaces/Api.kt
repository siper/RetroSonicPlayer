package ru.stersh.apisonic.interfaces

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import ru.stersh.apisonic.models.*

interface Api {

    @GET("rest/ping")
    suspend fun ping(
    ): Response<EmptyResponse>

    @GET("rest/getLicense")
    suspend fun getLicense(
    ): Response<LicenseResponse>

    @GET("rest/getNowPlaying")
    suspend fun getNowPlaying(
    ): Response<NowPlayingResponse>

    @GET("rest/getArtists")
    suspend fun getArtists(
    ): Response<ArtistsResponse>

    @GET("rest/getGenre")
    suspend fun getGenres(
    ): Response<GenreResponse>

    @GET("rest/getArtist")
    suspend fun getArtist(
        @Query("id") id: String
    ): Response<ArtistResponse>

    @GET("rest/getAlbum")
    suspend fun getAlbum(
        @Query("id") id: String
    ): Response<AlbumResponse>

    @GET("rest/getSong")
    suspend fun getSong(
        @Query("id") id: String
    ): Response<SongResponse>

    @GET("rest/getVideos")
    suspend fun getVideos(
    ): Response<VideosResponse>

    @GET("rest/getVideoInfo")
    suspend fun getVideoInfo(
        @Query("id") id: String
    ): Response<VideoInfoResponse>

    @GET("rest/getArtistInfo")
    suspend fun getArtistInfo(
        @Query("id") id: String,
        @Query("count") count: Int?,
        @Query("includeNotPresent") includeNotPresent: Boolean?
    ): Response<ArtistInfoResponse>

    @GET("rest/getArtistInfo2")
    suspend fun getArtistInfo2(
        @Query("id") id: String,
        @Query("count") count: Int?,
        @Query("includeNotPresent") includeNotPresent: Boolean?
    ): Response<ArtistInfo2Response>

    @GET("rest/getSimilarSongs")
    suspend fun getSimilarSongs(
        @Query("id") id: String,
        @Query("count") count: Int?
    ): Response<SimilarSongsResponse>

    @GET("rest/getSimilarSongs2")
    suspend fun getSimilarSongs2(
        @Query("id") id: String,
        @Query("count") count: Int?
    ): Response<SimilarSongsResponse>

    @GET("rest/getTopSongs")
    suspend fun getTopSongs(
        @Query("artist") artist: String,
        @Query("count") count: Int?
    ): Response<TopSongsResponse>

    @GET("rest/getRandomSongs")
    fun getRandomSongs(
        @Query("size") size: Int?,
        @Query("genre") genre: String?,
        @Query("fromYear") fromYear: Int?,
        @Query("toYear") toYear: Int?,
        @Query("musicFolderId") musicFolderId: String?
    ): Response<RandomSongsResponse>

    @GET("rest/getRandomSongs")
    fun getSongsByGenre(
        @Query("genre") genre: String,
        @Query("count") count: Int?,
        @Query("offset") offset: Int?,
        @Query("musicFolderId") musicFolderId: String?
    ): Response<SongsByGenreResponse>

    @GET("rest/getMusicFolders")
    suspend fun getMusicFolders(
    ): Response<MusicFoldersResponse>

    @GET("rest/getIndexes")
    suspend fun getIndexes(
        @Query("musicFolderId") musicFolderId: String?,
        @Query("ifModifiedSince") ifModifiedSince: Long?
    ): Response<IndexesResponse>

    @GET("rest/getMusicDirectory")
    suspend fun getMusicDirectory(
        @Query("id") id: String
    ): Response<MusicDirectoryResponse>

    @GET("rest/startScan")
    suspend fun startScan(
    ): Response<ScanStatusResponse>

    @GET("rest/getScanStatus")
    suspend fun getScanStatus(
    ): Response<ScanStatusResponse>

    @GET("rest/getAlbumList")
    suspend fun getAlbumList(
        @Query("type") type: String,
        @Query("size") size: Int?,
        @Query("offset") offset: Int?,
        @Query("fromYear") fromYear: Int?,
        @Query("toYear") toYear: Int?,
        @Query("genre") genre: String?,
        @Query("musicFolderId") musicFolderId: String?
    ): Response<AlbumListResponse>

    @GET("rest/getAlbumList2")
    suspend fun getAlbumList2(
        @Query("type") type: String,
        @Query("size") size: Int?,
        @Query("offset") offset: Int?,
        @Query("fromYear") fromYear: Int?,
        @Query("toYear") toYear: Int?,
        @Query("genre") genre: String?,
        @Query("musicFolderId") musicFolderId: String?
    ): Response<AlbumList2Response>

    @GET("rest/getStarred")
    suspend fun getStarred(
        @Query("musicFolderId") musicFolderId: String?
    ): Response<StarredResponse>


    @GET("rest/getStarred2")
    suspend fun getStarred2(
        @Query("musicFolderId") musicFolderId: String?
    ): Response<Starred2Response>

    @GET("rest/search")
    suspend fun search(
        @Query("artist") artist: String?,
        @Query("album") album: String?,
        @Query("title") title: String?,
        @Query("any") any: String?,
        @Query("count") count: Int?,
        @Query("offset") offset: Int?,
        @Query("newerThan") newerThan: Long?
    ): Response<SearchResponse>

    @GET("rest/search2")
    suspend fun search2(
        @Query("query") query: String,
        @Query("artistCount") artistCount: Int?,
        @Query("artistOffset") artistOffset: Int?,
        @Query("albumCount") albumCount: Int?,
        @Query("albumOffset") albumOffset: Int?,
        @Query("songCount") songCount: Int?,
        @Query("songOffset") songOffset: Int?,
        @Query("musicFolderId") musicFolderId: String?
    ): Response<Search2Response>

    @GET("rest/search3")
    suspend fun search3(
        @Query("query") query: String,
        @Query("artistCount") artistCount: Int?,
        @Query("artistOffset") artistOffset: Int?,
        @Query("albumCount") albumCount: Int?,
        @Query("albumOffset") albumOffset: Int?,
        @Query("songCount") songCount: Int?,
        @Query("songOffset") songOffset: Int?,
        @Query("musicFolderId") musicFolderId: String?
    ): Response<Search3Response>

    @GET("rest/star")
    suspend fun star(
        @Query("id") id: List<String>? = null,
        @Query("albumId") albumIds: List<String>? = null,
        @Query("artistId") artistIds: List<String>? = null
    ): Response<EmptyResponse>

    @GET("rest/unstar")
    suspend fun unstar(
        @Query("id") id: List<String>? = null,
        @Query("albumId") albumIds: List<String>? = null,
        @Query("artistId") artistIds: List<String>? = null
    ): Response<EmptyResponse>

    @GET("rest/setRating")
    suspend fun setRating(
        @Query("id") id: String,
        @Query("rating") rating: Int
    ): Response<EmptyResponse>

    @GET("rest/scrobble")
    suspend fun scrobble(
        @Query("id") id: String,
        @Query("time") time: Long?,
        @Query("submission") submission: Boolean?
    ): Response<EmptyResponse>

    @GET("rest/getPlaylists")
    suspend fun getPlaylists(
        @Query("username") username: String?
    ): Response<PlaylistsResponse>

    @GET("rest/getPlaylist")
    suspend fun getPlaylist(
        @Query("id") id: String
    ): Response<PlaylistResponse>

    @GET("rest/createPlaylist")
    suspend fun createPlaylist(
        @Query("playlistId") playlistId: String?,
        @Query("name") name: String?,
        @Query("songId") songIds: List<String>?
    ): Response<EmptyResponse>

    @GET("rest/updatePlaylist")
    suspend fun updatePlaylist(
        @Query("playlistId") playlistId: String?,
        @Query("name") name: String?,
        @Query("comment") comment: String?,
        @Query("public") public: Boolean?,
        @Query("songIdsToAdd") songIdsToAdd: List<String>?,
        @Query("songIndicesToRemove") songIndicesToRemove: List<String>?
    ): Response<EmptyResponse>

    @GET("rest/deletePlaylist")
    fun deletePlaylist(
        @Query("playlistId") playlistId: String
    ): Response<EmptyResponse>

    @GET("rest/download")
    fun download(
        @Query("id") id: String
    ): ResponseBody

    @GET("rest/stream")
    fun stream(
        @Query("id") id: String,
        @Query("maxBitRate") maxBitRate: Int?,
        @Query("format") format: String?,
        @Query("timeOffset") timeOffset: Int?,
        @Query("size") size: String?,
        @Query("estimateContentLength") estimateContentLength: Boolean?,
        @Query("converted") converted: Boolean?
    ): ResponseBody

    @GET("rest/hls.m3u8")
    fun hls(
        @Query("id") id: String,
        @Query("bitRate") bitRate: List<String>?,
        @Query("audioTrack") audioTrack: String?
    ): ResponseBody


    @GET("rest/getCaptions")
    fun getCaptions(
        @Query("id") id: String,
        @Query("format") format: String?
    ): ResponseBody

    @GET("rest/getLyrics")
    fun getLyrics(
        @Query("artist") artist: String?,
        @Query("title") title: String?
    ): ResponseBody

    @GET("rest/getCoverArt")
    fun getCoverArt(
        @Query("id") id: String,
        @Query("size") size: Int?
    ): ResponseBody

    @GET("rest/getAvatar")
    fun getAvatar(
        @Query("username") username: String
    ): ResponseBody
}
