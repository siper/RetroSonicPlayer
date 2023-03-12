package code.name.monkey.retromusic.feature.library.album.data

import code.name.monkey.retromusic.feature.library.album.domain.LibraryAlbum
import code.name.monkey.retromusic.feature.library.album.domain.LibraryAlbumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stersh.apisonic.models.AlbumList2
import ru.stersh.apisonic.models.ListType
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider

class LibraryAlbumRepositoryImpl(private val apiSonicProvider: ApiSonicProvider) : LibraryAlbumRepository {
    override fun getAlbums(): Flow<List<LibraryAlbum>> = flow {
        apiSonicProvider
            .getApiSonic()
            .getAlbumList2(ListType.ALPHABETICAL_BY_ARTIST, size = 500)
            .map { it.toLibraryAlbum() }
            .also { emit(it) }
    }

    private suspend fun AlbumList2.Album.toLibraryAlbum(): LibraryAlbum {
        return LibraryAlbum(
            id = id,
            title = name,
            artist = artist,
            coverUrl = apiSonicProvider.getApiSonic().getCoverArtUrl(coverArt),
            year = year
        )
    }
}