package com.luminary.servantlite

import android.content.Context
import android.content.SharedPreferences

class PrefsHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("udp_prefs", Context.MODE_PRIVATE)

    var listenIp: String
        get() = prefs.getString("ip", "0.0.0.0") ?: "0.0.0.0"
        set(value) = prefs.edit().putString("ip", value).apply()

    var listenPort: Int
        get() = prefs.getInt("port", 8888)
        set(value) = prefs.edit().putInt("port", value).apply()
}