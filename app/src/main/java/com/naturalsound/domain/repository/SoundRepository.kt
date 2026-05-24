package com.naturalsound.domain.repository

import com.naturalsound.domain.model.Sound
import com.naturalsound.domain.model.SoundCategory
import kotlinx.coroutines.flow.Flow

interface SoundRepository {
    fun getAllSounds(): Flow<List<Sound>>
    fun getSoundsByCategory(category: SoundCategory): Flow<List<Sound>>
    fun searchSounds(query: String): Flow<List<Sound>>
    suspend fun toggleFavorite(soundId: String, isFavorite: Boolean)
    suspend fun markAsDownloaded(soundId: String, localPath: String)
    fun getFavoriteSounds(): Flow<List<Sound>>
}
