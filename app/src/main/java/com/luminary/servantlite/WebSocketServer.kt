package com.luminary.servantlite

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.TimeUnit

/**
 * 简单模拟WebSocket“服务端”功能，监听指定端口
 * 实际上Android不适合做正式WebSocket服务器，
 * 这里用OkHttp WebSocket客户端监听远程连接消息，模拟“服务端”效果。
 *
 * 你可以用PC客户端连接此设备的IP和端口进行测试。
 */

class WebSocketServer(
    private val context: Context,
    private val onMessageReceived: (String) -> Unit
) {

    private val TAG = "WebSocketServer"

    // 监听端口（示例）
    private val PORT = 8765

    private var serverJob: Job? = null
    private var serverSocket: ServerSocket? = null

    private val notificationHelper = NotificationHelper(context)

    /**
     * 启动“WebSocket服务器”
     */
    fun startServer() {
        if (serverJob != null) return
        serverJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                serverSocket = ServerSocket(PORT)
                Log.i(TAG, "服务器启动，监听端口 $PORT")

                while (isActive) {
                    val client = serverSocket!!.accept()
                    Log.i(TAG, "收到客户端连接: ${client.inetAddress.hostAddress}")
                    handleClient(client)
                }
            } catch (e: Exception) {
                Log.e(TAG, "服务器异常: ${e.message}")
            }
        }
    }

    /**
     * 处理客户端Socket，读取消息并弹通知
     */
    private fun handleClient(client: Socket) {
        CoroutineScope(Dispatchers.IO).launch {
            client.use { socket ->
                val reader = socket.getInputStream().bufferedReader()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    line?.let {
                        Log.i(TAG, "收到消息: $it")
                        onMessageReceived(it)
                        notificationHelper.showNotification("紧急通知", it)
                    }
                }
            }
        }
    }

    /**
     * 停止服务器
     */
    fun stopServer() {
        serverJob?.cancel()
        serverJob = null
        try {
            serverSocket?.close()
            serverSocket = null
        } catch (e: Exception) {
            Log.e(TAG, "关闭服务器异常: ${e.message}")
        }
    }
}