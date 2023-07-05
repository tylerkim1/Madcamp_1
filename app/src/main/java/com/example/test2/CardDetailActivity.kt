package com.example.test2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test2.databinding.ActivityCardDetailBinding
import java.util.regex.Pattern

class CardDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardDetailBinding
    private lateinit var names: Array<String>
    private val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    private var oldCard: Card? = null
    private val cardDatabaseHelper = CardDatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        names = getContacts().map { it.first }.toTypedArray()

        val imageUri = intent.getStringExtra("imageUri")?.let { Uri.parse(it) } // String을 Uri로 변환
        val inputText = intent.getStringExtra("text")

        // 이전 카드를 저장합니다.
        oldCard = if (imageUri != null && inputText != null) {
            Card(imageUri, inputText)
        } else {
            null
        }

        if (imageUri != null && inputText != null) {
            Glide.with(this).load(imageUri).into(binding.imageDetail)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), PERMISSIONS_REQUEST_READ_CONTACTS)
            } else {

                binding.editButton.setOnClickListener {
                    binding.textDetail.isEnabled = true // TextView를 수정 가능하게 만듭니다.
                    binding.saveButton.visibility = View.VISIBLE // 저장 버튼을 보이게 합니다.
                }

                binding.saveButton.setOnClickListener {
                    binding.textDetail.isEnabled = false // TextView를 다시 수정 불가능하게 만듭니다.
                    binding.saveButton.visibility = View.GONE // 저장 버튼을 숨깁니다.
                    // 여기에서 텍스트 변경사항을 저장하세요.
                    val newText = binding.textDetail.text.toString()
                    val oldCard = getYourOldCard() // 이전 카드를 가져옵니다. 이것은 데이터베이스에서 해당 카드를 찾는데 사용되는 이미지 URI와 이전 텍스트를 가지고 있어야 합니다.
                    oldCard?.let { card ->
                        cardDatabaseHelper.updateCardText(newText, card)
                    }
                }

                val pattern = Pattern.compile("@([\\w_]+)")  // Handle Korean names, English letters, spaces, parentheses, numbers and special characters
                val matcher = pattern.matcher(inputText)
                val spannableString = SpannableStringBuilder(inputText)

                while (matcher.find()) {
                    val mentionName = matcher.group(1)
                    val lastSpaceIndex = mentionName.lastIndexOf(' ')
                    val validName = if (lastSpaceIndex != -1) mentionName.substring(0, lastSpaceIndex) else mentionName

                    if (names.contains(validName)) {
                        val clickableSpan = object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                val context = widget.context
                                val phoneNumber = getContacts().firstOrNull { it.first == validName }?.second

                                val intent = Intent(context, ContactDetailActivity::class.java).apply {
                                    ContactDataHolder.name = validName
                                    ContactDataHolder.phoneNumber = phoneNumber
                                    ContactDataHolder.profileImagePath = ContactDataHolder.profileImagePath
                                }
                                context.startActivity(intent)
                            }
                        }

                        spannableString.setSpan(
                            clickableSpan,
                            matcher.start(),
                            matcher.end(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        spannableString.setSpan(
                            ForegroundColorSpan(Color.BLUE),
                            matcher.start(),
                            matcher.end(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }

                val textView: TextView = binding.textDetail

                textView.apply {
                    text = spannableString
                    movementMethod = LinkMovementMethod.getInstance()
                }
            }
        }
    }

    private fun getYourOldCard(): Card? {
        return oldCard
    }

    private fun getContacts(): ArrayList<Pair<String, String>> {
        val contactsList = ArrayList<Pair<String, String>>()
        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)

        cursor?.let {
            while (cursor.moveToNext()) {
                val nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val phoneNumberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                var name = cursor.getString(nameColumnIndex)
                val phoneNumber = cursor.getString(phoneNumberColumnIndex)

                name = name.replace(" ", "_") // replace spaces with _

                val contact = Pair(name, phoneNumber)
                contactsList.add(contact)
            }
            cursor.close()
        }

        return contactsList
    }
}
