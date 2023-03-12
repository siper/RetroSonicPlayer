package code.name.monkey.retromusic.feature.settings.server.presentation

internal data class ServerSettingsUi(
    val title: String,
    val address: String,
    val username: String,
    val password: String,
    val isActive: Boolean
)