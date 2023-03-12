package ru.stersh.retrosonic.player.progress

data class PlayerProgress(
    val currentTimeMs: Long,
    val totalTimeMs: Long,
    val currentTime: String,
    val totalTime: String
)
