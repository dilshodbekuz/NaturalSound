package com.naturalsound.domain.usecase

import com.naturalsound.domain.model.Sound
import com.naturalsound.domain.model.SoundCategory
import com.naturalsound.domain.repository.SoundRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSoundsUseCase @Inject constructor(
    private val repository: SoundRepository
) {
    operator fun invoke(
        category: SoundCategory = SoundCategory.ALL,
        searchQuery: String = ""
    ): Flow<List<Sound>> {
        val base = if (category == SoundCategory.ALL)
            repository.getAllSounds()
        else
            repository.getSoundsByCategory(category)

        return base.map { sounds ->
            if (searchQuery.isBlank()) sounds
            else sounds.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.label.contains(searchQuery, ignoreCase = true)
            }
        }
    }
}
