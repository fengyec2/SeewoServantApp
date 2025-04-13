package com.luminary.servantlite

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket

class UdpListenerService : Service() {
    private val binder = LocalBinder()
    private var socket: DatagramSocket? = null
    private var isListening = false
    private lateinit var prefs: PrefsHelper

    inner class LocalBinder : Binder() {
        fun getService(): UdpListenerService = this@UdpListenerService
    }

    override fun onCreate() {
        super.onCreate()
        prefs = PrefsHelper(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        startListening()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "udp_channel",
                "UDP监听服务",
                NotificationManager.IMPORTANCE_LOW
            )
            getNotificationManager().createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "udp_channel")
            .setContentTitle("正在监听UDP消息")
            .setContentText("端口: ${prefs.listenPort}")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    fun startListening() {
        if (isListening) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val port = prefs.listenPort
                socket = DatagramSocket(port)
                val buffer = ByteArray(1024)

                isListening = true
                while (isListening) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket?.receive(packet)
                    val message = String(packet.data, 0, packet.length)

                    if (message.contains("ALERT:")) {
                        showNotification(message)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showNotification(message: String) {
        val notification = NotificationCompat.Builder(this, "udp_channel")
            .setContentTitle("收到新消息")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setAutoCancel(true)
            .build()

        getNotificationManager().notify(2, notification)
    }

    private fun getNotificationManager(): NotificationManager {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSystemService(NotificationManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        isListening = false
        socket?.close()
        super.onDestroy()
    }
}