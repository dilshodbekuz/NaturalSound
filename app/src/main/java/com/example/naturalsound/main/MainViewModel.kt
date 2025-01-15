package com.example.naturalsound.main

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturalsound.R
import com.example.naturalsound.sound_play.Player
import com.example.naturalsound.sound_play.PlayerImpl
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
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


    private val sounds = persistentListOf(
        SoundModel(1, "Rain", R.raw.rain, R.drawable.rain),
        SoundModel(2, "Fire", R.raw.fire, R.drawable.fire),
        SoundModel(3, "Typing", R.raw.typing, R.drawable.typing),
        SoundModel(4, "Birds", R.raw.birds, R.drawable.birds),
        SoundModel(5, "Ocean", R.raw.ocean, R.drawable.ocean),
        SoundModel(6, "Water Full", R.raw.water_full, R.drawable.waterfall),
        SoundModel(7, "Drip", R.raw.drip, R.drawable.drip),
        SoundModel(8, "Storm", R.raw.storm, R.drawable.storm),
        SoundModel(9, "Wind", R.raw.wind, R.drawable.strong_wind),
        SoundModel(10, "Bird", R.raw.bird_pure_sound, R.drawable.pure_bird),
        SoundModel(11, "Birds and Frogs", R.raw.birds_vs_frog, R.drawable.frog_and_bords),
        SoundModel(12, "Calm Rain", R.raw.calm_rain, R.drawable.calm_rain),
        SoundModel(13, "Calm River", R.raw.calm_river, R.drawable.calm_river),
        SoundModel(14, "Storm and Rain", R.raw.storm_and_rain, R.drawable.storm_rain),
        SoundModel(15, "Strong Wind", R.raw.strong_wild_wind, R.drawable.strong_wind),
        SoundModel(16, "Mountain River", R.raw.calm_mountain_river, R.drawable.mountain_river),
        SoundModel(17, "Calm Forest", R.raw.calm_forest, R.drawable.calm_forest),
        SoundModel(18, "Jungle", R.raw.jungle, R.drawable.jungle),
    )

    init {
        _uiState.update { it.copy(sounds = sounds) }
    }

    private fun playSound(context: Context, item: SoundModel) {
        viewModelScope.launch {
            val selectList = mutableListOf<SoundModel>()
            selectList.addAll(uiState.value.selectList)
            selectList.add(item)
            _uiState.update { it.copy(selectList = selectList.toImmutableList()) }
            val mediaPlayer = MediaPlayer.create(context, item.sound ).apply {
                isLooping = true
                start()
            }
            player.playSound(mediaPlayer = mediaPlayer)
            mediaPlayerList[item.id] = mediaPlayer
        }
    }

    private fun stopSound(item: SoundModel) {
        viewModelScope.launch {
            val selectList = mutableListOf<SoundModel>()
            selectList.addAll(uiState.value.selectList)
            selectList.remove(item)
            _uiState.update { it.copy(selectList = selectList.toImmutableList()) }
            val mediaPlayer = mediaPlayerList[item.id]
            mediaPlayer?.stop()
            player.stopSound(mediaPlayer!!)
        }
    }

    fun resetSound() {
        timerJob?.cancel()
        timerJob = null
        player.resetSound()
        _uiState.update { it.copy(selectList = persistentListOf(), counter = 0) }
    }

    fun setTimer(timer: Int) {
        timerJob?.cancel()
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

    fun onClickButton(item: SoundModel, context: Context) {
        if (uiState.value.selectList.contains(item)) {
            stopSound(item)
        } else playSound(context, item)
    }
}

@Immutable
data class UiState(
    val sounds: ImmutableList<SoundModel> = persistentListOf(),
    val selectList: ImmutableList<SoundModel> = persistentListOf(),
    val counter: Int? = null
)