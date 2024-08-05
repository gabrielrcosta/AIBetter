package com.example.finalprojectaibetter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectaibetter.Model.User

class ContactListAdapter(
    private val contactList: MutableList<User>,
    private val listener: OnContactItemClickListener
) : RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder>() {
    class ContactListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstName: TextView = itemView.findViewById(R.id.first_name)
        val roundImageView : RoundImageView = itemView.findViewById(R.id.round_image_view)
        @SuppressLint("SetTextI18n")
        fun bind(user: User, listener: OnContactItemClickListener) {
            firstName.text = "${user.userFirstName} ${user.userLastName}"
            itemView.setOnClickListener {
                listener.onContactItemClick(user)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactListViewHolder, position: Int) {
        val contact = contactList[position]
        holder.firstName.text = contact.userFirstName
        holder.roundImageView.loadImage(contact.profilePic)
        holder.bind(contact, listener)
    }

    override fun getItemCount(): Int = contactList.size

    interface OnContactItemClickListener {
        fun onContactItemClick(contact: User)
    }
}