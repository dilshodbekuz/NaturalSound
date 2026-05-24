package com.naturalsound.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naturalsound.domain.model.Sound
import com.naturalsound.domain.model.SoundCategory
import com.naturalsound.domain.usecase.GetSoundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class HomeUiState(
    val sounds: List<Sound> = emptyList(),
    val activeSoundIds: Set<String> = emptySet(),
    val selectedCategory: SoundCategory = SoundCategory.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val activeCount: Int get() = activeSoundIds.size
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSoundsUseCase: GetSoundsUseCase
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(SoundCategory.ALL)
    private val _searchQuery      = MutableStateFlow("")
    private val _activeSoundIds   = MutableStateFlow<Set<String>>(emptySet())
    private val _error            = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        _selectedCategory.flatMapLatest { category ->
            _searchQuery.debounce(300L).flatMapLatest { query ->
                getSoundsUseCase(category, query)
            }
        },
        _activeSoundIds,
        _selectedCategory,
        _searchQuery,
        _error
    ) { sounds, activeIds, category, query, error ->
        HomeUiState(
            sounds           = sounds,
            activeSoundIds   = activeIds,
            selectedCategory = category,
            searchQuery      = query,
            isLoading        = false,
            error            = error
        )
    }.catch { e ->
        emit(HomeUiState(isLoading = false, error = e.message))
    }.stateIn(
        scope          = viewModelScope,
        started        = SharingStarted.WhileSubscribed(5000),
        initialValue   = HomeUiState()
    )

    fun selectCategory(category: SoundCategory) { _selectedCategory.value = category }
    fun onSearch(query: String) { _searchQuery.value = query }
    fun updateActiveSounds(ids: Set<String>) { _activeSoundIds.value = ids }
    fun clearError() { _error.value = null }
}
