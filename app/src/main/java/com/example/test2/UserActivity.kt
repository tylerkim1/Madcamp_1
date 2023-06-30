package com.example.test2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test2.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root) // this line is changed

        val intent = this.intent
        if (intent != null) {
            val name = intent.getStringExtra("name")
            val phone = intent.getStringExtra("phone")
            val info = intent.getStringExtra("info")
            val imageId = intent.getIntExtra("imageid", R.drawable.profile)
            binding!!.nameProfile.text = name
            binding!!.phoneProfile.text = phone
            binding!!.infoProfile.text = info
            binding!!.profileImage.setImageResource(imageId)
        }
    }
}