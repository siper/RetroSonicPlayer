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
package ru.stersh.retrosonic.model.smartplaylist

import kotlinx.parcelize.Parcelize
import org.koin.core.component.KoinComponent
import ru.stersh.retrosonic.App
import ru.stersh.retrosonic.R
import ru.stersh.retrosonic.model.Song

@Parcelize
class HistoryPlaylist :
    AbsSmartPlaylist(
        name = App.getContext().getString(R.string.history),
        iconRes = R.drawable.ic_history,
    ),
    KoinComponent {

    override fun songs(): List<Song> {
        return topPlayedRepository.recentlyPlayedTracks()
    }
}
