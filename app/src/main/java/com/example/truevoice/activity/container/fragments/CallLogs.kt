package com.example.truevoice.activity.container.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CallLog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.truevoice.activity.container.fragments.adapters.CallLogAdapter
import com.example.truevoice.activity.container.fragments.adapters.CallLogEntry
import com.example.truevoice.databinding.FragmentCallLogsBinding

class CallLogs : Fragment() {

    private lateinit var binding: FragmentCallLogsBinding
    private lateinit var callLogAdapter: CallLogAdapter
    private val callLogs = mutableListOf<CallLogEntry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCallLogsBinding.inflate(inflater, container, false)

        binding.callLogRV.layoutManager = LinearLayoutManager(requireContext())
        callLogAdapter = CallLogAdapter(callLogs)
        binding.callLogRV.adapter = callLogAdapter

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

    private fun fetchCallLogs() {
        val cursor = requireContext().contentResolver.query(
            CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

            while (it.moveToNext()) {
                val number = it.getString(numberIndex)
                val type = when (it.getInt(typeIndex)) {
                    CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    else -> "Unknown"
                }
                val date = it.getString(dateIndex)
                val duration = it.getString(durationIndex)

                callLogs.add(CallLogEntry(number, type, date, duration))
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
}
