package com.naturalsound.ui.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.naturalsound.service.SoundPlayerService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Alarm vaqti kelganda Service ni ishga tushir
        val serviceIntent = Intent(context, SoundPlayerService::class.java)
        context.startForegroundService(serviceIntent)
    }
}
