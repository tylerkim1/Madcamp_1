package com.example.test2

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.ByteArrayOutputStream

class ContactRecyclerViewAdapter(
    private val context: Context,
    private val contactList: ArrayList<Triple<String, String, Bitmap?>>,
    private var itemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(contact: Triple<String, String, Bitmap?>)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(itemView, itemClickListener)
    }
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]

        // Bind the contact data to the view holder
        holder.bind(contact)

        // Set click listener for the item view
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ContactDetailActivity::class.java).apply {
                putExtra("name", contact.first)
                putExtra("phoneNumber", contact.second)
                val bitmap = contact.third
                if (bitmap != null) {
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()
                    putExtra("image", byteArray)
                }
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    inner class ContactViewHolder(itemView: View, private val itemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        private val contactImageView: ImageView = itemView.findViewById(R.id.contactImageView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val phoneNumberTextView: TextView = itemView.findViewById(R.id.phoneNumberTextView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val contact = contactList[position]
                    itemClickListener?.onItemClick(contact)
                }
            }
        }

        fun bind(contact: Triple<String, String, Bitmap?>) {
            nameTextView.text = contact.first
            phoneNumberTextView.text = contact.second

            if (contact.third != null) {
                val bitmap = contact.third as? Bitmap
                if (bitmap != null) {
                    val circularBitmap = ImageUtils.getCircularBitmap(bitmap)
                    contactImageView.setImageBitmap(circularBitmap)
                }
            } else {
                contactImageView.setImageResource(R.drawable.beats)
            }
        }
    }

    fun setContacts(contacts: ArrayList<Triple<String, String, Bitmap?>>) {
        contactList.clear()
        contactList.addAll(contacts)
        notifyDataSetChanged()
    }
}
