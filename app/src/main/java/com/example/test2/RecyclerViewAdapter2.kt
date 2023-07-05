package com.example.test2

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.provider.ContactsContract
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.test2.ContactDataHolder.profileImagePath
import java.util.regex.Pattern

class RecyclerViewAdapter2(private val context: Context, private val dataSet: ArrayList<Card>,
                           private val newCardResultLauncher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ADD_BUTTON = 0
        private const val TYPE_CARD = 1
        const val REQUEST_NEW_CARD = 2
    }

    private val names = getContacts().map { it.first }.toTypedArray()

    private fun checkPermission3(): Boolean {
        val result2 = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        )
        return result2 == PackageManager.PERMISSION_GRANTED
    }

    private fun getContacts(): ArrayList<Pair<String, String>> {
        val contactsList = ArrayList<Pair<String, String>>()

        if (checkPermission3()) {
            val cursor: Cursor? = context.contentResolver.query(
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

                    var name = cursor.getString(nameColumnIndex)
                    val phoneNumber = cursor.getString(phoneNumberColumnIndex)

                    name = name.replace(" ", "_") // replace spaces with _

                    val contact = Pair(name, phoneNumber)
                    contactsList.add(contact)
                }
                cursor.close()
            }
        }

        return contactsList
    }

    class AddCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val addCardButton: Button = view.findViewById(R.id.add_card_button)
    }

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.item_details)
        val imageView: ImageView = view.findViewById(R.id.item_image)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_ADD_BUTTON else TYPE_CARD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            TYPE_ADD_BUTTON -> AddCardViewHolder(inflater.inflate(R.layout.tab3_newcard_model, parent, false))
            TYPE_CARD -> CardViewHolder(inflater.inflate(R.layout.tab3_card_model, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount() = dataSet.size + 1 // Plus 1 for the add button

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_ADD_BUTTON) {
            (holder as AddCardViewHolder).addCardButton.setOnClickListener {
                val intent = Intent(context, NewCardActivity::class.java)
                newCardResultLauncher.launch(intent)
            }
        } else {
            val cardPosition = dataSet.size - position
            val card = dataSet[cardPosition]

            holder.itemView.setOnClickListener {
                val intent = Intent(context, CardDetailActivity::class.java)
                intent.putExtra("imageUri", card.imageUri.toString()) // Uri를 String으로 변환하여 저장
                intent.putExtra("text", card.text)
                context.startActivity(intent)
            }

            with(holder as CardViewHolder) {
                // Add image resource setting
                if (card.imageUri != null) {
                    imageView.setImageURI(card.imageUri)
                } else {
                    imageView.setImageResource(R.drawable.profile3) // 기본 템플릿 카드에 대한 기본 이미지 리소스 설정
                }

                // Add the mentioned-name link feature
                val inputText = card.text
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
                                //val profileImagePath = getContacts().fir

                                val intent = Intent(context, ContactDetailActivity::class.java).apply {
                                    ContactDataHolder.name = validName
                                    ContactDataHolder.phoneNumber = phoneNumber
                                    ContactDataHolder.profileImagePath = profileImagePath
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
                        spannableString.setSpan(
                            UnderlineSpan(),
                            matcher.start(),
                            matcher.end(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }

                textView.apply {
                    text = spannableString
                    movementMethod = LinkMovementMethod.getInstance()
                }
            }

        }
    }
}