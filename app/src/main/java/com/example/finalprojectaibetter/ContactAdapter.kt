package com.example.finalprojectaibetter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectaibetter.Model.User

class ContactAdapter(private val contactsList: MutableList<User>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val firstName: TextView = itemView.findViewById(R.id.first_name)
        private val roundImageView : RoundImageView = itemView.findViewById(R.id.round_image_view)

        @SuppressLint("SetTextI18n")
        fun bind(user: User, listener: OnItemClickListener) {
            firstName.text = "${user.userFirstName} ${user.userLastName}"
            roundImageView.loadImage(user.profilePic)
            itemView.setOnClickListener {
                listener.onItemClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactsList[position]
        holder.bind(user = contact, listener)
    }

    override fun getItemCount(): Int = contactsList.size

    interface OnItemClickListener {
        fun onItemClick(user: User)
    }
}