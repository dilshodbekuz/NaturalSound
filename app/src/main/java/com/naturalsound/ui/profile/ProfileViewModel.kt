package com.naturalsound.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class ProfileUiState(
    val autoDownloadOnWifi: Boolean = true,
    val notificationsOn:    Boolean = true,
    val nightMode:          Boolean = false,
    val eqBass:             Float   = 0f,
    val eqTreble:           Float   = 0f
)

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _state.asStateFlow()

    fun toggleAutoDownload() = _state.update { it.copy(autoDownloadOnWifi = !it.autoDownloadOnWifi) }
    fun toggleNotifications() = _state.update { it.copy(notificationsOn = !it.notificationsOn) }
    fun toggleNightMode()     = _state.update { it.copy(nightMode = !it.nightMode) }
    fun setGlobalBass(v: Float)   = _state.update { it.copy(eqBass = v) }
    fun setGlobalTreble(v: Float) = _state.update { it.copy(eqTreble = v) }
}
