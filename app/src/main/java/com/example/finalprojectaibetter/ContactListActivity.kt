package com.example.finalprojectaibetter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectaibetter.Model.User
import com.example.finalprojectaibetter.databinding.ActivityContactListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ContactListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactListBinding
    private lateinit var contactAdapter: ContactListAdapter
    private val db = Firebase.firestore
    private val userId by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    private var contactList: MutableList<User> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpClickListener()
        fetchContacts()
    }

    private fun setupRecyclerView(contactList: MutableList<User>) {
        contactAdapter = ContactListAdapter(contactList)
        binding.recyclerViewContactList.apply {
            layoutManager = LinearLayoutManager(this@ContactListActivity)
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
                Log.d(ContentValues.TAG, "profile = $profilePic") //getting images but link expired.
            }
            setupRecyclerView(contactList)
        }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error fetching users: ", exception)
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