package com.example.truevoice.activity.container

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.truevoice.MainActivity
import com.example.truevoice.R
import com.example.truevoice.activity.container.fragments.CallLogs
import com.example.truevoice.activity.container.fragments.Contacts
import com.example.truevoice.activity.container.fragments.IncommingCall
import com.example.truevoice.activity.container.fragments.OutgoingCall
import com.example.truevoice.databinding.ActivityContianerBinding

class Container : AppCompatActivity() {

   private lateinit var binding: ActivityContianerBinding
   private var previousFragment=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding=ActivityContianerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(previousFragment,IncommingCall())
        previousFragment=1
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.makeCall.setOnClickListener{
            startActivity(Intent(this@Container,MainActivity::class.java))
        }

        binding.incommingCl.setOnClickListener {
            if( previousFragment!=1) {
            binding.incommingCl.visibility=View.INVISIBLE
            binding.incommingClGrd.visibility=View.VISIBLE
                replaceFragment(previousFragment,IncommingCall())
                previousFragment=1
            }

            }

        binding.outgoingCl.setOnClickListener {
            if( previousFragment!=2) {
            binding.outgoingCl.visibility=View.INVISIBLE
            binding.outgoingClGrd.visibility=View.VISIBLE
                replaceFragment(previousFragment,OutgoingCall())
                previousFragment=2
            }

        }

        binding.clLogs.setOnClickListener {
            if( previousFragment!=3) {
            binding.clLogs.visibility=View.INVISIBLE
            binding.clLogsGrd.visibility=View.VISIBLE

                replaceFragment(previousFragment,CallLogs())
                previousFragment=3
            }
        }

        binding.contacts.setOnClickListener {
            if( previousFragment!=4) {
            binding.contacts.visibility=View.INVISIBLE
            binding.contactsGrd.visibility=View.VISIBLE
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
}