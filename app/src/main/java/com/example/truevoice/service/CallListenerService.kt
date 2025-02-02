package com.example.truevoice.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.Toast

class CallListenerService : Service() {

    private lateinit var telephonyManager: TelephonyManager

    override fun onCreate() {
        super.onCreate()
        listenForIncomingCalls()
    }

    private fun listenForIncomingCalls() {
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

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
        // Handle your custom UI for incoming calls
        Toast.makeText(this, "Incoming Call: $phoneNumber", Toast.LENGTH_LONG).show()
    }

    override fun onBind(intent: Intent?): IBinder? {
    return null
    }
}
