package com.example.finalprojectaibetter

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectaibetter.Model.Transaction
import com.example.finalprojectaibetter.databinding.ActivityStatementBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class StatementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatementBinding
    private val db = Firebase.firestore
    private val userId by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    private lateinit var adapter: StatementAdapter
    private var transactionList: MutableList<Transaction> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        adapter = StatementAdapter(listOf())
        binding.recyclerViewTransfers.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTransfers.adapter = adapter
        loadTransactions()
        setUpClickListener()
    }

    private fun loadTransactions() {
        db.collection("users")
            .document(userId)
            .collection("Transaction")
            .orderBy("dateOfTransaction", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                documents.forEach { document ->
                    val amount = document.getDouble("amount") ?: 0.0
                    val userId = document.getString("toUserId") ?: "No User ID"
                    val name = document.getString("toUserName") ?: "No Name"
                    val lastName = document.getString("toUserLastName") ?: "No Last Name"
                    val transferId = document.getString("transferId") ?: "No transfer ID"
                    val transactionType = document.getString("transactionType") ?: "SENT"
                    transactionList.add(
                        Transaction(
                            amount = amount,
                            dateOfTransaction = 0,
                            toUserId = userId,
                            toUserName = name,
                            toUserLastName = lastName,
                            transferId = transferId,
                            transactionType = transactionType
                        )
                    )
                }
                adapter = StatementAdapter(transactionList)  // Atualizar o adaptador com os dados
                binding.recyclerViewTransfers.adapter = adapter  // Configurar o adaptador no RecyclerView
            }
    }
    private fun Context.navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
    }
    private fun setUpClickListener() {
        binding.homeBalanceIcon.setOnClickListener {
            navigateTo(MainScreenActivity::class.java)
        }
        binding.productsIcon.setOnClickListener {
            navigateTo(ProductsActivity::class.java)
        }
        binding.cardsIcon.setOnClickListener {
            navigateTo(CardManagerActivity::class.java)
        }
        binding.settingsIcon.setOnClickListener {
            navigateTo(SettingsActivity::class.java)
        }
        binding.tranfersIcon.setOnClickListener {
            navigateTo(TransferActivity::class.java)
        }
    }
}