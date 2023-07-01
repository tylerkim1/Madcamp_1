package com.example.test2

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter2 : RecyclerView.Adapter<RecyclerViewAdapter2.ViewHolder>() {

    private val itemDetails = arrayOf(
        "@노가은 과 함께 방문한 관광명소. 함께라서 더 즐거운 시간이었다. 언젠가 다시 가게 된다면 바닷가를 조금 더 봐야겠다. 정말 아름다운 풍경이었다. @최은서 와 함께해서 더 재밌었다. 부산의 앞바다에서 풍겨오는 바다내음은 어딘가 모를 그리움을 불러 일으키고 있었습니다. @노가은 과 함께 방문한 관광명소. 함께라서 더 즐거운 시간이었다. 언젠가 다시 가게 된다면 바닷가를 조금 더 봐야겠다. 정말 아름다운 풍경이었다. @최은서 와 함께해서 더 재밌었다. 부산의 앞바다에서 풍겨오는 바다내음은 어딘가 모를 그리움을 불러 일으키고 있었습니다.",
        "@최은서 와 함께 방문한 관광명소. 함께라서 더 즐거운 시간이었다. 언젠가 다시 가게 된다면 바닷가를 조금 더 봐야겠다. 정말 아름다운 풍경이었다. 재밌었다. 부산의 앞바다에서 풍겨오는 바다내음은 어딘가 모를 그리움을 불러 일으키고 있었습니다.",
        "추억을 입력하세요"
    )

    private val itemImages = intArrayOf(
        R.drawable.profile1,
        R.drawable.profile2,
        R.drawable.person
    )

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image: ImageView
        var textDes : TextView

        init {
            image = itemView.findViewById(R.id.item_image)
            textDes = itemView.findViewById(R.id.item_details)
        }
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
        holder.textDes.text = itemDetails[position]
        holder.image.setImageResource(itemImages[position])
        holder.textDes.movementMethod = ScrollingMovementMethod()

//        holder.itemView.setOnClickListener { v: View ->
//
//            Toast.makeText(v.context, "Clicked on the item", Toast.LENGTH_SHORT).show()
//
//        }
    }

}