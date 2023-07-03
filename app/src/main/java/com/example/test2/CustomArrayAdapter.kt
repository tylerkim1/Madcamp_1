package com.example.test2

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class CustomArrayAdapter(context: Context, resource: Int, val items: List<String>) :
    ArrayAdapter<String>(context, resource, items) {

    private var filteredItems: List<String> = items // 멤버 변수 추가

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                constraint?.let {
                    val searchTerm = it.substring(it.indexOf("@") + 1) // "@" 뒤의 텍스트만 추출
                    if (searchTerm.isEmpty()) {
                        filterResults.values = items
                        filterResults.count = items.size
                    } else {
                        filteredItems = items.filter { it.contains(searchTerm, ignoreCase = true) }
                        filterResults.values = filteredItems
                        filterResults.count = filteredItems.size
                    }
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results?.values as? List<String> ?: listOf()  // null이면 빈 리스트를 반환
                notifyDataSetChanged()
            }
        }
    }
}
