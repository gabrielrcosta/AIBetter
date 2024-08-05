package com.example.finalprojectaibetter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojectaibetter.Model.User
import com.example.finalprojectaibetter.databinding.ActivityContactListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ContactListActivity : AppCompatActivity(), ContactListAdapter.OnContactItemClickListener {

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
        contactAdapter = ContactListAdapter(contactList, this)
        binding.recyclerViewContactList.apply {
            layoutManager = LinearLayoutManager(this@ContactListActivity)
            adapter = contactAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchContacts() {
        // get user data, converting database data in users
        db.collection("users").document(userId).collection("Contact").get().addOnSuccessListener { snapshot ->
            snapshot.forEach { document ->
                val userId = document.getString("userId") ?: "No User ID"
                val name = document.getString("userFirstName") ?: "No Name"
                val email = document.getString("email") ?: "No Email"
                val lastName = document.getString("userLastName") ?: "No Last Name"
                val profilePic = document.getString("profilePic") ?: "No Profile Pic"
                contactList.add(User(userId, name, lastName, email, profilePic))
            }
            setupRecyclerView(contactList)
        }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching users", Toast.LENGTH_SHORT).show()
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
    override fun onContactItemClick(contact: User) {
        val intent = Intent(this, TransferActivity::class.java).apply {
            putExtra("USER_ID", contact.userId)
            putExtra("USER_NAME", contact.userFirstName)
            putExtra("USER_LAST_NAME", contact.userLastName)
            Log.d(TAG, "${contact.userId} ${contact.userFirstName}" )
        }
        startActivity(intent)
    }
}