package com.example.finalprojectaibetter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class MainScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        val transferButton: ImageView = findViewById(R.id.tranfers_icon)
        transferButton.setOnClickListener {
            openActivity()
        }
        val cardsButton: ImageView = findViewById(R.id.cards_icon)
        cardsButton.setOnClickListener {
            openCardActivity()
        }
    }

    private fun openCardActivity() {
        val intent = Intent(this, CardManagerActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openActivity() {
        val intent = Intent(this, TransferActivity::class.java)
        startActivity(intent)
        finish()
    }
}