package com.example.test2

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class ContactDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        // Retrieve contact details from intent extras
        val name = intent.getStringExtra("name")
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val byteArray = intent.getByteArrayExtra("image")
        val bitmap = if (byteArray != null) {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } else {
            null
        }

        // Display contact details in the layout
        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        val phoneNumberTextView = findViewById<TextView>(R.id.phoneNumberTextView)
        val profileImageView = findViewById<ImageView>(R.id.profileImageView)


        nameTextView.text = name
        phoneNumberTextView.text = phoneNumber
        if (bitmap != null) {
            profileImageView.setImageBitmap(bitmap)
        } else {
            profileImageView.setImageResource(R.drawable.beats)
        }
    }
}

