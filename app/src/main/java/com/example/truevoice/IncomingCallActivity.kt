package com.example.truevoice

import android.Manifest
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE),
                1001
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

                return
            }
            telecomManager.endCall()
        } else {
            Toast.makeText(this, "This feature requires Android 9.0 or higher.", Toast.LENGTH_SHORT).show()
        }
    }
}
