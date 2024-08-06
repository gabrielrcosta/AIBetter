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
                val searchUserId = userEditText.text.toString()
                db.collection("users")
                    .whereEqualTo("userId", searchUserId)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            messageOnSuccess.visibility = View.GONE
                            messageOnFailure.apply {
                                text = "User not found."
                                visibility = View.VISIBLE
                            }
                        } else {
                            val document = documents.documents[0]
                            checkIfContactExists(document.id, document.data!!)
                        }
                    }
                    .addOnFailureListener {
                        messageOnSuccess.visibility = View.GONE
                        Log.d(TAG, "Error getting documents: ", it)
                        messageOnFailure.apply {
                            text = "Error finding user."
                            visibility = View.VISIBLE
                            Handler(Looper.getMainLooper()).postDelayed({ visibility = View.GONE }, 4000)
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
                        text = "User is already a contact."
                        visibility = View.VISIBLE
                        Handler(Looper.getMainLooper()).postDelayed({ visibility = View.GONE }, 4000)
                    }
                } else {
                    val userName = user["userFirstName"] as String
                    val lastName = user["userLastName"] as String
                    binding.messageOnSuccess.apply {
                        visibility = View.VISIBLE
                        text = "Would you like to add $userName $lastName as a contact?"
                    }
                    binding.addContactButton.setOnClickListener {
                        addContact(user, documentId)
                        Handler(Looper.getMainLooper()).postDelayed({ navigateTo(MainScreenActivity::class.java) }, 2000)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error checking if contact exists", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Error checking if contact exists: ", it)
            }
    }

    private fun addContact(user: MutableMap<String, Any>, documentId: String) {
        db.collection("users")
            .document(userId)
            .collection("Contact")
            .document(documentId)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error adding contact", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Error adding contact: ", it)
            }
    }
}
