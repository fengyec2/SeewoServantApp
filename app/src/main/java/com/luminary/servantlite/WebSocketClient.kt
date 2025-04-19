package com.luminary.servantlite

import android.util.Log
import okhttp3.*
import okio.ByteString

class WebSocketClient(
    private val url: String,
    private val onMessageReceived: (String) -> Unit,
    private val onStatusChanged: (String) -> Unit
) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.i("WebSocketClient", "连接成功")
                onStatusChanged("已连接")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.i("WebSocketClient", "收到消息: $text")
                onMessageReceived(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                val msg = bytes.utf8()
                Log.i("WebSocketClient", "收到二进制消息: $msg")
                onMessageReceived(msg)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.i("WebSocketClient", "连接关闭: $reason")
                onStatusChanged("连接关闭")
                webSocket.close(code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocketClient", "连接失败: ${t.message}")
                onStatusChanged("连接失败: ${t.message}")
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "客户端关闭连接")
        webSocket = null
        onStatusChanged("已断开")
    }
}