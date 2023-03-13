package code.name.monkey.retromusic.feature.settings.server.presentation

internal fun isValidAddress(address: String?): Boolean {
    return address != null
            && (address.startsWith("http://") || address.startsWith("https://"))
            && address.endsWith("/")
}