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
        SoundModel(1, R.string.typing, R.raw.typing, R.drawable.typing),
        SoundModel(2, R.string.rain, R.raw.rain, R.drawable.rain),
        SoundModel(3, R.string.calm_rain, R.raw.calm_rain, R.drawable.calm_rain),
        SoundModel(4, R.string.storm_rain, R.raw.storm_and_rain, R.drawable.storm_rain),
        SoundModel(5, R.string.water_full, R.raw.water_full, R.drawable.waterfall),
        SoundModel(6, R.string.calm_river, R.raw.calm_river, R.drawable.calm_river),
        SoundModel(
            7,
            R.string.mountain_river,
            R.raw.calm_mountain_river,
            R.drawable.mountain_river
        ),
        SoundModel(8, R.string.ocaen, R.raw.ocean, R.drawable.ocean),
        SoundModel(9, R.string.drip, R.raw.drip, R.drawable.drip),
        SoundModel(10, R.string.fire, R.raw.fire, R.drawable.fire),
        SoundModel(11, R.string.storm, R.raw.storm, R.drawable.storm),
        SoundModel(12, R.string.thunder, R.raw.long_thunder, R.drawable.long_thunder),
        SoundModel(
            13,
            R.string.peals_of_thunder,
            R.raw.peals_of_thunder,
            R.drawable.peals_of_thunder
        ),
        SoundModel(14, R.string.rain_thunder, R.raw.rain_mix_thunder, R.drawable.rain_and_thunder),
        SoundModel(
            15,
            R.string.rain_thunder_wind,
            R.raw.thunder_rain_wind_mixed,
            R.drawable.rain_thunder_wind_mixed
        ),
        SoundModel(16, R.string.wind, R.raw.wind, R.drawable.normal_wind),
        SoundModel(17, R.string.strong_wind, R.raw.strong_wild_wind, R.drawable.strong_wind),
        SoundModel(18, R.string.calm_forest, R.raw.calm_forest, R.drawable.calm_forest),
        SoundModel(19, R.string.jungle, R.raw.jungle, R.drawable.jungle),
        SoundModel(20, R.string.orphean, R.raw.eastern_orphean_bird, R.drawable.eastern_orphean),
        SoundModel(
            21,
            R.string.erithacus_rebecula_bird,
            R.raw.erithacus_rebecula_bird,
            R.drawable.robin_bird
        ),
        SoundModel(22, R.string.bird, R.raw.bird_pure_sound, R.drawable.pure_bird),
        SoundModel(
            23,
            R.string.greater_hoopoe,
            R.raw.greater_hoopoe_bird,
            R.drawable.greater_hoopoe
        ),
        SoundModel(
            24,
            R.string.hermit_thrush,
            R.raw.hermit_thrush_bird,
            R.drawable.hermit_thrush_bird
        ),
        SoundModel(
            25,
            R.string.indian_malabar,
            R.raw.indian_malabar_bird,
            R.drawable.indian_malabar_bird
        ),
        SoundModel(26, R.string.birds_frogs, R.raw.birds_vs_frog, R.drawable.frog_and_bords),
        SoundModel(
            27,
            R.string.olive_whistler,
            R.raw.olive_whistler_bird,
            R.drawable.olive_whistler
        ),
        SoundModel(
            28,
            R.string.pied_butcherbird,
            R.raw.pied_butcherbird,
            R.drawable.pied_butcherbird
        ),
        SoundModel(29, R.string.woodlark, R.raw.woodlark_bird, R.drawable.woodlark_bird),
        SoundModel(30, R.string.birds, R.raw.birds, R.drawable.birds),
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
            val mediaPlayer = MediaPlayer.create(context, item.sound).apply {
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
        _uiState.update {
            it.copy(
                selectList = persistentListOf(),
                counter = 0,
                progress = 1f,
                sheetContentTypes = BottomSheetContentType.Times
            )
        }
    }

    fun setTimer(timer: Int, isResound: Boolean = true) {
        _uiState.update {
            it.copy(
                progress = 1f
            )
        }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            if (uiState.value.counter == 0) {
                _uiState.update { it.copy(counter = timer) }
            }
            var stepTime = timer * 60
            while (stepTime >= 1) {
                val minute = stepTime / 60
                val seconds = stepTime % 60
                stepTime--
                _uiState.update { it.copy(counter = stepTime, minAndSec = "$minute : $seconds") }
                delay(1000)
                _uiState.update {
                    it.copy(
                        progress = stepTime.toFloat() / (timer * 60).toFloat(),
                    )
                }
            }
            if (stepTime.toInt() < 1 && isResound) {
                resetSound()
            }
        }
    }

    fun onMenuVisibilityChange(value: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(menuVisibility = value) }
        }
    }

    fun onClickButton(item: SoundModel, context: Context) {
        if (uiState.value.selectList.contains(item)) {
            stopSound(item)
        } else playSound(context, item)
    }

    fun setSheetContent(sheetContentType: BottomSheetContentType) {
        viewModelScope.launch {
            _uiState.update { it.copy(minAndSec = "", sheetContentTypes = sheetContentType) }
        }
    }
}

@Immutable
data class UiState(
    val sounds: ImmutableList<SoundModel> = persistentListOf(),
    val selectList: ImmutableList<SoundModel> = persistentListOf(),
    val counter: Int = 0,
    val minAndSec: String = "",
    val progress: Float = 1f,
    val sheetContentTypes: BottomSheetContentType = BottomSheetContentType.Times,
    val menuVisibility: Boolean = false
)

enum class BottomSheetContentType {
    Times, Progress
}