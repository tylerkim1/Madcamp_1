package com.example.test2

import android.annotation.SuppressLint
import android.app.Activity
import android.Manifest
import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.test2.databinding.ActivityNewCardBinding
import java.util.regex.Pattern

class NewCardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewCardBinding
    private var selectedImageUri: Uri? = null
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    private var fullText: String = ""
    var oldTextLength = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), PERMISSIONS_REQUEST_READ_CONTACTS)
        } else {
            val contacts = getContacts()
            val adapter = CustomArrayAdapter(this, contacts)
            binding.editText.setAdapter(adapter)

            binding.editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if (s.isNotEmpty() && s[s.length - 1] == '@') {
                        binding.editText.showDropDown()
                    } else if (s.contains("@")) {
                        val mentionText = s.substring(s.lastIndexOf('@') + 1)  // Extract text after @
                        adapter.filter.filter(mentionText)  // Filter based on the mention text
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    fullText = s.toString()  // 텍스트가 변경되기 전에 전체 텍스트를 백업합니다.
                    oldTextLength = s.length
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isNotEmpty() && s[s.length - 1] == '@') {
                        adapter.getFilter().filter(null)
                    }
                    if (s.length < oldTextLength) { //지워졌을 때

                        val mentionsPattern = Pattern.compile("@[\\w_]+")
                        val matcher = mentionsPattern.matcher(s)

                        var lastMentionStart = -1
                        var lastMentionEnd = -1

                        while (matcher.find()) {
                            lastMentionStart = matcher.start()
                            lastMentionEnd = matcher.end()
                        }

                        // If the text ends with a '@mention', remove the mention
                        if (lastMentionStart != -1 && lastMentionEnd == s.length) {
                            val handler = Handler(Looper.getMainLooper())
                            handler.post {
                                val editable = binding.editText.text
                                editable.delete(lastMentionStart, lastMentionEnd)
                            }
                        }
                    }
                    oldTextLength = s.length
                }

            })

            binding.editText.setOnItemClickListener { _, _, position, _ ->
                val selectedContact = adapter.getItem(position).toString()
                val atIndex = fullText.lastIndexOf("@")
                if (atIndex != -1) {
                    val startText = fullText.substring(0, atIndex) // this is the text before last '@'
                    val spaceIndex = fullText.indexOf(' ', atIndex)
                    val remainingText = if (spaceIndex != -1) fullText.substring(spaceIndex) else "" // this is the text after the space following '@' if exists
                    val mentionText = "@$selectedContact"
                    val newText = "$startText$mentionText $remainingText" // combine all

                    val pattern = Pattern.compile("@([\\w_]+)")
                    val matcher = pattern.matcher(newText)

                    val spannableString = SpannableStringBuilder(newText)

                    while (matcher.find()) {
                        if (matcher.start() < spannableString.length && matcher.end() <= spannableString.length) {
                            spannableString.setSpan(
                                ForegroundColorSpan(Color.rgb(239,129,0)),
                                matcher.start(),
                                matcher.end(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }

                    binding.editText.setText(spannableString)
                    val newTextLength = newText.length
                    val editTextLength = binding.editText.text.length
                    if (newTextLength <= editTextLength) {
                        binding.editText.setSelection(newTextLength)
                    } else {
                        binding.editText.setSelection(editTextLength)
                    }
                }
            }
        }

        // 이미지 선택 버튼 클릭 시 갤러리 열기
        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_SELECT_IMAGE)
        }

        // 추가 버튼 클릭 시 새로운 카드 생성
        binding.addCardButton.setOnClickListener {
            val text = binding.editText.text.toString()

            if (selectedImageUri == null) {
                Toast.makeText(this, "사진을 선택해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(this, "추억을 입력해주세요!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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
            // 선택된 이미지를 이미지 뷰에 표시
            Glide.with(this)
                .load(selectedImageUri)
                .into(binding.imageView)
        }
    }

    @SuppressLint("Range")
    private fun getContacts(): ArrayList<String> {
        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        val contacts = ArrayList<String>()

        cursor?.let {
            while (cursor.moveToNext()) {
                val nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                var name = cursor.getString(nameColumnIndex)

                name = name.replace(" ", "_") // replace spaces with _
                contacts.add(name)
            }
            cursor.close()
        }

        return contacts
    }

    companion object {
        private const val REQUEST_SELECT_IMAGE = 1
    }
}
