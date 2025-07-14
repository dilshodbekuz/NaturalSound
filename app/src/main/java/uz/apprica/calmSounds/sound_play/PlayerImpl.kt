package uz.apprica.calmSounds.sound_play

import android.media.MediaPlayer
import android.util.Log

class PlayerImpl : Player {
    private val mediaPlayers: MutableList<MediaPlayer> = mutableListOf()


    override fun playSound(mediaPlayer: MediaPlayer) {
        mediaPlayers.add(mediaPlayer)
    }

    override fun stopSound(mediaPlayer: MediaPlayer) {
        Log.d("aaa","mediaPlayer ${mediaPlayer.audioSessionId}")
        mediaPlayer.stop()
        mediaPlayer.release()
        mediaPlayers.remove(mediaPlayer)
    }

    override fun resetSound() {
        mediaPlayers.forEach { mediaPlayer ->
            mediaPlayer.reset()
            mediaPlayer.stop() // Ovozlarni to'xtatish
            mediaPlayer.release() // MediaPlayerni ozod qilish
        }
        mediaPlayers.clear()
    }
}