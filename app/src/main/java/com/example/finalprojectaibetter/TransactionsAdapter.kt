package com.example.finalprojectaibetter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectaibetter.Model.Transaction
import com.example.finalprojectaibetter.Model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionsAdapter(private var transactionList: List<Transaction>) :
    RecyclerView.Adapter<TransactionsAdapter.ContactViewHolder>() {
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstName: TextView = itemView.findViewById(R.id.user_name)
        val dateOfTransaction: TextView = itemView.findViewById(R.id.transaction_date)
        //val roundImageView : RoundImageView = itemView.findViewById(R.id.round_image_user)
        val amountTransferred: TextView = itemView.findViewById(R.id.amount_sent)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_transfer, parent, false)
        return ContactViewHolder(view)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val transfer = transactionList[position]
        holder.dateOfTransaction.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(transfer.dateOfTransaction))
        when (transfer.transactionType) {
            TransactionType.SENT.name -> {
                holder.firstName.text = "${transfer.toUserName} ${transfer.toUserLastName}"
                holder.amountTransferred.text = "-€${transfer.amount}"
            }
            TransactionType.RECEIVED.name -> {
                holder.firstName.text = "${transfer.toUserName} ${transfer.toUserLastName}"
                holder.amountTransferred.text = "+€${transfer.amount}"
            }
        }
    }
    override fun getItemCount(): Int = 3

    @SuppressLint("NotifyDataSetChanged")
    fun updateTransfer(list: List<Transaction>) {
        this.transactionList = list
        notifyDataSetChanged()
    }
}

