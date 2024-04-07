package com.example.finalprojectaibetter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectaibetter.Model.User

class ContactListAdapter(private val contactList: MutableList<User>) :
        RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder>() {
            class ContactListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                val firstName: TextView = itemView.findViewById(R.id.first_name)
                val lastName: TextView = itemView.findViewById(R.id.last_name)
                val roundImageView : RoundImageView = itemView.findViewById(R.id.round_image_view)

            }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactListViewHolder, position: Int) {
        val contact = contactList[position]
        holder.firstName.text = contact.userFirstName
        holder.lastName.text = contact.userLastName
        holder.roundImageView.loadImage(contact.profilePic)
    }

    override fun getItemCount(): Int = contactList.size
}