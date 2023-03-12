package code.name.monkey.retromusic.feature.home.domain

import kotlinx.coroutines.flow.Flow

internal interface UserRepository {
    fun getUsername(): Flow<String>

    fun getAvatarUrl(): Flow<String>
}