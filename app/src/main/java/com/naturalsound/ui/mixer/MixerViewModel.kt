package com.naturalsound.ui.mixer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naturalsound.domain.model.Sound
import com.naturalsound.domain.repository.SoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Scene (saqlangan aralashma) ───────────────────────────────────────────────
data class SoundScene(
    val id: String,
    val name: String,
    val emoji: String,
    val volumes: Map<String, Float>   // soundId → volume
)

val defaultScenes = listOf(
    SoundScene("sleep", "Uyqu", "😴", emptyMap()),
    SoundScene("focus", "Diqqat", "🎯", emptyMap()),
    SoundScene("meditate", "Meditatsiya", "🧘", emptyMap()),
    SoundScene("read", "O'qish", "📖", emptyMap())
)

// ── EQ sozlamasi ──────────────────────────────────────────────────────────────
data class EqSettings(val bass: Float = 0f, val treble: Float = 0f)   // -5f..+5f

// ── Mixer UI state ────────────────────────────────────────────────────────────
data class MixerUiState(
    val activeSounds: List<Sound> = emptyList(),
    val volumes: Map<String, Float> = emptyMap(),          // soundId → 0f..1f
    val eqSettings: Map<String, EqSettings> = emptyMap(), // soundId → EQ
    val scenes: List<SoundScene> = defaultScenes,
    val selectedScene: String? = "sleep",
    val isAllPlaying: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MixerViewModel @Inject constructor(
    private val soundRepository: SoundRepository
) : ViewModel() {

    private val _activeSoundIds = MutableStateFlow<Set<String>>(emptySet())
    private val _volumes = MutableStateFlow<Map<String, Float>>(emptyMap())
    private val _eqSettings = MutableStateFlow<Map<String, EqSettings>>(emptyMap())
    private val _scenes = MutableStateFlow(defaultScenes)
    private val _selectedScene = MutableStateFlow<String?>("sleep")
    private val _isAllPlaying = MutableStateFlow(true)

    private val _activeSoundsFlow = _activeSoundIds.flatMapLatest { ids ->
        if (ids.isEmpty()) flowOf(emptyList())
        else soundRepository.getAllSounds().map { all -> all.filter { it.id in ids } }
    }

    val uiState: StateFlow<MixerUiState> = combine(
        combine(_activeSoundsFlow, _volumes, _eqSettings) { sounds, vols, eq ->
            Triple(sounds, vols, eq)
        },
        _scenes,
        _selectedScene,
        _isAllPlaying
    ) { (sounds, vols, eq), scenes, scene, playing ->
        MixerUiState(
            activeSounds = sounds,
            volumes = vols,
            eqSettings = eq,
            scenes = scenes,
            selectedScene = scene,
            isAllPlaying = playing
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MixerUiState())

    // MainActivity dan keladi
    fun syncActiveSounds(ids: Set<String>, currentVolumes: Map<String, Float>) {
        _activeSoundIds.value = ids
        // Mavjud volumelarni saqlab, yangilarini default 1f bilan qo'shamiz
        val merged = currentVolumes.toMutableMap()
        ids.forEach { id -> merged.getOrPut(id) { 1f } }
        _volumes.value = merged
    }

    fun setVolume(soundId: String, volume: Float) {
        _volumes.update { it + (soundId to volume) }
    }

    fun setEq(soundId: String, bass: Float? = null, treble: Float? = null) {
        _eqSettings.update { map ->
            val current = map[soundId] ?: EqSettings()
            map + (soundId to current.copy(
                bass = bass ?: current.bass,
                treble = treble ?: current.treble
            ))
        }
    }

    fun selectScene(sceneId: String) {
        _selectedScene.value = sceneId
        val scene = _scenes.value.find { it.id == sceneId } ?: return
        if (scene.volumes.isNotEmpty()) _volumes.value = scene.volumes.toMutableMap()
    }

    fun saveCurrentAsScene(name: String, emoji: String) {
        val newScene = SoundScene(
            id = System.currentTimeMillis().toString(),
            name = name,
            emoji = emoji,
            volumes = _volumes.value.toMap()
        )
        _scenes.update { it + newScene }
        _selectedScene.value = newScene.id
    }

    fun toggleAllPlaying() {
        _isAllPlaying.update { !it }
    }

    fun setAllPlaying(playing: Boolean) {
        _isAllPlaying.value = playing
    }
}
