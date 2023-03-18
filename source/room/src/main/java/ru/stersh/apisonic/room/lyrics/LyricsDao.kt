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
package ru.stersh.apisonic.room.lyrics

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LyricsDao {
    @Query("SELECT * FROM LyricsEntity WHERE songId =:songId LIMIT 1")
    fun lyricsWithSongId(songId: Int): LyricsEntity?

    @Insert
    fun insertLyrics(lyricsEntity: LyricsEntity)

    @Delete
    fun deleteLyrics(lyricsEntity: LyricsEntity)

    @Update
    fun updateLyrics(lyricsEntity: LyricsEntity)
}
