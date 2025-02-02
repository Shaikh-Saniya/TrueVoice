package com.example.truevoice

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class IncomingCallActivity : AppCompatActivity() {

    private val REQUEST_PERMISSIONS = 1001

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_incoming_call)

        // Check permissions
        checkPermissions()

        // Apply window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            return@setOnApplyWindowInsetsListener insets
        }

        // Get the caller number from the intent
        val callerNumber = intent.getStringExtra("CALLER_NUMBER") ?: "Unknown"
        findViewById<TextView>(R.id.callerNumber).text = "Incoming call from: $callerNumber"

        // Set up button listeners
        findViewById<Button>(R.id.answerButton).setOnClickListener {
            answerCall()
        }

        findViewById<Button>(R.id.endCallButton).setOnClickListener {
            endCall()
        }
    }

    private fun checkPermissions() {
        // Check if necessary permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            requestPermissions(
                arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE),
                REQUEST_PERMISSIONS
            )
        }
    }

    private fun answerCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ANSWER_PHONE_CALLS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permission to answer calls is required", Toast.LENGTH_SHORT).show()
                return
            }
            telecomManager.acceptRingingCall()
        } else {
            Toast.makeText(this, "This feature requires Android 8.0 or higher.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun endCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ANSWER_PHONE_CALLS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permission to end calls is required", Toast.LENGTH_SHORT).show()
                return
            }
            telecomManager.endCall()
        } else {
            Toast.makeText(this, "This feature requires Android 9.0 or higher.", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, you can proceed with call functionality
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                // Permissions denied, show a message or prompt the user to allow permissions
                Toast.makeText(this, "Permissions denied, unable to answer/end calls", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
