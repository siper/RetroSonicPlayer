package code.name.monkey.retromusic.feature.player.cover.domain

import kotlinx.coroutines.flow.Flow

interface CoverArtUrlsRepository {
    fun getCoverArtUrls(): Flow<CoverArtUrlList>
}