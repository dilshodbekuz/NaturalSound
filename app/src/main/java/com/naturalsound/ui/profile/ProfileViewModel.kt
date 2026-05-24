package com.naturalsound.ui.profile

import androidx.lifecycle.ViewModel
import com.naturalsound.data.prefs.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class ProfileUiState(
    val userName: String = "",
    val eqBass:   Float  = 0f,
    val eqTreble: Float  = 0f
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefs: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState(userName = prefs.userName))
    val uiState: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun setGlobalBass(v: Float)    = _state.update { it.copy(eqBass = v) }
    fun setGlobalTreble(v: Float)  = _state.update { it.copy(eqTreble = v) }

    fun logout(onDone: () -> Unit) {
        prefs.clearAll()
        onDone()
    }
}
