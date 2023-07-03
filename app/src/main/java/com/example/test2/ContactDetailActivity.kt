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
//import com.example.test2.databinding.ActivityUserBinding


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

//        phoneNumberTextView.setOnClickListener {
//            dialPhoneNumber(phoneNumber)
//        }
    }

//    private fun dialPhoneNumber(phoneNumber: String?) {
//        val intent = Intent(Intent.ACTION_DIAL)
//        intent.data = Uri.parse("tel:$phoneNumber")
//        startActivity(intent)
//    }
    fun onCallButtonClick(view: View) {
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        startActivity(dialIntent)
    }
    fun onSendMessageButtonClick(view: View) {
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val defaultSmsMessage = "안녕하세요, 연락드렸습니다!" // 기본 메시지 내용

        val sendIntent = Intent(Intent.ACTION_SENDTO)
        sendIntent.data = Uri.parse("smsto:$phoneNumber")
        sendIntent.putExtra("sms_body", defaultSmsMessage) // 기본 메시지 내용을 설정합니다
        startActivity(sendIntent)
    }

}
