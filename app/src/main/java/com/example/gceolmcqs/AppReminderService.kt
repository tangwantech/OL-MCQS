package com.example.gceolmcqs

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class AppReminderService : Service() {

    private val handler = Handler()
    private val checkInterval = 3 * 60 * 1000L // Check every 6 hours
    private val reminderThreshold = 3 * 60 * 1000L // 24 hours
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences(MCQConstants.APP_USAGE_PREFS, Context.MODE_PRIVATE)
        startForegroundService()
        checkAppUsage()
        Toast.makeText(this, "App reminder service started", Toast.LENGTH_LONG).show()
    }

    private fun checkAppUsage() {
        handler.postDelayed({
            val lastUsedTime = sharedPreferences.getLong(MCQConstants.LAST_USED, 0)
            val currentTime = System.currentTimeMillis()

            if (lastUsedTime != 0L && (currentTime - lastUsedTime) >= reminderThreshold) {
                sendNotification()
            }

            checkAppUsage() // Schedule next check
        }, checkInterval)
    }

    private fun sendNotification() {
        val channelId = "gceolmcqs_reminder_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "GCE OL MCQS Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val launchIntent = packageManager.getLaunchIntentForPackage(MCQConstants.APP_PACKAGE_NAME)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.app_usage_reminder))
            .setContentText(getString(R.string.app_usage_reminder_message))
            .setSmallIcon(R.drawable.app_logo)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun startForegroundService() {
        val channelId = "appA_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "AppA Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.app_usage_reminder))
            .setContentText(getString(R.string.app_usage_reminder_message))
            .setSmallIcon(R.drawable.app_logo)
            .build()

        startForeground(2, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}


