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

class TransactionsAdapter(private var transactionList: List<Transaction>, private var listener: OnTransferItemClickListener) :
    RecyclerView.Adapter<TransactionsAdapter.ContactViewHolder>() {
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstName: TextView = itemView.findViewById(R.id.user_name)
        val dateOfTransaction: TextView = itemView.findViewById(R.id.transaction_date)
        val amountTransferred: TextView = itemView.findViewById(R.id.amount_sent)

        fun bind(user: Transaction, listener: OnTransferItemClickListener) {
            firstName.text = user.toUserName
            itemView.setOnClickListener {
                listener.onTransferItemClick(user)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_transfer, parent, false)
        return ContactViewHolder(view)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val transfer = transactionList[position]
        holder.dateOfTransaction.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(transfer.dateOfTransaction))
        holder.bind(user = transfer, listener)
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
    override fun getItemCount(): Int = transactionList.size

    interface OnTransferItemClickListener {
        fun onTransferItemClick(transferList: Transaction)
    }
}

