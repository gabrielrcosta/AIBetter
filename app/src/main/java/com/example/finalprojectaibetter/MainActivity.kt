package com.example.finalprojectaibetter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.finalprojectaibetter.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initializing binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        binding.button.setOnClickListener{
            /*val regNumber = binding.regNumber.text.toString()
            val password = binding.passwordEditable.text.toString()

            if (regNumber.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(regNumber, password).addOnCompleteListener {
                    if (it.isSuccessful) {*/
                        val intent = Intent(this, MainScreenActivity::class.java)
                        startActivity(intent)
                    /*} else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }*/
        }
    }
        /*override fun onStart() {
            super.onStart()
            //checking if user is logged in or not and if not, loading logIn page.
            if (firebaseAuth.currentUser != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }*/
}