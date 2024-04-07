package com.example.finalprojectaibetter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.finalprojectaibetter.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initializing binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        val firebaseAuth = FirebaseAuth.getInstance()

        //setting the click on button to log in
        binding.button.setOnClickListener{
            //getting the information from the edit texts
            val regNumber = binding.regNumber.text.toString()
            val password = binding.passwordEditable.text.toString()
            //if the is information we attempt to log in
            if (regNumber.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(regNumber, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //if successful I am getting the user Id to initiate in the Fire store.
                        val userId = firebaseAuth.currentUser?.uid?: ""
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(userId).get()
                            .addOnSuccessListener {
                                val intent = Intent(this, MainScreenActivity::class.java).apply {
                                    putExtra("USER_ID", userId)
                                }
                                startActivity(intent)
                                //Starting the MainScreenActivity with the created intent.
                                }
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        //in case the login and password does not match any in the database return the message.
                    }
                }
            }
        }
    }
}