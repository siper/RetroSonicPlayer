package code.name.monkey.retromusic.feature.library.artist.data

import code.name.monkey.retromusic.feature.library.artist.domain.LibraryArtist
import code.name.monkey.retromusic.feature.library.artist.domain.LibraryArtistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.stersh.apisonic.models.Artist
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider

class LibraryArtistRepositoryImpl(private val apiSonicProvider: ApiSonicProvider) : LibraryArtistRepository {
    override fun getArtists(): Flow<List<LibraryArtist>> = flow {
        apiSonicProvider
            .getApiSonic()
            .getArtists()
            .asArtistList()
            .map { it.toDomain() }
            .also { emit(it) }
    }

    private suspend fun Artist.toDomain(): LibraryArtist {
        return LibraryArtist(
            id = id,
            title = name,
            coverArtUrl = apiSonicProvider
                .getApiSonic()
                .getCoverArtUrl(coverArt)
        )
    }
}