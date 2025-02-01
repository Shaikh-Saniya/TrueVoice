package com.example.truevoice.activity.container.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CallLog
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.truevoice.activity.container.fragments.adapters.CallLogAdapter
import com.example.truevoice.activity.container.fragments.adapters.CallLogEntry
import com.example.truevoice.databinding.FragmentOutgoingCallBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OutgoingCall : Fragment() {

    private lateinit var binding: FragmentOutgoingCallBinding
    private lateinit var callLogAdapter: CallLogAdapter
    private val callLogs = mutableListOf<CallLogEntry>()

    // Create the BroadcastReceiver instance
    private val callReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            // Check if the call state is outgoing
            if (TelephonyManager.EXTRA_STATE_OFFHOOK == state) {
                // Outgoing call has started, fetch fresh data or update the list
                if (phoneNumber != null) {
                    fetchCallLogs()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOutgoingCallBinding.inflate(inflater, container, false)

        binding.outgoingClRv.layoutManager = LinearLayoutManager(requireContext())
        callLogAdapter = CallLogAdapter(callLogs)
        binding.outgoingClRv.adapter = callLogAdapter

        // Check for permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.READ_CALL_LOG), 101)
        } else {
            fetchCallLogs()
        }

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchCallLogs() {
        // Clear existing call logs before fetching new data
        callLogs.clear()

        // Add a selection to query only outgoing calls
        val selection = CallLog.Calls.TYPE + " = ?"
        val selectionArgs = arrayOf(CallLog.Calls.OUTGOING_TYPE.toString()) // 2 is for outgoing calls

        val cursor = requireContext().contentResolver.query(
            CallLog.Calls.CONTENT_URI, null, selection, selectionArgs, CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

            // Initialize SimpleDateFormat to format the timestamp
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())


            while (it.moveToNext()) {
                val number = it.getString(numberIndex)
                val type = when (it.getInt(typeIndex)) {
                    CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    else -> "Unknown"
                }
                val dateMillis = it.getLong(dateIndex)
                val formattedDate = dateFormat.format(Date(dateMillis)) // Format the date as a string

                val duration = it.getString(durationIndex)

                // Add the call log entry to the list
                callLogs.add(CallLogEntry(number, type, formattedDate, duration))
            }
            callLogAdapter.notifyDataSetChanged()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCallLogs()
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Register the BroadcastReceiver when the fragment starts
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)
        requireContext().registerReceiver(callReceiver, filter)
    }

    // Unregister the BroadcastReceiver when the fragment stops
    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(callReceiver)
    }
}
