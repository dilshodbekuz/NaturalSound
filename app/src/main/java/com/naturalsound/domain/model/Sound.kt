package com.naturalsound.domain.model

data class Sound(
    val id: String = "",
    val name: String = "",
    val category: SoundCategory = SoundCategory.ALL,
    val firebaseUrl: String = "",
    val localPath: String? = null,
    val durationMinutes: Int = 0,
    val emoji: String = "🎵",
    val isFavorite: Boolean = false,
    val isOfflineAvailable: Boolean = false,
    val tags: List<String> = emptyList()
) {
    val isDownloaded: Boolean get() = localPath != null
    val playbackUrl: String get() = localPath ?: firebaseUrl
}

enum class SoundCategory(val label: String, val emoji: String) {
    ALL("Hammasi", "🌍"),
    RAIN("Yomg'ir", "🌧️"),
    FOREST("O'rmon", "🌲"),
    OCEAN("Oqean", "🌊"),
    FIRE("Olov", "🔥"),
    STORM("Bo'ron", "⛈️"),
    BIRDS("Qushlar", "🐦"),
    WIND("Shamol", "💨"),
    SPACE("Koinot", "🌌")
}
