package ru.stersh.retrosonic.player.controls.domain

interface PlayerControls {
    fun play()
    fun pause()
    fun next()
    fun previous()
    fun seek(time: Long)
}