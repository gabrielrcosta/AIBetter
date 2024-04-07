package com.example.finalprojectaibetter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectaibetter.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListener()
    }

    //class created to clear the onCreate area.
    // As this activity does not have any functionality other than clicks
    private fun setupClickListener() {
        binding.btnContactSupport.setOnClickListener {
            val cardView = binding.cardAibContact
            if (cardView.visibility == View.VISIBLE) {
                binding.cardAibContact.visibility = View.GONE
            } else {
                cardView.visibility = View.VISIBLE
            }
        }

        binding.btnMessages.setOnClickListener {
            val cardView = binding.cardAibMessage
            if (cardView.visibility == View.VISIBLE) {
                binding.cardAibMessage.visibility = View.GONE
            } else {
                cardView.visibility = View.VISIBLE
            }
        }

        binding.homeBalanceIcon.setOnClickListener {
            navigateTo(MainScreenActivity::class.java)
        }
        binding.productsIcon.setOnClickListener {
            navigateTo(ProductsActivity::class.java)
        }
        binding.tranfersIcon.setOnClickListener {
            navigateTo(TransferActivity::class.java)
        }
        binding.cardsIcon.setOnClickListener {
            navigateTo(CardManagerActivity::class.java)
        }
    }
    private fun Context.navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }
}
