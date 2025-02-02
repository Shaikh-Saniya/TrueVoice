package com.example.truevoice.activity.container.fragments

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.truevoice.R
import com.example.truevoice.activity.contacts.Contact
import com.example.truevoice.activity.contacts.ContactAdapter

class Contacts : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var contactAdapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), 100)
        } else {
            fetchContacts()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchContacts()
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("Range")
    private fun fetchContacts() {
        val contactList = mutableListOf<Contact>()
        val contentResolver: ContentResolver = requireContext().contentResolver

        // Query the Contacts Provider for contact data
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )

        cursor?.let {
            while (it.moveToNext()) {
                val name =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                // Add contact to the list
                contactList.add(Contact(name, phoneNumber))
            }
            it.close()
        }

            val nameContacts = contactList.filter { it.name.firstOrNull()?.isLetter() == true }
            val numberContacts = contactList.filter { it.name.firstOrNull()?.isDigit() == true }
            val sortedNameContacts = nameContacts.sortedBy { it.name }
            val sortedNumberContacts = numberContacts.sortedBy { it.phoneNumber }
            val finalSortedList = sortedNameContacts + sortedNumberContacts

            contactAdapter = ContactAdapter(finalSortedList)
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.contactsRV)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = contactAdapter

        return view
    }

    // Handle runtime permission result

}
