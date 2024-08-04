package com.example.finalprojectaibetter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.finalprojectaibetter.databinding.ActivityTransferBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class TransferActivity : AppCompatActivity() {

    //Firebase Firestore database instance
    private val db = Firebase.firestore
    private lateinit var userId: String
    private lateinit var userName: String
    private lateinit var userLastName: String

    //Binding object to access the layout views
    private lateinit var binding: ActivityTransferBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Inflate the layout for this activity using binding
        binding = ActivityTransferBinding.inflate(layoutInflater)
        //Set the content view to the root of the layout
        setContentView(binding.root)

        // Get info from the user clicked on the previous screen.
        userId = intent.getStringExtra("USER_ID") ?: ""
        userName = intent.getStringExtra("USER_NAME") ?: ""
        userLastName = intent.getStringExtra("USER_LAST_NAME") ?: ""
        Log.d("ConfirmTransactionActivity", "Received userId: $userId, userName: $userName, userLastName: $userLastName")

        fetchUserAccountBalance()
        setupClickListener()

        //Set an onClick listener for the 'confirm transfer' button
        binding.transferConfirmButton.setOnClickListener {
            val value = binding.editText.text

            //Create an Intent to start the ConfirmTransactionActivity
            val intent = Intent(this, ConfirmTransactionActivity::class.java).apply {
                //Get the value from the editText and put it as an extra in the Intent
                putExtra("USER_ID", userId)
                putExtra("USER_NAME", userName)
                putExtra("USER_LAST_NAME", userLastName)
                putExtra("value", value.toString())
            }
            //Start the activity defined in the Intent
            startActivity(intent)
        }
    }
    //Setup click listeners for several UI elements
    private fun setupClickListener() {
        //Listener for the close arrow to finish the activity
        binding.closeArrow.setOnClickListener {
            finish()
        }
        //Listeners to navigate to different activities based on the icon clicked
        binding.homeBalanceIcon.setOnClickListener {
            navigateTo(MainScreenActivity::class.java)
        }
        binding.productsIcon.setOnClickListener {
            navigateTo(ProductsActivity::class.java)
        }
        binding.cardsIcon.setOnClickListener {
            navigateTo(CardManagerActivity::class.java)
        }
        binding.settingsIcon.setOnClickListener {
            navigateTo(SettingsActivity::class.java)
        }
    }
    //Help method to start an activity for the given class
    private fun Context.navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
    }
    @SuppressLint("SuspiciousIndentation")
    //Fetches the user's account balance from Firestore
    private fun fetchUserAccountBalance() {
        try {
            //Get the user ID from FirebaseAuth
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            //Query Firestore for the user's "Account" collection
            db.collection("users")
                .document(userId)
                .collection("Account")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        //Log the document ID and data
                        Log.d(ContentValues.TAG, "document ID ${document.id} => ${document.data}")
                        val balance = document.data["balance"]
                        Log.d(ContentValues.TAG, "balance ${balance.toString()}")
                        //Display the balance if it exists
                        balance?.let {
                            displayBalance(it.toString())
                        }
                    }
                }
                .addOnFailureListener{
                    //Log any errors the app might find
                    it.message?.let { it1 -> Log.d(ContentValues.TAG, it1) }
                }
        } catch (exception: Exception) {
            //Log any exceptions within the TAG = contentValue
            Log.d(ContentValues.TAG, "error exception = ${exception.message}")
        }
    }

    //Displays the user balance on the UI.
    @SuppressLint("SetTextI18n")
    private fun displayBalance(balance: String) {
        //update balance text on the text view
        binding.balanceTextAtt.text = "Your balance is: € $balance"
    }
}