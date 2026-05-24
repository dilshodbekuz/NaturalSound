package com.naturalsound.ui.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class TimerUiState(
    val sleepTimerOn:    Boolean = false,
    val remainingMs:     Long    = 30 * 60 * 1000L,
    val selectedMinutes: Int     = 30,
    val alarmEnabled:    Boolean = false,
    val alarmHour:       Int     = 7,
    val alarmMinute:     Int     = 0,
    val fadeOutSeconds:  Int     = 60,
    val todayMinutes:    Int     = 252,
    val streakDays:      Int     = 12,
    val offlineCount:    Int     = 7,
    val favoriteCount:   Int     = 14
)

@HiltViewModel
class TimerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _state.asStateFlow()

    private var timerJob: Job? = null

    fun setTimer(minutes: Int) {
        _state.update { it.copy(selectedMinutes = minutes, remainingMs = minutes * 60_000L) }
        if (_state.value.sleepTimerOn) startCountdown()
    }

    fun toggleSleepTimer() {
        val nowOn = !_state.value.sleepTimerOn
        _state.update { it.copy(sleepTimerOn = nowOn) }
        if (nowOn) startCountdown() else stopCountdown()
    }

    fun addMinutes(minutes: Int) {
        _state.update { it.copy(remainingMs = it.remainingMs + minutes * 60_000L) }
    }

    fun setFadeOut(seconds: Int) {
        _state.update { it.copy(fadeOutSeconds = seconds) }
    }

    fun toggleAlarm() {
        val enabled = !_state.value.alarmEnabled
        _state.update { it.copy(alarmEnabled = enabled) }
        if (enabled) scheduleAlarm()
        else cancelAlarm()
    }

    fun setAlarmTime(hour: Int, minute: Int) {
        _state.update { it.copy(alarmHour = hour, alarmMinute = minute) }
        if (_state.value.alarmEnabled) scheduleAlarm()
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
                // Service ga stop signal → MainActivity orqali
            }
        }
    }

    private fun stopCountdown() {
        timerJob?.cancel()
        _state.update { it.copy(remainingMs = _state.value.selectedMinutes * 60_000L) }
    }

    private fun scheduleAlarm() {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent   = Intent(context, AlarmReceiver::class.java)
        val pending  = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, _state.value.alarmHour)
            set(java.util.Calendar.MINUTE, _state.value.alarmMinute)
            set(java.util.Calendar.SECOND, 0)
            if (before(java.util.Calendar.getInstance())) add(java.util.Calendar.DATE, 1)
        }
        alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pending)
    }

    private fun cancelAlarm() {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent   = Intent(context, AlarmReceiver::class.java)
        val pending  = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmMgr.cancel(pending)
    }

    override fun onCleared() { timerJob?.cancel(); super.onCleared() }
}
