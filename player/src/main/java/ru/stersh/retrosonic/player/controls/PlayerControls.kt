package ru.stersh.retrosonic.player.controls

interface PlayerControls {
    fun play()
    fun pause()
    fun next()
    fun previous()
    fun seek(time: Long)

    fun toggleFavorite(favorite: Boolean)
}