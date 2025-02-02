package com.example.truevoice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.truevoice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { it }) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up dial pad buttons
        val dialPadButtons = listOf(
            binding.one,
            binding.two,
            binding.three,
            binding.four,
            binding.five,
            binding.six,
            binding.seven,
            binding.eight,
            binding.nine,
            binding.zero,
            binding.star,
            binding.hash
        )

        // Add listeners for dial pad buttons
        dialPadButtons.forEach { button ->
            button?.setOnClickListener {
                val input = button?.text.toString()
                binding.phoneNumber?.append(input)
            }
        }

        // Clear the entered number
        binding.clearBtn?.setOnClickListener {
            binding.phoneNumber?.text = ""
        }

        // Make a call
        binding.callBtn?.setOnClickListener {
            var phoneNumber = binding.phoneNumber?.text.toString()
            if (phoneNumber.isNotEmpty()) {
                makeCall(phoneNumber)
                phoneNumber= " "
            } else {
                Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }

        // Request necessary permissions
        requestPermissions()
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.CALL_PHONE
            )
        )
    }

    private fun makeCall(phoneNumber: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Grant CALL_PHONE permission", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        startActivity(intent)
        binding.phoneNumber?.text=""
    }
}
