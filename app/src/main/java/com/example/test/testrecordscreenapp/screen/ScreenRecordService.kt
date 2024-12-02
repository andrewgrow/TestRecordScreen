package com.example.test.testrecordscreenapp.screen

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjection
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat


class ScreenRecordService: Service() {
    private var screenRecorder: ScreenRecorder? = null

    companion object {
        private var isRecording = false
        fun isRecording(): Boolean = isRecording

        private var instance: ScreenRecordService? = null

        private var savedMetrics: DisplayMetrics? = null
        private var savedOutputPath: String? = null

        private const val CHANNEL_ID = "ScreenRecordChannel"
        private const val NOTIFICATION_ID = 1

        fun startService(context: Context, metrics: DisplayMetrics, outputPath: String) {
            savedMetrics = metrics
            savedOutputPath = outputPath
            val intent = Intent(context, ScreenRecordService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, ScreenRecordService::class.java)
            context.stopService(intent)
        }

        fun provideMediaProjection(mediaProjection: MediaProjection) {
            ScreenRecordManager.mediaProjection = mediaProjection

            val metrics = savedMetrics
            val outputPath = savedOutputPath
            if (metrics != null && outputPath != null) {
                instance?.startRecording(metrics, outputPath)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        instance = this
        startForegroundServiceNotification()
        return START_STICKY
    }

    fun startRecording(metrics: DisplayMetrics, outputPath: String) {
        if (isRecording) return

        val mediaProjection = ScreenRecordManager.mediaProjection ?: return
        screenRecorder = ScreenRecorder(mediaProjection, metrics, outputPath)
        screenRecorder?.startRecording()
        isRecording = true
    }

    private fun stopRecording() {
        screenRecorder?.stopRecording()
        screenRecorder = null
        isRecording = false
    }

    override fun onDestroy() {
        stopRecording()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startForegroundServiceNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Waiting for Recording")
            .setContentText("Tap Record to start screen recording.")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Screen Record",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}