package code.name.monkey.retromusic.feature.settings.server.domain

internal data class ServerSettings(
    val id: Long?,
    val title: String,
    val address: String,
    val username: String,
    val password: String,
    val isActive: Boolean,
    val useLegacyAuth: Boolean
)