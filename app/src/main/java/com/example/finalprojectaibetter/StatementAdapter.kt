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

class StatementAdapter(private val transactionsList: List<Transaction>) :
RecyclerView.Adapter<StatementAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.textViewDate)
        val transactionId: TextView = view.findViewById(R.id.textViewTransactionId)
        val recipient: TextView = view.findViewById(R.id.textViewRecipient)
        val amount: TextView = view.findViewById(R.id.textViewValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_statement, parent, false)
        return TransactionViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionsList[position]
        holder.date.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(transaction.dateOfTransaction))
        holder.transactionId.text = transaction.transferId
        when (transaction.transactionType) {
            TransactionType.SENT.name -> {
                holder.recipient.text = "${transaction.toUserName} ${transaction.toUserLastName}"
                holder.amount.text = "-€${transaction.amount}"
            }
            TransactionType.RECEIVED.name -> {
                holder.recipient.text = "From: ${transaction.toUserName} ${transaction.toUserLastName}"
                holder.amount.text = "+€${transaction.amount}"
            }
        }
    }

    override fun getItemCount(): Int = transactionsList.size
}
