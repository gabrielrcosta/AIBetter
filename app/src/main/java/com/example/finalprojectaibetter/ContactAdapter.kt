package com.example.finalprojectaibetter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectaibetter.Model.User

class ContactAdapter(private val contactsList: MutableList<User>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstName: TextView = itemView.findViewById(R.id.first_name)
        val lastName: TextView = itemView.findViewById(R.id.last_name)
        val roundImageView : RoundImageView = itemView.findViewById(R.id.round_image_view)

        fun bind(user: User, listener: OnItemClickListener) {
            firstName.text = user.userFirstName
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
        holder.firstName.text = contact.userFirstName
        holder.lastName.text = contact.userLastName
        holder.roundImageView.loadImage(contact.profilePic)
    }

    override fun getItemCount(): Int = 3

    interface OnItemClickListener {
        fun onItemClick(user: User)
    }

}