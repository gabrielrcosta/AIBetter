package com.example.finalprojectaibetter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class TransferActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        val closeButton: ImageView = findViewById(R.id.close_arrow)
        closeButton.setOnClickListener {
            openActivity()
        }
    }
    private fun openActivity() {
        val intent = Intent(this, MainScreenActivity::class.java)
        startActivity(intent)
        finish()
    }
}