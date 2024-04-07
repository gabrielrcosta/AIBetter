package com.example.finalprojectaibetter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.finalprojectaibetter.databinding.ActivityCardManagerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CardManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardManagerBinding
    private val db = FirebaseFirestore.getInstance()
    private val userId by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUserData(userId)
        getCardInfo(userId)
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.homeBalanceIcon.setOnClickListener {
            navigateTo(MainScreenActivity::class.java)
        }
        binding.productsIcon.setOnClickListener {
            navigateTo(ProductsActivity::class.java)
        }
        binding.tranfersIcon.setOnClickListener {
            navigateTo(TransferActivity::class.java)
        }
        binding.settingsIcon.setOnClickListener {
            navigateTo(SettingsActivity::class.java)
        }
        binding.freezeCard.setOnClickListener {
            val textViewVisibility = binding.frozenCardText
            if (textViewVisibility.visibility == View.VISIBLE) {
                textViewVisibility.visibility = View.INVISIBLE
            } else {
                textViewVisibility.visibility = View.VISIBLE
            }
        }
        binding.replaceCard.setOnClickListener {
            val overlay = binding.overlayLayout
            overlay.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                overlay.visibility = View.GONE
            },2000)
        }
        binding.showPin.setOnClickListener {
            val overlayPin = binding.pinOverlayLayout
            val pinShown = binding.pinShown
            val revealPinButton = binding.revealPinButton
            overlayPin.visibility = View.VISIBLE
            revealPinButton.setOnClickListener {
                getCardInfo(userId)
                pinShown.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    pinShown.visibility = View.INVISIBLE
                },4000)
            }
            overlayPin.setOnClickListener {
                overlayPin.visibility = View.GONE
            }
        }
        binding.cancelCard.setOnClickListener {
            val overlayCardCanceled = binding.cardCanceledOverlay
            overlayCardCanceled.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                overlayCardCanceled.visibility = View.GONE
            },4000)
        }
    }

    private fun Context.navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }

    private fun getCardInfo(userId: String) {
        db.collection("users")
            .document(userId)
            .collection("Account")
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.forEach { document ->
                    val cardLastDigit = document.data["cardLastDigit"]
                    val cardPin = document.data["cardPin"]
                    getCardPin(cardPin.toString())
                    setCardInfo(cardLastDigit.toString())
                }
            }
    }
    private fun getCardPin(pin: String) {
        binding.pinShown.text = pin
    }

    private fun setCardInfo(cardLastDigit: String) {
        binding.lastDigitsCard.text = cardLastDigit
    }

    private fun getUserData(userId: String) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val userName = snapshot.data?.get("userFirstName")
                val lastName = snapshot.data?.get("userLastName")
                setCardHolderName(userName.toString(), lastName.toString())
            }
    }
    @SuppressLint("SetTextI18n")
    private fun setCardHolderName(userName: String, lastName: String) {
        binding.cardHolderName.text = "$userName $lastName"
    }
}