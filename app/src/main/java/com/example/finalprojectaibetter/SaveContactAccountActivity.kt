package com.example.finalprojectaibetter

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.example.finalprojectaibetter.databinding.ActivitySaveContactAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SaveContactAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaveContactAccountBinding
    private var db = Firebase.firestore
    private val userId by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveContactAccountBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(binding.root)

            searchButton.setOnClickListener {
                val userId = userEditText.text.toString()
                db.collection("users")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            messageOnSuccess.apply {
                                visibility = View.VISIBLE
                            }
                            userFirstName.apply {
                                text = document.data["userFirstName"].toString()
                                visibility = View.VISIBLE
                            }
                        }
                        addContactButton.setOnClickListener {
                            for (document in documents) {
                                Log.d(TAG, "document data = ${document.data}")
                                addContact(document.data, document.id)
                                Handler(Looper.getMainLooper()).postDelayed({navigateTo(MainScreenActivity::class.java)} , 2000)
                                return@setOnClickListener
                            }
                        }
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Error getting documents: ", it)
                        messageOnFailure.apply {
                            visibility = View.VISIBLE
                            Handler(Looper.getMainLooper()).postDelayed({ visibility = View.GONE } , 4000)
                        }
                    }

                closeArrow.setOnClickListener {
                    navigateTo(MainScreenActivity::class.java)
                }
                addContactButton.setOnClickListener {
                    navigateTo(MainScreenActivity::class.java)
                }
            }
        }
    }
    private fun Context.navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
    }

    private fun addContact(user: MutableMap<String, Any>, documentId: String) {
        db.collection("users")
            .document(userId)
            .collection("Contact")
            .document(documentId)
            .set(user)
    }
}