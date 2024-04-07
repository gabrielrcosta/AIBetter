package com.example.finalprojectaibetter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.finalprojectaibetter.databinding.ActivityBudgetForecastBinding
import java.text.DecimalFormat

class BudgetForecastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetForecastBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closeArrow.setOnClickListener {
            navigateTo(ProductsActivity::class.java)
        }

        binding.calculateButton.setOnClickListener {
            val initial = binding.initialAmount.text.toString().toDouble()
            val monthlySavings = binding.monthlySavings.text.toString().toDouble()
            val forecastMonths = binding.forecastMonths.text.toString().toInt()
            val forecastResult = binding.forecastResult
            val annualInterestRate = 0.03
            val result = calculateForecastWithGrowth(initial, monthlySavings, forecastMonths, annualInterestRate)
            forecastResult.text = "Forecast with growth for $forecastMonths months: ${DecimalFormat("#,###.##").format(result)}"
        }
    }
    private fun calculateForecastWithGrowth(initial: Double, monthly: Double, months: Int, annualInterestRate: Double): Double {
        var total = initial
        val monthlyInterestRate = annualInterestRate / 12
        for (i in 1..months) {
            // Add monthly savings first
            total += monthly
            // Apply monthly interest
            total *= (1 + monthlyInterestRate)
        }
        return total
    }
    private fun Context.navigateTo(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }
}