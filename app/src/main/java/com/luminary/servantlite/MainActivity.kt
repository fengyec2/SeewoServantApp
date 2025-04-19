package com.luminary.servantlite

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
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

    // SharedPreferences 名称和键名
    private val PREFS_NAME = "server_prefs"
    private val KEY_IP = "server_ip"
    private val KEY_PORT = "server_port"

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

        // 读取保存的IP和端口，赋值到输入框
        loadSavedServerInfo()

        // 申请忽略电池优化权限，提升后台存活率
        requestIgnoreBatteryOptimization()

        btnSaveConnect.setOnClickListener {
            val ip = etServerIp.text.toString().trim()
            val port = etServerPort.text.toString().trim()

            if (ip.isEmpty() || port.isEmpty()) {
                tvStatus.text = getString(R.string.ip_or_port_invalid)
                return@setOnClickListener
            }

            // 保存输入的IP和端口
            saveServerInfo(ip, port)

            serverUrl = "ws://$ip:$port"

            // 启动前台服务，保证后台运行
            startForegroundService()

            startWebSocket()
        }

        btnDisconnect.setOnClickListener {
            stopWebSocket()
            stopForegroundService()
        }
    }

    private fun loadSavedServerInfo() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedIp = prefs.getString(KEY_IP, "")
        val savedPort = prefs.getString(KEY_PORT, "")
        etServerIp.setText(savedIp)
        etServerPort.setText(savedPort)
    }

    private fun saveServerInfo(ip: String, port: String) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_IP, ip)
            .putString(KEY_PORT, port)
            .apply()
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
                    notificationHelper.showNotification("服务器消息", message)
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

    private fun startForegroundService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun stopForegroundService() {
        val serviceIntent = Intent(this, ForegroundService::class.java)
        stopService(serviceIntent)
    }

    private fun requestIgnoreBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopWebSocket()
        stopForegroundService()
    }
}