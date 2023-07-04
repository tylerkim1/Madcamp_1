package com.example.test2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.net.Uri
import android.view.View

object ContactDataHolder {
    var name: String? = null
    var phoneNumber: String? = null
    var profileImagePath: String? = null
}

class ContactDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        // Retrieve contact details from the ContactDataHolder
        val name = ContactDataHolder.name
        val phoneNumber = ContactDataHolder.phoneNumber
        val profileImagePath = ContactDataHolder.profileImagePath

        // Display contact details in the layout
        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        val phoneNumberTextView = findViewById<TextView>(R.id.phoneNumberTextView)
        val profileImageView = findViewById<ImageView>(R.id.profileImageView)

        nameTextView.text = name
        phoneNumberTextView.text = phoneNumber
        if (profileImagePath != null) {
            val profileImageBitmap = BitmapFactory.decodeFile(profileImagePath)
            profileImageView.setImageBitmap(profileImageBitmap)
        }
    }


    fun onCallButtonClick(view: View) {
        val phoneNumber = ContactDataHolder.phoneNumber
        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        startActivity(dialIntent)
    }

    fun onSendMessageButtonClick(view: View) {
        val phoneNumber = ContactDataHolder.phoneNumber
        val defaultSmsMessage = "문득 사진을 보다가 생각나서 연락했어. 잘, 지내내?" // 기본 메시지 내용

        val sendIntent = Intent(Intent.ACTION_SENDTO)
        sendIntent.data = Uri.parse("smsto:$phoneNumber")
        sendIntent.putExtra("sms_body", defaultSmsMessage) // 기본 메시지 내용을 설정합니다
        startActivity(sendIntent)
    }
}
