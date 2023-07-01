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
import android.database.Cursor
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.recyclerview.widget.GridLayoutManager
import com.example.test2.databinding.FragmentFourthBinding


class FourthFragment : Fragment() {
    private val PERMISSION_REQUEST_CODE = 300

    private var _binding: FragmentFourthBinding? = null
    private val binding get() = _binding!!


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

    private fun getContacts(): ArrayList<Pair<String, String>> {
        val contactsList = ArrayList<Pair<String, String>>()

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

                val name = cursor.getString(nameColumnIndex)
                val phoneNumber = cursor.getString(phoneNumberColumnIndex)

                val contact = Pair(name, phoneNumber)
                contactsList.add(contact)
            }
            cursor.close()
        }

        return contactsList
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
                    getContacts()
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
        requestPermissions2()
        return binding.root
    }


}

