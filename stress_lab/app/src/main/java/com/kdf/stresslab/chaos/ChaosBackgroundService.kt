package com.kdf.stresslab.chaos

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

/**
 * Servicio en Primer Plano (Foreground Service).
 * Mantiene los motores de estrés (CPU, RAM, I/O) activos en segundo plano
 * mientras el usuario o el script automatizado navega en la app que se desea probar.
 */
class ChaosBackgroundService : Service() {

    private val CHANNEL_ID = "kdf_chaos_service_channel"
    private val NOTIFICATION_ID = 4251
    private val cpuEngine = CpuStressEngine()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("⚡ KDF StressLab Activo")
            .setContentText("Saturación de recursos y caos en ejecución...")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: "START"
        if (action == "STOP") {
            cpuEngine.stop()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        } else {
            cpuEngine.startCpuStress()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        cpuEngine.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "KDF StressLab Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
