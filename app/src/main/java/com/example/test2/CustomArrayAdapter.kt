package com.example.test2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView

class CustomArrayAdapter(context: Context, val items: List<String>) :
    BaseAdapter(), Filterable {

    private var filteredItems: List<String> = items

    private val filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            val searchTerm = constraint?.substring(constraint.indexOf("@") + 1) ?: "" // "@" 뒤의 텍스트만 추출
            if (searchTerm.isEmpty()) {
                filterResults.values = items
                filterResults.count = items.size
            } else {
                filteredItems = items.filter { it.contains(searchTerm, ignoreCase = true) }
                filterResults.values = filteredItems
                filterResults.count = filteredItems.size
            }
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results?.count ?: 0 > 0) {
                filteredItems = results?.values as? List<String> ?: listOf()  // null이면 빈 리스트를 반환
                notifyDataSetChanged()
            }
        }

    }

    override fun getCount(): Int {
        return filteredItems.size
    }

    override fun getItem(position: Int): String {
        return if (position < filteredItems.size) filteredItems[position] else ""
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView = convertView as? TextView
            ?: LayoutInflater.from(parent.context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false) as TextView
        view.text = getItem(position)
        return view
    }

    override fun getFilter(): Filter {
        return filter
    }
}
