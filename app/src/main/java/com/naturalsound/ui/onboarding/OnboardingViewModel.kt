package com.naturalsound.ui.onboarding

import androidx.lifecycle.ViewModel
import com.naturalsound.data.prefs.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class OnboardingState(
    val selectedLang: String = "uz",
    val name:         String = ""
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefs: UserPreferences
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun isOnboardingDone(): Boolean = prefs.isOnboardingDone

    fun selectLanguage(code: String) = _state.update { it.copy(selectedLang = code) }

    fun setName(name: String) = _state.update { it.copy(name = name) }

    /** LanguageScreen: tilni saqlaydi va keyingi ekranga o'tadi */
    fun saveLanguageAndProceed(onNext: () -> Unit) {
        prefs.languageCode = _state.value.selectedLang
        onNext()
    }

    /** NameScreen: ismni saqlaydi, onboardingni yakunlaydi */
    fun saveAndFinish(onDone: () -> Unit) {
        val name = _state.value.name.trim()
        if (name.isEmpty()) return
        prefs.userName         = name
        prefs.isOnboardingDone = true
        onDone()
    }
}
