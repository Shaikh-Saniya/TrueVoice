package com.example.truevoice.service

import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log

class CallConnectionService : ConnectionService() {

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        Log.d("CallService", "Outgoing call started")
        return super.onCreateOutgoingConnection(connectionManagerPhoneAccount, request)
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        Log.d("CallService", "Incoming call received")
        val connection = object : Connection() {
            override fun onAnswer() {
                Log.d("CallService", "Call Answered")
            }

            override fun onReject() {
                Log.d("CallService", "Call Rejected")
                setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
                destroy()
            }
        }
        return connection
    }
}
