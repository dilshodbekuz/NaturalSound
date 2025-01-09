package com.example.naturalsound.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.naturalsound.MainActivity
import com.example.naturalsound.R

class MyForegroundService : Service(),LifecycleObserver {

    private val SERVICE_ID = 1
    private val CHANNEL_ID = "my_foreground_service_channel"

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Perform background work here if needed
        return START_STICKY // Ensures the service is restarted if it is killed by the system
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setShowWhen(true)
            .setAutoCancel(true)
            .setContentTitle("Sounds is running...")
            .setContentText("The service is running in the background.")
            .setSmallIcon(R.drawable.ic_music_)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    2,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()
        startForeground(SERVICE_ID, notification)
    }
}