package com.example.naturalsound

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturalsound.sound_play.Player
import com.example.naturalsound.sound_play.PlayerImpl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val player: Player by lazy { PlayerImpl() }
    private var mediaPlayerList = HashMap<Int, MediaPlayer>()

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()
    private var timerJob: Job? = null


    val sounds = listOf(
        SoundState(1, "Rain", R.raw.rain),
        SoundState(2, "Fire", R.raw.fire),
        SoundState(3, "Typing", R.raw.typing),
        SoundState(4, "Birds", R.raw.birds),
        SoundState(5, "Ocean", R.raw.ocean),
        SoundState(6, "Water Full", R.raw.water_full),
        SoundState(7, "Drip", R.raw.drip),
        SoundState(8, "Storm", R.raw.storm)
    )

    fun playSound(context: Context, item: SoundState?) {
        val selectList = mutableListOf<SoundState?>()
        selectList.addAll(uiState.value.selectList)
        selectList.add(item)
        _uiState.update { it.copy(selectList = selectList) }
        val mediaPlayer = MediaPlayer.create(context, item?.sound ?: 0).apply {
            isLooping = true
            start()
        }
        player.playSound(mediaPlayer = mediaPlayer)
        mediaPlayerList[item!!.id!!] = mediaPlayer
    }

    fun stopSound(item: SoundState?) {
        val selectList = mutableListOf<SoundState?>()
        selectList.addAll(uiState.value.selectList)
        selectList.remove(item)
        _uiState.update { it.copy(selectList = selectList) }
        val mediaPlayer = mediaPlayerList[item?.id!!]
        mediaPlayer?.stop()
        player.stopSound(mediaPlayer!!)
    }

    fun resetSound() {
        timerJob?.cancel()
        timerJob = null
        player.resetSound()
        _uiState.update { it.copy(selectList = mutableListOf(), counter = 0) }
    }

    fun setTimer(timer: Int) {
        timerJob = viewModelScope.launch {
            if (uiState.value.counter == null) {
                _uiState.update { it.copy(counter = timer) }
            }
            var stepTime = timer
            while (stepTime >= 1) {
                delay(1000)
                stepTime--
                _uiState.update { it.copy(counter = stepTime) }
            }
            if (stepTime < 1) {
                resetSound()
            }
        }
    }
}

data class UiState(
    val selectList: MutableList<SoundState?> = mutableListOf(),
    val counter: Int? = null
)