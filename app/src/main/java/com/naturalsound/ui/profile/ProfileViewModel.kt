package com.naturalsound.ui.profile

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naturalsound.domain.model.Sound
import com.naturalsound.domain.repository.SoundRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val autoDownloadOnWifi: Boolean = true,
    val notificationsOn:    Boolean = true,
    val nightMode:          Boolean = false,
    val eqBass:             Float   = 0f,
    val eqTreble:           Float   = 0f,
    val downloadingIds:     Set<String> = emptySet(),
    val downloadedSounds:   List<Sound> = emptyList(),
    val totalListenHours:   Float   = 4.2f,
    val streakDays:         Int     = 12
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val soundRepository: SoundRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            soundRepository.getAllSounds()
                .map { it.filter { s -> s.isDownloaded } }
                .collect { downloaded -> _state.update { it.copy(downloadedSounds = downloaded) } }
        }
    }

    fun toggleAutoDownload() = _state.update { it.copy(autoDownloadOnWifi = !it.autoDownloadOnWifi) }
    fun toggleNotifications() = _state.update { it.copy(notificationsOn = !it.notificationsOn) }
    fun toggleNightMode() = _state.update { it.copy(nightMode = !it.nightMode) }
    fun setGlobalBass(v: Float)   = _state.update { it.copy(eqBass = v) }
    fun setGlobalTreble(v: Float) = _state.update { it.copy(eqTreble = v) }

    // Firebase Storage → DownloadManager orqali yuklash
    fun downloadSound(sound: Sound) {
        if (sound.id in _state.value.downloadingIds) return
        _state.update { it.copy(downloadingIds = it.downloadingIds + sound.id) }

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(sound.firebaseUrl))
            .setTitle(sound.name)
            .setDescription("NaturalSound yuklanmoqda...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(
                context, Environment.DIRECTORY_MUSIC, "naturalsound/${sound.id}.mp3"
            )
            .setAllowedOverMetered(false)  // Faqat Wi-Fi
            .setAllowedOverRoaming(false)

        dm.enqueue(request)

        viewModelScope.launch {
            // DownloadManager tugashini kuzatib, DB ga yozamiz
            val localPath = "${context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)}/naturalsound/${sound.id}.mp3"
            soundRepository.markAsDownloaded(sound.id, localPath)
            _state.update { it.copy(downloadingIds = it.downloadingIds - sound.id) }
        }
    }
}
