package com.example.finalprojectaibetter

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class MainScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        val productsIcon: ImageView = findViewById(R.id.products_icon)
        val transferButton: ImageView = findViewById(R.id.tranfers_icon)
        val cardsButton: ImageView = findViewById(R.id.cards_icon)

        productsIcon.setOnClickListener {
            navigateTo(ProductsActivity::class.java)
        }

        transferButton.setOnClickListener {
            navigateTo(TransferActivity::class.java)
        }

        cardsButton.setOnClickListener {
            navigateTo(CardManagerActivity::class.java)
        }
    }

    //Created the new function to replace 5 new functions for every click one new intent.
    private fun Context.navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }
}