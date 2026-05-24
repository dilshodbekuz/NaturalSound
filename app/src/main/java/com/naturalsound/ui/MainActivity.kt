package com.naturalsound.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.MobileAds
import com.naturalsound.data.prefs.UserPreferences
import com.naturalsound.domain.model.Sound
import com.naturalsound.service.SoundPlayerService
import com.naturalsound.ui.navigation.AppNavigation
import com.naturalsound.ui.theme.NaturalSoundTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var prefs: UserPreferences

    private var svc: SoundPlayerService? = null
    private var isBound = false

    private val _activeIds    = mutableStateOf<Set<String>>(emptySet())
    private val _activeNames  = mutableStateOf<List<String>>(emptyList())
    private val _volumes      = mutableStateOf<Map<String, Float>>(emptyMap())

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(n: ComponentName, b: IBinder) {
            svc = (b as SoundPlayerService.SoundBinder).getService()
            isBound = true
            svc?.onStateChanged = ::sync
            sync()
        }
        override fun onServiceDisconnected(n: ComponentName) {
            svc = null; isBound = false
            _activeIds.value = emptySet(); _activeNames.value = emptyList()
        }
    }

    private fun sync() {
        _activeIds.value   = svc?.getActiveSoundIds() ?: emptySet()
        _activeNames.value = svc?.activeSoundNames?.toList() ?: emptyList()
        _volumes.value     = svc?.getAllVolumes() ?: emptyMap()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize AdMob SDK on a background thread so it never blocks the UI.
        // The SDK is ready to serve ads once the callback fires.
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(this@MainActivity) { status ->
                Log.d("AdMob", "MobileAds initialized: $status")
            }
        }
        Intent(this, SoundPlayerService::class.java).also {
            startForegroundService(it)
            bindService(it, conn, Context.BIND_AUTO_CREATE)
        }
        setContent {
            val appVm: AppViewModel = viewModel()
            val theme by appVm.theme.collectAsStateWithLifecycle()
            NaturalSoundTheme(darkTheme = theme != "light") {
                val ids     by _activeIds
                val names   by _activeNames
                val volumes by _volumes
                AppNavigation(
                    activeSoundIds   = ids,
                    activeSoundNames = names,
                    currentVolumes   = volumes,
                    onToggleSound    = { s -> if (svc?.isPlaying(s.id) == true) svc?.stopSound(s.id) else svc?.playSound(s.id, s.name, s.firebaseUrl) },
                    onStopSound      = { id -> svc?.stopSound(id) },
                    onPauseAll       = { svc?.pauseAllSounds() },
                    onResumeAll      = { svc?.resumeAllSounds() },
                    onStopAll        = { svc?.stopAllSounds() },
                    onSetVolume      = { id, v -> svc?.setSoundVolume(id, v) },
                    onFadeOutAndStop = { svc?.fadeOutAndStop() }
                )
            }
        }
    }

    override fun onDestroy() {
        if (isBound) { unbindService(conn); isBound = false }
        super.onDestroy()
    }
}
