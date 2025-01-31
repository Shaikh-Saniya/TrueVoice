package com.example.truevoice.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast

class IncomingCallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    Toast.makeText(context, "Incoming Call...", Toast.LENGTH_SHORT).show()
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    Toast.makeText(context, "Call Answered", Toast.LENGTH_SHORT).show()
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Toast.makeText(context, "Call Ended", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
