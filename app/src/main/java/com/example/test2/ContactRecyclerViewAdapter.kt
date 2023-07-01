package com.example.test2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test2.R

class ContactRecyclerViewAdapter(
    private val context: Context,
    private val contactList: MutableList<Pair<String, String>>
) : RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val phoneNumberTextView: TextView = itemView.findViewById(R.id.phoneNumberTextView)

        fun bind(contact: Pair<String, String>) {
            nameTextView.text = contact.first
            phoneNumberTextView.text = contact.second
        }
    }

    fun setContacts(contacts: List<Pair<String, String>>) {
        contactList.clear()
        contactList.addAll(contacts)
        notifyDataSetChanged()
    }
}
