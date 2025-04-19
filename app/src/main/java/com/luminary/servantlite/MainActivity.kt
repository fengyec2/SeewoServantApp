package com.luminary.servantlite

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvMessage: TextView
    private lateinit var btnConnect: Button
    private lateinit var btnDisconnect: Button

    private lateinit var notificationHelper: NotificationHelper
    private var webSocketClient: WebSocketClient? = null

    // 替换成你PC的局域网IP和端口
    private val serverUrl = "ws://192.168.137.1:8765"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        tvMessage = findViewById(R.id.tvMessage)
        btnConnect = findViewById(R.id.btnConnect)
        btnDisconnect = findViewById(R.id.btnDisconnect)

        notificationHelper = NotificationHelper(this)

        btnConnect.setOnClickListener {
            startWebSocket()
            btnConnect.isEnabled = false
            btnDisconnect.isEnabled = true
            tvStatus.text = "连接中..."
        }

        btnDisconnect.setOnClickListener {
            stopWebSocket()
            btnConnect.isEnabled = true
            btnDisconnect.isEnabled = false
            tvStatus.text = "已断开"
        }
    }

    private fun startWebSocket() {
        webSocketClient = WebSocketClient(
            serverUrl,
            onMessageReceived = { message ->
                runOnUiThread {
                    tvMessage.text = message
                    notificationHelper.showNotification("紧急通知", message)
                }
            },
            onStatusChanged = { status ->
                runOnUiThread {
                    tvStatus.text = status
                }
            }
        )
        webSocketClient?.connect()
    }

    private fun stopWebSocket() {
        webSocketClient?.disconnect()
        webSocketClient = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopWebSocket()
    }
}