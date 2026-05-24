package com.naturalsound.ui

import androidx.lifecycle.ViewModel
import com.naturalsound.data.prefs.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val prefs: UserPreferences
) : ViewModel() {

    private val _lang = MutableStateFlow(prefs.languageCode)
    val lang: StateFlow<String> = _lang.asStateFlow()

    private val _theme = MutableStateFlow(prefs.themeMode)
    val theme: StateFlow<String> = _theme.asStateFlow()

    fun setLanguage(code: String) {
        prefs.languageCode = code
        _lang.value = code
    }

    fun setTheme(mode: String) {
        prefs.themeMode = mode
        _theme.value = mode
    }
}
