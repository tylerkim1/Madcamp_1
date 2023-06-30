package com.example.test2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import android.Manifest
import android.os.Environment


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val PERMISSION_REQUEST_CODE = 200
    private var imagePaths: ArrayList<String>? = null
    private var imagesRV: RecyclerView? = null
    private var imageRVAdapter: RecyclerViewAdapter? = null
    private fun checkPermission(): Boolean {
        // Check if the permissions are granted or not and return the result.
        val result = ContextCompat.checkSelfPermission(
            requireActivity().applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        if (checkPermission()) {
            // If the permissions are already granted, get all images from external storage.
            Toast.makeText(requireContext(), "Permissions granted..", Toast.LENGTH_SHORT).show()
            getImagePath()
        } else {
            // If the permissions are not granted, request permissions.
            requestPermission()
        }
    }

    private fun requestPermission() {
        // Request the read external storage permission.
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun prepareRecyclerView() {
        // Prepare the RecyclerView.
        imageRVAdapter = RecyclerViewAdapter(requireContext(), imagePaths!!)
        val manager = GridLayoutManager(requireContext(), 3)
        imagesRV?.layoutManager = manager
        imagesRV?.adapter = imageRVAdapter
    }

    private fun getImagePath() {
        // Get all image paths from external storage.
        val isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        if (isSDPresent) {
            val columns = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
            )
            val orderBy = MediaStore.Images.Media._ID
            val cursor: Cursor? = requireActivity().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,
                null,
                null,
                orderBy
            )
            val count = cursor?.count ?: 0
            for (i in 0 until count) {
                cursor?.moveToPosition(i)
                val dataColumnIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
                imagePaths?.add(cursor?.getString(dataColumnIndex!!)!!)
            }
            cursor?.close()
            imageRVAdapter?.notifyDataSetChanged()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // This method is called after permissions have been granted.
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted) {
                        Toast.makeText(
                            requireContext(),
                            "Permissions Granted..",
                            Toast.LENGTH_SHORT
                        ).show()
                        getImagePath()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Permissions denied, Permissions are required to use the app..",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)
        // Initialize RecyclerView and other views
        imagesRV = view.findViewById(R.id.idRVImages)
        imagePaths = ArrayList()
        prepareRecyclerView()
        requestPermissions()
        return view
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}