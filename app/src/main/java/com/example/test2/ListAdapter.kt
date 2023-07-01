package com.example.test2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class ListAdapter(context: Context?, userArrayList: ArrayList<User>) : ArrayAdapter<User?>(
    context!!, R.layout.list_item, userArrayList!! as List<User?>
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val user = getItem(position)
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }
        val imageView = convertView!!.findViewById<ImageView>(R.id.profile_pic)
        val userName = convertView.findViewById<TextView>(R.id.personName)
        val phoneNo = convertView.findViewById<TextView>(R.id.personPhonenumber)
        val info = convertView.findViewById<TextView>(R.id.personInfo)
        imageView.setImageResource(user!!.imageId)
        userName.text = user.name
        phoneNo.text = user.phoneNo
        info.text = user.info
        return convertView
    }
}