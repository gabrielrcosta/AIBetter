package com.example.finalprojectaibetter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.finalprojectaibetter.databinding.ActivityUserDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserDetails : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailsBinding
    private val db = FirebaseFirestore.getInstance()
    private val userId by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListener()
        getUserDetails(userId)
    }

    private fun setupClickListener() {
        //Listeners to navigate to different activities based on the icon clicked
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
    private fun Context.navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
    }
    private fun getUserDetails(userId: String) {
        db
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener {
                val name = it.data?.get("userFirstName")
                val lastName = it.data?.get("userLastName")
                val email = it.data?.get("email")
                val accountId = it.data?.get("userId")
                val picture = it.data?.get("profilePic")
                setUserDetails(name.toString(), lastName.toString(), email.toString(), accountId.toString(), picture.toString())
            }
    }
    @SuppressLint("SetTextI18n")
    private fun setUserDetails(name: String, lastName: String, email: String, accountId: String, picture: String) {
        binding.firstName.text = name
        binding.lastName.text = lastName
        binding.email.text = email
        binding.userIdText.text = "Account ID: $accountId"
        binding.userImage.loadImage(picture)
    }
}