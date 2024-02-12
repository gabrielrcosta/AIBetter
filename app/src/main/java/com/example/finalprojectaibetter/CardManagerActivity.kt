package com.example.finalprojectaibetter

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class CardManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_manager)

        val homeButton: ImageView = findViewById(R.id.home_balance_icon)
        val productsIcon: ImageView = findViewById(R.id.products_icon)
        val transferButton: ImageView = findViewById(R.id.tranfers_icon)

        homeButton.setOnClickListener {
            navigateTo(MainScreenActivity::class.java)
        }
        productsIcon.setOnClickListener {
            navigateTo(ProductsActivity::class.java)
        }
        transferButton.setOnClickListener {
            navigateTo(TransferActivity::class.java)
        }
    }
    private fun Context.navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }
}