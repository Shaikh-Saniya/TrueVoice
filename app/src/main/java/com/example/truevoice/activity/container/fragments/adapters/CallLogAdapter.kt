package com.example.truevoice.activity.container.fragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.truevoice.R

data class CallLogEntry(
    val phoneNumber: String,
    val callType: String,
    val callDate: String,
    val callDuration: String
)
    class CallLogAdapter(private val callLogs: List<CallLogEntry>) :
        RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>() {

        class CallLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val phoneNumber: TextView = itemView.findViewById(R.id.phoneNumber)
            val callType: TextView = itemView.findViewById(R.id.callType)
            val callDate: TextView = itemView.findViewById(R.id.callDate)
            val callDuration: TextView = itemView.findViewById(R.id.callDuration)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_call_logs, parent, false)
            return CallLogViewHolder(view)
        }

        override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
            val log = callLogs[position]
            holder.phoneNumber.text = log.phoneNumber
            holder.callType.text = log.callType
            holder.callDate.text = log.callDate
            holder.callDuration.text = log.callDuration
        }

        override fun getItemCount(): Int = callLogs.size
    }