package com.example.finalprojectaibetter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectaibetter.Model.Transaction
import com.example.finalprojectaibetter.Model.User
import com.example.finalprojectaibetter.databinding.ActivityMainScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.lang.Exception

class MainScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainScreenBinding
    private lateinit var contactAdapter: ContactAdapter
    private var contactList: MutableList<User> = mutableListOf()
    private var transactionList: MutableList<Transaction> = mutableListOf()
    private val db = FirebaseFirestore.getInstance()
    private val userId by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e(TAG, "No user logged in")
            startActivity(Intent(this, MainActivity::class.java)) // Redirecione para a tela de login
            finish()
            return
        }

        fetchContacts()
        fetchTransfer()
        fetchUserAccountBalance()
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.addContact.setOnClickListener {
            navigateTo(SaveContactAccountActivity::class.java)
        }
        binding.productsIcon.setOnClickListener {
            navigateTo(ProductsActivity::class.java)
        }
        binding.tranfersIcon.setOnClickListener {
            navigateTo(TransferActivity::class.java)
        }
        binding.cardsIcon.setOnClickListener {
            navigateTo(CardManagerActivity::class.java)
        }
        binding.settingsIcon.setOnClickListener {
            navigateTo(SettingsActivity::class.java)
        }
        binding.recentTransactions.setOnClickListener {
            navigateTo(StatementActivity::class.java)
        }
        binding.contactsText.setOnClickListener {
            navigateTo(ContactListActivity::class.java)
        }
    }

    //Created the new function to replace 5 new functions for every click one new intent.
    private fun Context.navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
    }

    private fun setupTransfersRecyclerView(transactionList: List<Transaction>) {
        val transactionsAdapter = TransactionsAdapter(transactionList)
        binding.recyclerRecentTransfers.layoutManager = LinearLayoutManager(this@MainScreenActivity)
        binding.recyclerRecentTransfers.adapter = transactionsAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchTransfer() {
        // Aqui você busca os dados das transferencias, convertendo os documentos do Firestore em objetos User
        db.collection("users")
            .document(userId)
            .collection("Transaction")
            .orderBy("dateOfTransaction", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
            snapshot.forEach { document ->
                Log.d(TAG,"data= ${document.data}")
                val amount = document.getDouble("amount") ?: 0.0
                val userId = document.getString("toUserId") ?: "No User ID"
                val name = document.getString("toUserName") ?: "No Name"
                val lastName = document.getString("toUserLastName") ?: "No Last Name"
                val transferId = document.getString("transferId") ?: "No transfer ID"
                val transactionType = document.getString("transactionType") ?: "SENT"
                transactionList.add(
                    Transaction(amount = amount, dateOfTransaction = 0, toUserId = userId,
                    toUserName = name, toUserLastName = lastName, transferId = transferId, transactionType = transactionType)
                )
            }
            setupTransfersRecyclerView(transactionList)
            }.addOnFailureListener { exception ->
            Log.e(TAG, "Error fetching users: ", exception)
        }
    }

    private fun setupRecyclerView(contactList: List<User>) {
        contactAdapter = ContactAdapter(contactList)
        binding.recyclerContacts.apply {
            layoutManager = LinearLayoutManager(this@MainScreenActivity)
            adapter = contactAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchContacts() {
        // Aqui você busca os dados dos usuários, convertendo os documentos do Firestore em objetos User
        db.collection("users").document(userId).collection("Contact").get().addOnSuccessListener { snapshot ->
            snapshot.forEach { document ->
                val userId = document.getString("userId") ?: "No User ID"
                val name = document.getString("userFirstName") ?: "No Name"
                val email = document.getString("email") ?: "No Email"
                val lastName = document.getString("userLastName") ?: "No Last Name"
                val accountId = document.getString("accountId") ?: "No account ID"
                val profilePic = document.getString("profilePic") ?: "No Profile Pic"
                contactList.add(User(userId, name, lastName, email, accountId, profilePic))
                Log.d(TAG, "profile = $profilePic") //getting images but link expired.
            }
            setupRecyclerView(contactList)
        }
            .addOnFailureListener { exception ->
            Log.e(TAG, "Error fetching users: ", exception)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun fetchUserAccountBalance() {
            try {
                    db.collection("users")
                        .document(userId)
                        .collection("Account")
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                Log.d(TAG, "document ID ${document.id} => ${document.data}")
                                val balance = document.data["balance"]
                                Log.d(TAG, "balance ${balance.toString()}")
                                balance?.let {
                                    displayBalance(it.toString())
                                }
                            }
                        }
                        .addOnFailureListener{
                            it.message?.let { it1 -> Log.d(TAG, it1) }
                        }
            } catch (exception: Exception) {
                Log.d(TAG, "error exception = ${exception.message}")
            }
    }
    private fun displayBalance(balance: String) {
        binding.balanceValue.text = balance
    }
}