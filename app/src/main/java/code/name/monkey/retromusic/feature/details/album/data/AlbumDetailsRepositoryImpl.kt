package code.name.monkey.retromusic.feature.details.album.data

import code.name.monkey.retromusic.feature.details.album.domain.AlbumDetails
import code.name.monkey.retromusic.feature.details.album.domain.AlbumDetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stersh.apisonic.models.Album
import ru.stersh.apisonic.models.Artist
import ru.stersh.apisonic.models.Song
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider

class AlbumDetailsRepositoryImpl(private val apiSonicProvider: ApiSonicProvider) : AlbumDetailsRepository {
    override fun getAlbumDetails(id: String): Flow<AlbumDetails> = flow {
        val album = apiSonicProvider.getApiSonic().getAlbum(id)
        val artist = apiSonicProvider.getApiSonic().getArtist(album.artistId)

        album
            .toDomain(artist.toDomain())
            .also { emit(it) }
    }

    private suspend fun Album.toDomain(artist: AlbumDetails.Artist): AlbumDetails {
        return AlbumDetails(
            id = id,
            title = name,
            artist = artist,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
            year = year,
            songs = song?.map { it.toDomain() } ?: emptyList()
        )
    }

    private suspend fun Artist.toDomain(): AlbumDetails.Artist {
        return AlbumDetails.Artist(
            id = id,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
            title = name
        )
    }

    private suspend fun Song.toDomain(): AlbumDetails.Song {
        return AlbumDetails.Song(
            id = id,
            albumId = albumId,
            coverArtUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
            year = year,
            title = title,
            artist = artist,
            album = album
        )
    }
}