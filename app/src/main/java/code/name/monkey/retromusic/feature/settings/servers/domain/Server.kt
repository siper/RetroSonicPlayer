package code.name.monkey.retromusic.feature.settings.servers.domain

internal data class Server(
    val id: Long,
    val title: String,
    val url: String,
    val isActive: Boolean
)