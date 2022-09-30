package code.name.monkey.retromusic.feature.player.controls.presentation

data class PlayerProgressUi(
    val total: String,
    val current: String,
    val totalMs: Long,
    val currentMs: Long
)
