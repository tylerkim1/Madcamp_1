package com.example.test2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ThirdFragment : Fragment() {

    private val cards = ArrayList<Card>()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<*>? = null

    private val newCardResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.getStringExtra("imageUri")?.let { Uri.parse(it) } // Convert String to Uri
            val text = data?.getStringExtra("text")
            if (text != null) {
                val newCard = Card(imageUri, text)
                cards.add(newCard)
                adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        cards.apply {
            add(Card( Uri.parse(""),
                "@경효 과 함께 방문한 관광명소. 함께라서 더 즐거운 시간이었다. 언젠가 다시 가게 된다면 바닷가를 조금 더 봐야겠다. 정말 아름다운 풍경이었다. @최은서 와 함께해서 더 재밌었다. 부산의 앞바다에서 풍겨오는 바다내음은 어딘가 모를 그리움을 불러 일으키고 있었습니다. @노가은 과 함께 방문한 관광명소. 함께라서 더 즐거운 시간이었다. 언젠가 다시 가게 된다면 바닷가를 조금 더 봐야겠다. 정말 아름다운 풍경이었다. @최은서 와 함께해서 더 재밌었다. 부산의 앞바다에서 풍겨오는 바다내음은 어딘가 모를 그리움을 불러 일으키고 있었습니다."))
            add(Card( Uri.parse(""),
                "@이상현 와 함께 방문한 관광명소. 함께라서 더 즐거운 시간이었다. 언젠가 다시 가게 된다면 바닷가를 조금 더 봐야겠다. 정말 아름다운 풍경이었다. 재밌었다. 부산의 앞바다에서 풍겨오는 바다내음은 어딘가 모를 그리움을 불러 일으키고 있었습니다."))
            // add more cards if needed
            add(Card( Uri.parse(""),
                "@이상현 안녕하세요"))
            // add more cards if needed
        }
        adapter = RecyclerViewAdapter2(requireContext(), cards, newCardResultLauncher)
        recyclerView.adapter = adapter

    }
}