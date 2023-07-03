package com.example.test2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import com.example.test2.databinding.ActivityNewCardBinding

class NewCardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewCardBinding
    private lateinit var selectedImageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 이미지 선택 버튼 클릭 시 갤러리 열기
        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_SELECT_IMAGE)
        }


        // 추가 버튼 클릭 시 새로운 카드 생성
        binding.addCardButton.setOnClickListener {
            val text = binding.editText.text.toString()
            val resultIntent = Intent().apply {
                putExtra("imageUri", selectedImageUri.toString()) // Convert Uri to String
                putExtra("text", text)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data!!
            // 선택된 이미지를 이미지 뷰에 표시하는 코드 추가
        }
    }

    companion object {
        private const val REQUEST_SELECT_IMAGE = 1
    }
}
