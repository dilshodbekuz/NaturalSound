package com.example.naturalsound.main

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturalsound.R
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
    var timerJob: Job? = null


    val sounds = listOf(
        SoundModel(1, "Rain", R.raw.rain, R.drawable.rain),
        SoundModel(2, "Fire", R.raw.fire, R.drawable.fire),
        SoundModel(3, "Typing", R.raw.typing, R.drawable.typing),
        SoundModel(4, "Birds", R.raw.birds, R.drawable.birds),
        SoundModel(5, "Ocean", R.raw.ocean, R.drawable.ocean),
        SoundModel(6, "Water Full", R.raw.water_full, R.drawable.waterfall),
        SoundModel(7, "Drip", R.raw.drip, R.drawable.drip),
        SoundModel(8, "Storm", R.raw.storm, R.drawable.storm),
        SoundModel(9, "Wind", R.raw.wind, R.drawable.wind)
    )

    fun playSound(context: Context, item: SoundModel?) {
        val selectList = mutableListOf<SoundModel?>()
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

    fun stopSound(item: SoundModel?) {
        val selectList = mutableListOf<SoundModel?>()
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
            var stepTime = timer * 60
            while (stepTime >= 1) {
                stepTime--
                _uiState.update { it.copy(counter = stepTime) }
                delay(1000)
            }
            if (stepTime.toInt() < 1) {
                resetSound()
            }
        }
    }
}

data class UiState(
    val selectList: MutableList<SoundModel?> = mutableListOf(),
    val counter: Int? = null
)