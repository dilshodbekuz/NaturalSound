package com.naturalsound.domain.model

data class Sound(
    val id: String = "",
    val name: String = "",
    val category: SoundCategory = SoundCategory.ALL,
    val firebaseUrl: String = "",
    val emoji: String = "🎵",
    val tags: List<String> = emptyList()
)

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
