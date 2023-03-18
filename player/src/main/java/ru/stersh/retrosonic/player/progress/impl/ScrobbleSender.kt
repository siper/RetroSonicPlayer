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
package ru.stersh.retrosonic.player.progress.impl

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.stersh.apisonic.provider.apisonic.ApiSonicProvider
import timber.log.Timber

internal class ScrobbleSender(
    val id: String,
    private val apiSonicProvider: ApiSonicProvider,
) {
    private var scrobbleSent = false
    private var submissionSent = false

    private val sendMutex = Mutex()

    suspend fun trySendScrobble() = sendMutex.withLock {
        if (scrobbleSent) {
            return@withLock
        }
        runCatching {
            apiSonicProvider
                .getApiSonic()
                .scrobble(
                    id = id,
                    time = System.currentTimeMillis(),
                )
            scrobbleSent = true
        }.onFailure { Timber.w(it) }
    }

    suspend fun trySendSubmission() = sendMutex.withLock {
        if (submissionSent) {
            return@withLock
        }
        runCatching {
            apiSonicProvider
                .getApiSonic()
                .scrobble(
                    id = id,
                    time = System.currentTimeMillis(),
                    submission = true,
                )
            submissionSent = true
        }.onFailure { Timber.w(it) }
    }
}
