package com.example.finalprojectaibetter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import com.example.finalprojectaibetter.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpButton.setOnClickListener {
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString()
            val confirmPassword = binding.passwordConfirmed.text.toString()
            validateData(email, password, confirmPassword, firstName, lastName)
        }
    }

    private fun validateData(email: String, password: String, confirmPassword: String, firstName: String, lastName: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
        } else if(TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please reenter password", Toast.LENGTH_SHORT).show()
        } else if(password != confirmPassword) {
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
        } else if (password.length < 6) {
            Toast.makeText(this, "Password must have atleast 6 caracters", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(this, "First Name is empty", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, "Last Name is empty", Toast.LENGTH_SHORT).show()
        }
    }
}