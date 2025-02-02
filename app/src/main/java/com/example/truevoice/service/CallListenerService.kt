package com.example.truevoice.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.Toast
import com.example.truevoice.IncomingCallActivity

class CallListenerService : Service() {

    private lateinit var telephonyManager: TelephonyManager

    override fun onCreate() {
        super.onCreate()
        // Initialize the TelephonyManager in onCreate
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        listenForIncomingCalls()
    }

    private fun listenForIncomingCalls() {
        // Register the PhoneStateListener to listen for incoming call state changes
        telephonyManager.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        // Handle incoming call UI
                        showIncomingCallUI(phoneNumber)
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        // Call answered
                        Toast.makeText(applicationContext, "Call Answered", Toast.LENGTH_SHORT).show()
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        // Call ended
                        Toast.makeText(applicationContext, "No Call Active", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun showIncomingCallUI(phoneNumber: String?) {
        // Start IncomingCallActivity with a flag to prevent errors
        val intent = Intent(this, IncomingCallActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Add flag to start the activity
            putExtra("PHONE_NUMBER", phoneNumber)  // Optional: pass the phone number
        }
        startActivity(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Since this is a service without binding, return null
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the listener when the service is destroyed
        telephonyManager.listen(null, PhoneStateListener.LISTEN_NONE)
    }
}
