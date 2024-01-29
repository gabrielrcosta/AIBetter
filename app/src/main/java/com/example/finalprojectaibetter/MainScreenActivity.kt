package com.example.finalprojectaibetter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class MainScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        val closeButton: ImageView = findViewById(R.id.tranfers_icon)
        closeButton.setOnClickListener {
            openActivity()
        }
    }
    private fun openActivity() {
        val intent = Intent(this, TransferActivity::class.java)
        startActivity(intent)
        finish()
    }
}