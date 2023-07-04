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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.properties.Delegates

class ThirdFragment : Fragment() {

    private var cards = ArrayList<Card>()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<*>? = null
    private lateinit var dbHelper: CardDatabaseHelper
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val newCardResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri = data?.getStringExtra("imageUri")?.let { Uri.parse(it) } // Convert String to Uri
            val text = data?.getStringExtra("text")
            if (text != null && imageUri != null) {
                val newCard = Card(imageUri, text)
                dbHelper.addCard(newCard)
                cards.add(newCard)
                adapter?.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = true
                refreshData()
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

        swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        dbHelper = CardDatabaseHelper(requireContext())
        cards = dbHelper.getAllCards().toMutableList() as ArrayList<Card>

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        adapter = RecyclerViewAdapter2(requireContext(), cards, newCardResultLauncher)
        recyclerView.adapter = adapter
    }

    private fun refreshData() {
        // 카드를 다시 데이터베이스에서 불러오기
        cards.clear()
        cards.addAll(dbHelper.getAllCards())
        adapter?.notifyDataSetChanged()
        swipeRefreshLayout.isRefreshing = false
    }
}