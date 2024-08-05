package com.example.finalprojectaibetter

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.finalprojectaibetter.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val auth = FirebaseAuth.getInstance()
    private var db = Firebase.firestore
    private val profilePic = "https://firebasestorage.googleapis.com/v0/b/aibetter.appspot.com/o/blank-profile-picture.jpg.webp?alt=media&token=9fcec946-627a-4603-9a16-9615fa0ac957"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signIn.setOnClickListener {
            navigateTo(Login::class.java)
        }

        binding.signUpButton.setOnClickListener {
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString()
            val confirmPassword = binding.passwordConfirmed.text.toString()

            if (validateData(email, password, confirmPassword, firstName, lastName)) {
                val userId = getRandomId()
                verifyRandomId(userId) { isUnique ->
                    if (isUnique) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    val newUser = hashMapOf(
                                        "userId" to userId,
                                        "userFirstName" to firstName,
                                        "userLastName" to lastName,
                                        "email" to email,
                                        "profilePic" to profilePic
                                    )
                                    db.collection("users").document(user?.uid.toString()).set(newUser)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "User Created")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w(TAG, "Error on performing new user registration", e)
                                        }
                                    val cardLastDigits = getCardLastDigit()
                                    val cardDetails = hashMapOf(
                                        "balance" to 10000,
                                        "cardLastDigit" to cardLastDigits,
                                        "cardPin" to "1234"
                                    )
                                    db.collection("users").document(user?.uid.toString()).collection("Account").document(user?.uid.toString()).set(cardDetails)
                                        .addOnSuccessListener {
                                            navigateTo(Login::class.java)
                                        }
                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "userId is not unique, please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getRandomId(): String {
        val randomId = (1 .. 999999).random()
        return String.format("%06d", randomId)
    }

    private fun getCardLastDigit(): String {
        val lastDigit =  (1..9999).random()
        return String.format("%04d", lastDigit)
    }

    private fun verifyRandomId(userId: String, onResult: (Boolean) -> Unit) {
        db.collection("users").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    //if not empty, the number already exists
                    onResult(false)
                } else {
                    //if empty, the number is good to go
                    onResult(true)
                }
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    private fun Context.navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
    }

    private fun validateData(email: String, password: String, confirmPassword: String, firstName: String, lastName: String): Boolean {
        return when {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(confirmPassword) -> {
                Toast.makeText(this, "Please reenter password", Toast.LENGTH_SHORT).show()
                false
            }
            password != confirmPassword -> {
                Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                false
            }
            password.length < 6 -> {
                Toast.makeText(this, "Password must have at least 6 characters", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(firstName) -> {
                Toast.makeText(this, "First Name is empty", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.isEmpty(lastName) -> {
                Toast.makeText(this, "Last Name is empty", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
}