package com.example.test2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.content.pm.PackageManager

import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import android.Manifest
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test2.databinding.FragmentFourthBinding
import androidx.recyclerview.widget.RecyclerView

class FourthFragment : Fragment() {
    private val PERMISSION_REQUEST_CODE = 300

    private var _binding: FragmentFourthBinding? = null
    private val binding get() = _binding!!

    private var contactsRV: RecyclerView? = null
    private var contactRVAdapter: ContactRecyclerViewAdapter? = null

    private fun checkPermission2(): Boolean {
        val result2 = ContextCompat.checkSelfPermission(
            requireActivity().applicationContext,
            Manifest.permission.READ_CONTACTS
        )
        return result2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions2() {
        if (checkPermission2()) {
            // 연락처 권한이 승인되어 있는 경우 연락처 관련 작업 수행
            Toast.makeText(requireContext(), "Contact permissions granted..", Toast.LENGTH_SHORT).show()
            // 연락처 처리 작업 수행
            getContacts()

        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        // Request the read external storage permission.
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_CONTACTS),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun getContacts(): ArrayList<Triple<String, String, Bitmap?>> {
        val contactsList = ArrayList<Triple<String, String, Bitmap?>>()

        val cursor: Cursor? = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursor?.let {
            while (cursor.moveToNext()) {
                val nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val phoneNumberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val contactIdColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)

                val name = cursor.getString(nameColumnIndex)
                val phoneNumber = cursor.getString(phoneNumberColumnIndex)
                val contactId = cursor.getLong(contactIdColumnIndex)
                val photoUri = getPhotoUri(contactId)

                val photoBitmap: Bitmap? = if (photoUri != null) {
                    val inputStream = requireActivity().contentResolver.openInputStream(photoUri)
                    BitmapFactory.decodeStream(inputStream)
                } else {
                    null
                }

                contactsList.add(Triple(name, phoneNumber, photoBitmap))
            }
            cursor.close()
        }

        return contactsList
    }

    private fun getPhotoUri(contactId: Long): Uri? {
        val contentResolver = requireActivity().contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Photo.PHOTO_URI),
            "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
            arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE),
            null
        )

        var photoUri: Uri? = null
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI)
                photoUri = Uri.parse(it.getString(columnIndex))
            }
        }

        cursor?.close()
        return photoUri
    }
    private fun prepareRecyclerView() {
        // Prepare the RecyclerView.
        contactRVAdapter = ContactRecyclerViewAdapter(requireContext(), getContacts())
        contactRVAdapter?.setOnItemClickListener(object : ContactRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(contact: Triple<String, String, Bitmap?>) {
                navigateToContactDetail(contact)
            }
        })
        val manager = LinearLayoutManager(requireContext())
        contactsRV?.layoutManager = manager
        contactsRV?.adapter = contactRVAdapter
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        contactsRV = binding.contactsRecyclerView

        // Set item click listener for RecyclerView
        contactRVAdapter?.setOnItemClickListener(object : ContactRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(contact: Triple<String, String, Bitmap?>) {
                navigateToContactDetail(contact)
            }
        })

        // Request permissions and prepare RecyclerView
        requestPermissions2()
        prepareRecyclerView()
    }


    private fun navigateToContactDetail(contact: Triple<String, String, Bitmap?>) {
        val intent = Intent(requireContext(), ContactDetailActivity::class.java)
        intent.putExtra("name", contact.first)
        intent.putExtra("phoneNumber", contact.second)
        contact.third?.let { bitmap ->
            val photoUri = getPhotoUri(contact.first.toLong()) // Pass the name to get the photo URI
            intent.putExtra("photoUri", photoUri)
        }
        startActivity(intent)
    }





    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 연락처 권한이 승인되었을 경우 연락처 관련 작업 수행
                    Toast.makeText(requireContext(), "Contact permissions granted..", Toast.LENGTH_SHORT).show()
                    // 연락처 처리 작업 수행
                    val contacts = getContacts()

                    // Update RecyclerView with new data
                    contactRVAdapter?.setContacts(contacts)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Contact permissions denied, required to use the app..",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFourthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


