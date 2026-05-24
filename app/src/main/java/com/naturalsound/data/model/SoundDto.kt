package com.naturalsound.data.model

import com.naturalsound.domain.model.Sound
import com.naturalsound.domain.model.SoundCategory

data class SoundDto(
    val id: String = "",
    val name: String = "",
    val url: String = "",
    val category: String = "ALL",
    val emoji: String = "🎵",
    val tags: List<String> = emptyList()
)

private fun mapCategory(raw: String): SoundCategory = when (raw.lowercase()) {
    "rain"               -> SoundCategory.RAIN
    "birds"              -> SoundCategory.BIRDS
    "fire"               -> SoundCategory.FIRE
    "ocean", "waterfull" -> SoundCategory.OCEAN
    "forest"             -> SoundCategory.FOREST
    "storm", "drop"      -> SoundCategory.STORM
    "wind"               -> SoundCategory.WIND
    "space"              -> SoundCategory.SPACE
    else                 -> SoundCategory.ALL
}

fun SoundDto.toDomain(): Sound = Sound(
    id          = id,
    name        = name,
    category    = mapCategory(category),
    firebaseUrl = url,
    emoji       = emoji,
    tags        = tags
)
