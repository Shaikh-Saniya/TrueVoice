package com.example.truevoice.activity.container

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.truevoice.IncomingCallActivity
import com.example.truevoice.MainActivity
import com.example.truevoice.R
import com.example.truevoice.activity.container.fragments.CallLogs
import com.example.truevoice.activity.container.fragments.Contacts
import com.example.truevoice.activity.container.fragments.IncommingCall
import com.example.truevoice.activity.container.fragments.OutgoingCall
import com.example.truevoice.databinding.ActivityContianerBinding
import android.content.Context
import android.provider.Settings
import android.telecom.TelecomManager
import com.example.truevoice.service.CallListenerService


class Container : AppCompatActivity() {

   private lateinit var binding: ActivityContianerBinding
   private var previousFragment=0
    private val PERMISSIONS_REQUEST_CODE = 123
    private lateinit var telephonyManager: TelephonyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val serviceIntent = Intent(this, CallListenerService::class.java)
        startService(serviceIntent)

        checkAndRequestPermissions()

        binding=ActivityContianerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(previousFragment,IncommingCall())
        previousFragment=1
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (arePermissionsGranted()) {
            val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val defaultDialerPackage = telecomManager.defaultDialerPackage
            if (defaultDialerPackage == packageName) {
                Toast.makeText(this@Container, "True Voice is your default dialer app", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                startActivity(intent)
            }
        } else {
            // Prompt for necessary permissions if not granted
            requestPermissions(arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ANSWER_PHONE_CALLS
            ), PERMISSIONS_REQUEST_CODE)
        }



        binding.makeCall.setOnClickListener{
            startActivity(Intent(this@Container,MainActivity::class.java))
        }

        binding.incommingCl.setOnClickListener {
            if( previousFragment!=1) {
            binding.incommingCl.visibility=View.INVISIBLE
            binding.incommingClGrd.visibility=View.VISIBLE
                binding.CallType.text="Incomming Calls"
                replaceFragment(previousFragment,IncommingCall())
                previousFragment=1

            }

            }

        binding.outgoingCl.setOnClickListener {
            if( previousFragment!=2) {
            binding.outgoingCl.visibility=View.INVISIBLE
            binding.outgoingClGrd.visibility=View.VISIBLE
                binding.CallType.text="Outgoing Call"
                replaceFragment(previousFragment,OutgoingCall())
                previousFragment=2
            }

        }

        binding.clLogs.setOnClickListener {
            if( previousFragment!=3) {
            binding.clLogs.visibility=View.INVISIBLE
            binding.clLogsGrd.visibility=View.VISIBLE
                binding.CallType.text="Call Logs"
                replaceFragment(previousFragment,CallLogs())
                previousFragment=3
            }
        }

        binding.contacts.setOnClickListener {
            if( previousFragment!=4) {
            binding.contacts.visibility=View.INVISIBLE
            binding.contactsGrd.visibility=View.VISIBLE
                binding.CallType.text="Contacts"
                replaceFragment(previousFragment,Contacts())
                previousFragment=4
            }

        }
        
    }

    private fun replaceFragment(previousFragment:Int , currentFragment:Fragment){
        Log.d("fragments","$previousFragment")
        when(previousFragment){
             1 ->{ binding.incommingCl.visibility=View.VISIBLE
                 binding.incommingClGrd.visibility=View.INVISIBLE
             }
            2 ->{
                binding.outgoingCl.visibility=View.VISIBLE
                binding.outgoingClGrd.visibility=View.INVISIBLE
            }
            3 ->{
                binding.clLogs.visibility=View.VISIBLE
                binding.clLogsGrd.visibility=View.INVISIBLE
            }
            4 ->{
                binding.contacts.visibility=View.VISIBLE
                binding.contactsGrd.visibility=View.INVISIBLE
            }0->{

            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer,currentFragment).commit()


    }
    private fun checkAndRequestPermissions() {
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ANSWER_PHONE_CALLS
            ), PERMISSIONS_REQUEST_CODE)
        } else {
            listenForIncomingCalls()
        }
    }

    private fun arePermissionsGranted(): Boolean {
        return checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                listenForIncomingCalls()
            } else {
                Toast.makeText(
                    this,
                    "Permissions denied. Cannot proceed without required permissions.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun listenForIncomingCalls() {
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // Register the PhoneStateListener
        telephonyManager.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        // Call is incoming, show UI
                        showIncomingCallUI(phoneNumber)
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        // Call is answered
                        Toast.makeText(this@Container, "Call Answered", Toast.LENGTH_SHORT).show()
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        // Call ended or no call is active
                        Toast.makeText(this@Container, "No Call Active", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun showIncomingCallUI(phoneNumber: String?) {
        // Show your custom UI for incoming calls
        Toast.makeText(this, "Incoming Call: $phoneNumber", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Don't forget to unregister the listener when the activity is destroyed
        telephonyManager.listen(null, PhoneStateListener.LISTEN_NONE)
    }
}
