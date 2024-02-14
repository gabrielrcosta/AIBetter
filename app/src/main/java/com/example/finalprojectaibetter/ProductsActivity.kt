package com.example.finalprojectaibetter

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ProductsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        val homeButton: ImageView = findViewById(R.id.home_balance_icon)
        val transferButton: ImageView = findViewById(R.id.tranfers_icon)
        val cardsButton: ImageView = findViewById(R.id.cards_icon)

        homeButton.setOnClickListener {
            navigateTo(MainScreenActivity::class.java)
        }

        transferButton.setOnClickListener {
            navigateTo(TransferActivity::class.java)
        }

        cardsButton.setOnClickListener {
            navigateTo(CardManagerActivity::class.java)
        }
    }
}

    private fun Context.navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }