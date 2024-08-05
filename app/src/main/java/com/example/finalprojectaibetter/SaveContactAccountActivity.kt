package com.example.finalprojectaibetter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.finalprojectaibetter.databinding.ActivitySaveContactAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SaveContactAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaveContactAccountBinding
    private var db = Firebase.firestore
    private val userId by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveContactAccountBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(binding.root)
            closeArrow.setOnClickListener {
                finish()
            }

            searchButton.setOnClickListener {
                val userId = userEditText.text.toString()
                db.collection("users")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            messageOnSuccess.visibility = View.GONE
                            messageOnFailure.apply {
                                visibility = View.VISIBLE
                                text = "User is already a contact."
                            }
                        } else {
                            messageOnFailure.visibility = View.GONE
                            for (document in documents) {
                                val userName = document.data["userFirstName"]
                                val lastName = document.data["userLastName"]
                                messageOnSuccess.apply {
                                    visibility = View.VISIBLE
                                    text = "Would you like to add $userName $lastName as a contact?"
                                }
                                addContactButton.setOnClickListener {
                                    checkIfContactExists(document.id, document.data)
                                    Log.d(TAG, "document data = ${document.data}")
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        messageOnSuccess.visibility = View.GONE
                        Log.d(TAG, "Error getting documents: ", it)
                        messageOnFailure.apply {
                            visibility = View.VISIBLE
                            Handler(Looper.getMainLooper()).postDelayed({ visibility = View.GONE } , 4000)
                        }
                    }
            }
        }
    }
    private fun Context.navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
    }

    @SuppressLint("SetTextI18n")
    private fun checkIfContactExists(documentId: String, user: MutableMap<String, Any>) {
        db.collection("users")
            .document(userId)
            .collection("Contact")
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.messageOnFailure.apply {
                        visibility = View.VISIBLE
                        Handler(Looper.getMainLooper()).postDelayed({ visibility = View.GONE } , 4000)
                    }
                } else {
                    addContact(user, documentId)
                    Handler(Looper.getMainLooper()).postDelayed({navigateTo(MainScreenActivity::class.java)} , 2000)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "User not Found", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Error checking if contact exists: ", it)
            }
    }

    private fun addContact(user: MutableMap<String, Any>, documentId: String) {
        db.collection("users")
            .document(userId)
            .collection("Contact")
            .document(documentId)
            .set(user)
    }
}