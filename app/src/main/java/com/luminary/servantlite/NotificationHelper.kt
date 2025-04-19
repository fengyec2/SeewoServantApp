package com.luminary.servantlite

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    private val ALERT_CHANNEL_ID = "urgent_notification_channel"
    private val ALERT_CHANNEL_NAME = "紧急通知"

    private val HEARTBEAT_CHANNEL_ID = "heartbeat_notification_channel"
    private val HEARTBEAT_CHANNEL_NAME = "心跳通知"

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // ALERT 通知渠道 - 高优先级
            val alertChannel = NotificationChannel(
                ALERT_CHANNEL_ID,
                ALERT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "收到来自PC端的紧急通知"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 500, 500)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(alertChannel)

            // HEARTBEAT 通知渠道 - 低优先级
            val heartbeatChannel = NotificationChannel(
                HEARTBEAT_CHANNEL_ID,
                HEARTBEAT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "收到服务器心跳消息"
                enableLights(false)
                enableVibration(false)
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            notificationManager.createNotificationChannel(heartbeatChannel)
        }
    }

    /**
     * 根据消息类型发送不同优先级的通知
     * @param title 通知标题
     * @param content 通知内容
     */
    fun showNotification(title: String, content: String) {
        val (channelId, smallIcon, priority, category) = when {
            content.contains("[ALERT]") -> {
                // ALERT消息
                Quadruple(
                    ALERT_CHANNEL_ID,
                    android.R.drawable.ic_dialog_alert,
                    NotificationCompat.PRIORITY_HIGH,
                    NotificationCompat.CATEGORY_ALARM
                )
            }
            content.contains("[HEARTBEAT]") -> {
                // HEARTBEAT消息
                Quadruple(
                    HEARTBEAT_CHANNEL_ID,
                    android.R.drawable.ic_dialog_info,
                    NotificationCompat.PRIORITY_LOW,
                    NotificationCompat.CATEGORY_SERVICE
                )
            }
            else -> {
                // 默认渠道，使用 ALERT 通道作为兜底
                Quadruple(
                    ALERT_CHANNEL_ID,
                    android.R.drawable.ic_dialog_info,
                    NotificationCompat.PRIORITY_DEFAULT,
                    NotificationCompat.CATEGORY_MESSAGE
                )
            }
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(priority)
            .setCategory(category)
            .setAutoCancel(true)
            .apply {
                if (channelId == ALERT_CHANNEL_ID) {
                    setDefaults(NotificationCompat.DEFAULT_ALL)
                }
            }
            .build()

        // 这里使用不同的通知ID避免覆盖
        val notificationId = if (channelId == ALERT_CHANNEL_ID) 1001 else 1002

        notificationManager.notify(notificationId, notification)
    }

    // Kotlin 没有内置 Quadruple，自己定义一个数据类简化代码
    private data class Quadruple<A, B, C, D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )
}