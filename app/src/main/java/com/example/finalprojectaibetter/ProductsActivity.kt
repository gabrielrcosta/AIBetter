package com.example.finalprojectaibetter

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.finalprojectaibetter.databinding.ActivityProductsBinding

class ProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupClickListener()
    }
    private fun setupClickListener() {
        //class created to clear the onCreate function and pass only one call.
        binding.homeBalanceIcon.setOnClickListener {
            navigateTo(MainScreenActivity::class.java)
        }
        binding.tranfersIcon.setOnClickListener {
            navigateTo(TransferActivity::class.java)
        }
        binding.cardsIcon.setOnClickListener {
            navigateTo(CardManagerActivity::class.java)
        }
        binding.settingsIcon.setOnClickListener {
            navigateTo(SettingsActivity::class.java)
        }
        binding.budgetButton.setOnClickListener {
            navigateTo(BudgetForecastActivity::class.java)
        }
    }

    private fun Context.navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }
}