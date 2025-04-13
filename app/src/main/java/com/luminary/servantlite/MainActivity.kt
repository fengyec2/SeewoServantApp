package com.luminary.servantlite

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: PrefsHelper
    private val REQUEST_NOTIFICATION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = PrefsHelper(this)
        setupUI()
        requestNotificationPermission()
        startService()
    }

    private fun setupUI() {
        findViewById<EditText>(R.id.etIp).setText(prefs.listenIp)
        findViewById<EditText>(R.id.etPort).setText(prefs.listenPort.toString())

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveSettings()
            restartService()
        }
    }

    private fun saveSettings() {
        prefs.listenIp = findViewById<EditText>(R.id.etIp).text.toString()
        prefs.listenPort = findViewById<EditText>(R.id.etPort).text.toString().toIntOrNull() ?: 8888
    }

    private fun startService() {
        val serviceIntent = Intent(this, UdpListenerService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun restartService() {
        val stopIntent = Intent(this, UdpListenerService::class.java)
        stopService(stopIntent)
        startService()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showPermissionRationaleDialog()
                }
                else -> {
                    requestPermission()
                }
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("需要通知权限")
            .setMessage("此应用需要通知权限来显示UDP消息提醒")
            .setPositiveButton("确定") { _, _ ->
                requestPermission()
            }
            .show()
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_NOTIFICATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限已授予
                }
            }
        }
    }
}