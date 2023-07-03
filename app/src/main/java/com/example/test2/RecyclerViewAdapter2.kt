package com.example.test2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.provider.ContactsContract
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.regex.Pattern

class RecyclerViewAdapter2(private val context: Context) : RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder>() {

    private val itemDetails = arrayOf(
        "@경효 과 함께 방문한 관광명소. 함께라서 더 즐거운 시간이었다. 언젠가 다시 가게 된다면 바닷가를 조금 더 봐야겠다. 정말 아름다운 풍경이었다. @최은서 와 함께해서 더 재밌었다. 부산의 앞바다에서 풍겨오는 바다내음은 어딘가 모를 그리움을 불러 일으키고 있었습니다. @노가은 과 함께 방문한 관광명소. 함께라서 더 즐거운 시간이었다. 언젠가 다시 가게 된다면 바닷가를 조금 더 봐야겠다. 정말 아름다운 풍경이었다. @최은서 와 함께해서 더 재밌었다. 부산의 앞바다에서 풍겨오는 바다내음은 어딘가 모를 그리움을 불러 일으키고 있었습니다.",
        "@이상현 와 함께 방문한 관광명소. 함께라서 더 즐거운 시간이었다. 언젠가 다시 가게 된다면 바닷가를 조금 더 봐야겠다. 정말 아름다운 풍경이었다. 재밌었다. 부산의 앞바다에서 풍겨오는 바다내음은 어딘가 모를 그리움을 불러 일으키고 있었습니다.",
        "추억을 입력하세요"
    )

    private val itemImages = intArrayOf(
        R.drawable.profile1,
        R.drawable.profile2,
        R.drawable.person
    )

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

                    val name = cursor.getString(nameColumnIndex)
                    val phoneNumber = cursor.getString(phoneNumberColumnIndex)

                    val contact = Pair(name, phoneNumber)
                    contactsList.add(contact)
                }
                cursor.close()
            }
        }

        return contactsList
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image: ImageView = itemView.findViewById(R.id.item_image)
        var textDes: TextView = itemView.findViewById(R.id.item_details)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_model, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemDetails.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.setImageResource(itemImages[position])

        val inputText = itemDetails[position]
        val pattern = Pattern.compile("@([가-힣]+)")  // 한글 이름을 처리하도록 정규표현식 변경
        val matcher = pattern.matcher(inputText)

        val spannableString = SpannableStringBuilder(inputText)

        while (matcher.find()) {
            val mentionName = matcher.group(1)
            if (names.contains(mentionName)) {  // names 배열을 사용하여 이름이 존재하는지 확인
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val context = widget.context
                        val phoneNumber = getContacts().firstOrNull { it.first == mentionName }?.second

                        val intent = Intent(context, UserActivity::class.java).apply {
                            putExtra("name", mentionName)
                            putExtra("phone", phoneNumber)  // phoneNumber 추가
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
                    ForegroundColorSpan(Color.BLUE),  // 글씨 색상을 파란색으로 설정합니다.
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    UnderlineSpan(),  // 밑줄을 추가합니다.
                    matcher.start(),
                    matcher.end(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        holder.textDes.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()  // 클릭 가능한 링크를 활성화합니다.
        }
    }

}