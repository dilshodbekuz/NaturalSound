package com.naturalsound.service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.naturalsound.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import uz.apprica.naturalsound.R

@AndroidEntryPoint
class SoundPlayerService : Service() {

    companion object {
        const val CHANNEL_ID      = "naturalsound_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP_ALL  = "action_stop_all"
        const val ACTION_PAUSE_ALL = "action_pause_all"
        const val ACTION_RESUME_ALL = "action_resume_all"
    }

    inner class SoundBinder : Binder() {
        fun getService(): SoundPlayerService = this@SoundPlayerService
    }

    private val binder = SoundBinder()
    private val players = mutableMapOf<String, MediaPlayer>()
    private val volumes = mutableMapOf<String, Float>()
    private val soundIdToName = mutableMapOf<String, String>()
    var activeSoundNames = mutableListOf<String>()
        private set
    var onStateChanged: (() -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildIdleNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_ALL  -> stopAllSounds()
            ACTION_PAUSE_ALL -> pauseAllSounds()
            ACTION_RESUME_ALL -> resumeAllSounds()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    fun playSound(soundId: String, soundName: String, url: String, volume: Float = 1f) {
        if (players.containsKey(soundId)) {
            stopSound(soundId)
            return
        }
        if (url.isBlank()) {
            Log.e("SoundPlayer", "URL bo'sh: soundId=$soundId")
            return
        }
        Log.d("SoundPlayer", "Boshlanyapti: $soundName | $url")

        val player = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            // ── Assets (OGG) yoki HTTP URL ───────────────────────────────
            if (url.startsWith("sounds/")) {
                try {
                    val afd = this@SoundPlayerService.assets.openFd(url)
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    afd.close()
                } catch (e: Exception) {
                    Log.e("SoundPlayer", "Assets xato: $url — ${e.message}")
                    return@apply
                }
            } else {
                setDataSource(url)
            }
            isLooping = true
            setVolume(volume, volume)
            setOnPreparedListener {
                Log.d("SoundPlayer", "Tayyor, boshlandi: $soundName")
                it.start()
            }
            setOnErrorListener { _, what, extra ->
                Log.e("SoundPlayer", "Xato: what=$what extra=$extra | $soundName")
                players.remove(soundId)
                volumes.remove(soundId)
                onStateChanged?.invoke()
                true
            }
            prepareAsync()
        }
        players[soundId] = player
        volumes[soundId] = volume
        soundIdToName[soundId] = soundName
        if (!activeSoundNames.contains(soundName)) activeSoundNames.add(soundName)
        updateNotification()
        onStateChanged?.invoke()
    }

    fun stopSound(soundId: String) {
        players[soundId]?.apply { stop(); release() }
        players.remove(soundId)
        volumes.remove(soundId)
        soundIdToName.remove(soundId)?.let { activeSoundNames.remove(it) }
        onStateChanged?.invoke()
        if (players.isEmpty()) {
            activeSoundNames.clear()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        } else {
            updateNotification()
        }
    }

    fun setSoundVolume(soundId: String, volume: Float) {
        volumes[soundId] = volume
        players[soundId]?.setVolume(volume, volume)
    }

    fun isPlaying(soundId: String) = players[soundId]?.isPlaying == true
    fun getActiveSoundIds(): Set<String> = players.keys.toSet()
    fun getAllVolumes(): Map<String, Float> = volumes.toMap()

    fun stopAllSounds() {
        players.values.forEach { it.stop(); it.release() }
        players.clear()
        volumes.clear()
        soundIdToName.clear()
        activeSoundNames.clear()
        onStateChanged?.invoke()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    fun pauseAllSounds() {
        players.values.forEach { if (it.isPlaying) it.pause() }
        onStateChanged?.invoke()
        updateNotification()
    }

    fun resumeAllSounds() {
        players.values.forEach { if (!it.isPlaying) it.start() }
        onStateChanged?.invoke()
        updateNotification()
    }

    fun fadeOutAndStop(durationMs: Long = 3000L) {
        val steps     = 30
        val stepDelay = durationMs / steps
        var step      = 0
        val handler   = android.os.Handler(mainLooper)
        val runnable  = object : Runnable {
            override fun run() {
                if (step >= steps) { stopAllSounds(); return }
                val vol = 1f - (step.toFloat() / steps)
                players.values.forEach { it.setVolume(vol, vol) }
                step++
                handler.postDelayed(this, stepDelay)
            }
        }
        handler.post(runnable)
    }

    private fun buildIdleNotification(): Notification {
        val intent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_leaf)
            .setContentTitle("NaturalSound")
            .setContentText("Tayyor")
            .setContentIntent(intent)
            .setOngoing(false)
            .setSilent(true)
            .build()
    }

    private fun buildNotification(): Notification {
        val mainIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, SoundPlayerService::class.java).apply { action = ACTION_STOP_ALL },
            PendingIntent.FLAG_IMMUTABLE
        )
        val pauseIntent = PendingIntent.getService(
            this, 2,
            Intent(this, SoundPlayerService::class.java).apply { action = ACTION_PAUSE_ALL },
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_leaf)
            .setContentTitle("${players.size} ta ovoz ijroetda")
            .setContentText(activeSoundNames.take(3).joinToString(" · "))
            .setContentIntent(mainIntent)
            .addAction(R.drawable.ic_pause, "Pauza", pauseIntent)
            .addAction(R.drawable.ic_stop, "To'xtat", stopIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification() {
        if (players.isEmpty()) return
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "NaturalSound ijro", NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Fon rejimida ovoz ijrosi" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        stopAllSounds()
        super.onDestroy()
    }
}