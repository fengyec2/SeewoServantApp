package com.luminary.servantlite

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etServerIp: EditText
    private lateinit var etServerPort: EditText
    private lateinit var btnSaveConnect: Button
    private lateinit var btnDisconnect: Button
    private lateinit var tvStatus: TextView
    private lateinit var tvMessage: TextView

    private lateinit var notificationHelper: NotificationHelper
    private var webSocketClient: WebSocketClient? = null

    private var serverUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etServerIp = findViewById(R.id.etServerIp)
        etServerPort = findViewById(R.id.etServerPort)
        btnSaveConnect = findViewById(R.id.btnSaveConnect)
        btnDisconnect = findViewById(R.id.btnDisconnect)
        tvStatus = findViewById(R.id.tvStatus)
        tvMessage = findViewById(R.id.tvMessage)

        notificationHelper = NotificationHelper(this)

        btnSaveConnect.setOnClickListener {
            val ip = etServerIp.text.toString().trim()
            val port = etServerPort.text.toString().trim()

            if (ip.isEmpty() || port.isEmpty()) {
                tvStatus.text = "请输入有效的IP和端口"
                return@setOnClickListener
            }

            serverUrl = "ws://$ip:$port"
            startWebSocket()
        }

        btnDisconnect.setOnClickListener {
            stopWebSocket()
        }
    }

    private fun startWebSocket() {
        btnSaveConnect.isEnabled = false
        btnDisconnect.isEnabled = true
        tvStatus.text = "连接中..."

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
                    when (status) {
                        "已连接" -> {
                            btnSaveConnect.isEnabled = false
                            btnDisconnect.isEnabled = true
                        }
                        "已断开", "连接失败" -> {
                            btnSaveConnect.isEnabled = true
                            btnDisconnect.isEnabled = false
                        }
                    }
                }
            }
        )
        webSocketClient?.connect()
    }

    private fun stopWebSocket() {
        webSocketClient?.disconnect()
        webSocketClient = null
        runOnUiThread {
            btnSaveConnect.isEnabled = true
            btnDisconnect.isEnabled = false
            tvStatus.text = "已断开"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopWebSocket()
    }
}