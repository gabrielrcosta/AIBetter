package com.example.finalprojectaibetter

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectaibetter.Model.Transaction
import com.example.finalprojectaibetter.Model.TransactionType
import com.example.finalprojectaibetter.databinding.ActivityConfirmTransactionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class ConfirmTransactionActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var binding: ActivityConfirmTransactionBinding
    private val fromUserId by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmTransactionBinding.inflate(layoutInflater).apply {
            setContentView(root)
            val finalValue = intent.getStringExtra("value") ?: "0"
            selectedUserTextView.text = getString(R.string.sending_amount, finalValue, " ")

            searchUserButton.setOnClickListener {
                val toUserName = searchUserEditText.text.toString().trim()
                if (toUserName.isEmpty()) {
                    Toast.makeText(this@ConfirmTransactionActivity, "Please enter a username.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                selectedUserTextView.text = getString(R.string.sending_amount, finalValue, toUserName)
                sendMoneyButton.setOnClickListener {
                    initiateTransferProcess(toUserName, finalValue.toDoubleOrNull() ?: 0.0)
                    Log.d(TAG, "name: $toUserName")
                    updateUIOnSuccess(userName = toUserName, amount = finalValue.toDouble())
                }
            }
            closeArrow.setOnClickListener { finish() }  //finish() to close the current activity and return to the previous one
        }
    }
    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
    }

    private fun getUserIdByName(userName: String, onComplete: (String?) -> Unit) {
        db.collection("users").whereEqualTo("userFirstName", userName).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    onComplete(document.id)
                    return@addOnSuccessListener
                }
                onComplete(null)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting documents: $e", e)
                onComplete(null)
            }
    }
    private fun performTransaction(fromUserId: String, toUserId: String, amount: Double, userName: String, userLastName: String) {
        transfer(fromUserId, toUserId, amount) { isSuccess ->
            if (isSuccess) {
                val transactionSent = Transaction(amount, System.currentTimeMillis(), toUserId, userName, UUID.randomUUID().toString(), userLastName, TransactionType.SENT.name)
                Log.d(TAG, "transactionSent: $transactionSent")
                getUserName(userId = fromUserId) { fromUserName ->
                    if (fromUserName != null) {
                        getUserLastName(userId = fromUserId) {fromUserLastName ->
                            if (fromUserLastName != null) {
                                val transactionReceived = Transaction(amount, System.currentTimeMillis(), fromUserId, fromUserName, UUID.randomUUID().toString(), fromUserLastName, TransactionType.RECEIVED.name)
                                Log.d(TAG, "transactionReceived: $transactionReceived")
                                saveTransferSent(transactionSent)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Failed to transfer funds.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getUserName(userId: String, onComplete: (String?) -> Unit) {
        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            val userName = document.getString("userFirstName")
            if (userName.isNullOrEmpty()) {
                Log.d(TAG, "No user found for user ID $userId")
            } else {
                onComplete(userName)
            }
        }.addOnFailureListener {e->
            Log.e(TAG, "Failed to fetch user name for ID $userId: ${e.message}", e)
            onComplete(null)
        }
    }

    private fun getUserLastName(userId: String, onComplete: (String?) -> Unit) {
        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            val lastName = document.getString("userLastName")
            if (lastName.isNullOrEmpty()) {
                Log.d(TAG, "No last name found for user ID $userId")
                onComplete(null)
            } else {
                onComplete(lastName)
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to fetch user last name for ID $userId: ${e.message}", e)
            onComplete(null)
        }
    }
    private fun initiateTransferProcess(toUserName: String, amount: Double) {
        if (amount <= 0) {
            Toast.makeText(this, "Invalid amount.", Toast.LENGTH_LONG).show()
            Log.d(TAG, "amount: $amount")
            return
        }
        getUserIdByName(toUserName) { toUserId ->
            Log.d(TAG, "toUserId: $toUserId")
            if (toUserId == null) {
                Toast.makeText(this, "User not found.", Toast.LENGTH_LONG).show()
                return@getUserIdByName
            } else {
                getUserLastName(toUserId) { toUserLastName ->
                    Log.d(TAG, "toUserLastName: $toUserLastName")
                    if (toUserLastName != null) {
                        performTransaction(fromUserId, toUserId, amount, toUserName, toUserLastName)
                    }
                }
            }
        }
    }
    private fun updateUIOnSuccess(userName: String, amount: Double) {
        binding.overlayText.text = getString(R.string.sent_amount_to, amount.toString(), userName)
        binding.overlay.apply {
            visibility = View.VISIBLE
            postDelayed({
                visibility = View.GONE
                navigateTo(MainScreenActivity::class.java)
            }, 2000)
        }
    }
    private fun transfer(fromUserId: String, toUserId: String, amount: Double, onComplete: (Boolean) -> Unit) {
            val fromUserAccountRef = db.collection("users").document(fromUserId).collection("Account").document(fromUserId)
            val toUserAccountRef = db.collection("users").document(toUserId).collection("Account").document(toUserId)

            db.runTransaction { transaction ->
                val fromAccountSnapshot = transaction.get(fromUserAccountRef)
                val toAccountSnapshot = transaction.get(toUserAccountRef)
                Log.d(TAG, "run transaction method $fromAccountSnapshot $toAccountSnapshot")

                //getting the balance of the accounts
                val fromAccountBalance = fromAccountSnapshot.getDouble("balance") ?: error("Balance of sendind account not found")
                val toAccountBalance = toAccountSnapshot.getDouble("balance") ?: error("Balance not found")
                Log.d(TAG, "$fromAccountBalance $toAccountBalance")

                //check if the account has enough balance
                if (fromAccountBalance < amount) {
                    throw FirebaseFirestoreException("Saldo insuficiente", FirebaseFirestoreException.Code.ABORTED)
                }
                //calculate new balance
                val newFromAccountBalance = fromAccountBalance - amount
                val newToAccountBalance = toAccountBalance + amount

                //Att the balance of the involved accounts
                transaction.update(fromUserAccountRef, "balance", newFromAccountBalance)
                transaction.update(toUserAccountRef, "balance", newToAccountBalance)

                null
            }
                .addOnSuccessListener {
                    Log.d(TAG, "Success")
                    onComplete(true) //callback success
                }
                .addOnFailureListener { e ->
                Log.e(TAG, "Transfer Failed", e)
                    onComplete(false) //callback failed
            }
    }
    private fun saveTransferSent(transaction: Transaction) {
        val transferMap = hashMapOf(
            "transferId" to transaction.transferId,
            "toUserName" to transaction.toUserName,
            "toUserId" to transaction.toUserId,
            "amount" to transaction.amount,
            "dateOfTransaction" to transaction.dateOfTransaction,
            "toUserLastName" to transaction.toUserLastName,
            "transactionType" to transaction.transactionType
        )
        db.collection("users")
            .document(fromUserId)
            .collection("Transaction")
            .add(transferMap)
            .addOnSuccessListener { document ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${document.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document", exception)
            }
    }
}