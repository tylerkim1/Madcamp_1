package com.example.test2

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.test2.databinding.ActivityNewCardBinding
import java.io.ByteArrayOutputStream

class NewCardActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var editText: EditText
    private lateinit var buttonSelectImage: Button
    private lateinit var buttonAddCard: Button

    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_card)

        imageView = findViewById(R.id.imageView)
        editText = findViewById(R.id.editText)
        buttonSelectImage = findViewById(R.id.button_select_image)
        buttonAddCard = findViewById(R.id.button_add_card)

        buttonSelectImage.setOnClickListener {
            dispatchTakePictureIntent()
        }

        buttonAddCard.setOnClickListener {
            val text = editText.text.toString()
            val image = imageView.tag as? Uri

            if (text.isNotBlank() && image != null) {
                val resultIntent = Intent().apply {
                    putExtra("text", text)
                    putExtra("image", image.toString())
                }

                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            val uri = getImageUriFromBitmap(imageBitmap)

            imageView.setImageBitmap(imageBitmap)
            imageView.tag = uri
        }
    }

    companion object {
        const val REQUEST_IMAGE_PICK = 1
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }
}
