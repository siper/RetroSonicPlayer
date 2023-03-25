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
package ru.stersh.retrosonic.feature.mylibrary.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import ru.stersh.apisonic.room.serversettings.ServerSettingsDao
import ru.stersh.retrosonic.feature.mylibrary.domain.UserRepository

internal class UserRepositoryImpl(
    private val serverSettingsDao: ServerSettingsDao,
    private val apiSonicProvider: ApiSonicProvider,
) : UserRepository {
    override fun getUsername(): Flow<String> {
        return serverSettingsDao
            .flowActive()
            .mapNotNull { it?.username }
    }

    override fun getAvatarUrl(): Flow<String> {
        return getUsername().flatMapLatest { username ->
            flow {
                apiSonicProvider
                    .getApiSonic()
                    .getAvatarUrl(username)
                    .also { emit(it) }
            }
        }
    }
}
