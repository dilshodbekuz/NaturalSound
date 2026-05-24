package com.naturalsound.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naturalsound.data.prefs.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class TimerUiState(
    // ── Uyqu taymeri ─────────────────────────────────────────────────────────
    val sleepTimerOn:    Boolean = false,
    val remainingMs:     Long    = 30 * 60 * 1000L,
    val selectedMinutes: Int     = 30,
    // ── Statistika ────────────────────────────────────────────────────────────
    val todaySeconds:    Int     = 0,
    val streakDays:      Int     = 0,
    val todaySessions:   Int     = 0,
)

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val prefs: UserPreferences
) : ViewModel() {

    // sdf init blokidan OLDIN e'lon qilinishi shart
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _state = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var statsJob: Job? = null
    private var prevActiveCount = 0

    init {
        resetDailyIfNeeded()
        syncStatsToState()
    }

    // ── Uyqu taymeri ─────────────────────────────────────────────────────────

    fun setTimer(minutes: Int) {
        _state.update { it.copy(selectedMinutes = minutes, remainingMs = minutes * 60_000L) }
        if (_state.value.sleepTimerOn) startCountdown()
    }

    fun startTimer() {
        _state.update { it.copy(sleepTimerOn = true) }
        startCountdown()
    }

    fun stopTimer() {
        _state.update { it.copy(sleepTimerOn = false) }
        stopCountdown()
    }

    fun addMinutes(minutes: Int) {
        _state.update { it.copy(remainingMs = it.remainingMs + minutes * 60_000L) }
    }

    private fun startCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_state.value.remainingMs > 0 && _state.value.sleepTimerOn) {
                delay(1000)
                _state.update { it.copy(remainingMs = maxOf(0, it.remainingMs - 1000)) }
            }
            if (_state.value.remainingMs == 0L) {
                _state.update { it.copy(sleepTimerOn = false) }
            }
        }
    }

    private fun stopCountdown() {
        timerJob?.cancel()
        _state.update { it.copy(remainingMs = _state.value.selectedMinutes * 60_000L) }
    }

    // ── Statistika (activeCount HomeScreen/MainActivity dan keladi) ───────────

    /** HomeScreen dan har rekompositsiyada chaqiriladi */
    fun updateActiveCount(count: Int) {
        if (prevActiveCount == 0 && count > 0) onSessionStart()
        if (prevActiveCount > 0 && count == 0) stopStatsTracking()
        prevActiveCount = count
    }

    private fun onSessionStart() {
        resetDailyIfNeeded()
        prefs.todaySessions = prefs.todaySessions + 1
        updateStreak()
        startStatsTracking()
        syncStatsToState()
    }

    private fun startStatsTracking() {
        statsJob?.cancel()
        statsJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                prefs.todaySeconds = prefs.todaySeconds + 1
                _state.update { it.copy(todaySeconds = prefs.todaySeconds) }
            }
        }
    }

    private fun stopStatsTracking() {
        statsJob?.cancel()
    }

    private fun updateStreak() {
        val today = todayStr()
        val last  = prefs.lastUsedDate
        prefs.streakDays = when {
            last == today     -> prefs.streakDays               // bugun allaqachon
            last == yesterday() -> prefs.streakDays + 1         // kecha ishlatilgan
            else              -> 1                              // bo'shliq — qayta boshlash
        }
        prefs.lastUsedDate = today
    }

    private fun resetDailyIfNeeded() {
        val today = todayStr()
        if (prefs.todayDate != today) {
            prefs.todayDate    = today
            prefs.todaySeconds = 0
            prefs.todaySessions = 0
        }
    }

    private fun syncStatsToState() {
        _state.update {
            it.copy(
                todaySeconds  = prefs.todaySeconds,
                streakDays    = prefs.streakDays,
                todaySessions = prefs.todaySessions,
            )
        }
    }

    // ── Sana yordamchilari ────────────────────────────────────────────────────

    private fun todayStr(): String = sdf.format(Date())

    private fun yesterday(): String {
        val cal = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        return sdf.format(cal.time)
    }

    override fun onCleared() {
        timerJob?.cancel()
        statsJob?.cancel()
        super.onCleared()
    }
}
