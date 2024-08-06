package com.example.finalprojectaibetter

import android.annotation.SuppressLint
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
    private lateinit var userId: String
    private lateinit var userName: String
    private lateinit var userLastName: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmTransactionBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        initializeUI()
    }

    private fun initializeUI() {
        userId = intent.getStringExtra("USER_ID") ?: ""
        userName = intent.getStringExtra("USER_NAME") ?: ""
        userLastName = intent.getStringExtra("USER_LAST_NAME") ?: ""
        Log.d(TAG, "Received userId: $userId, userName: $userName, userLastName: $userLastName")

        val finalValue = intent.getStringExtra("value") ?: "0"
        binding.selectedUserTextView.text = getString(R.string.sending_amount, finalValue, " ")

        if (userId.isNotEmpty() && userName.isNotEmpty() && userLastName.isNotEmpty()) {
            binding.selectedUserTextView.text = getString(R.string.sending_amount, finalValue, userName)
            getUserById(userId) { idReceiver ->
                if (idReceiver != null) {
                    Log.d(TAG, "User ID: $idReceiver")
                    binding.sendMoneyButton.setOnClickListener {
                        initiateTransferProcess(idReceiver, finalValue.toDoubleOrNull() ?: 0.0)
                        updateUIOnSuccess(userName, finalValue.toDouble())
                    }
                }
            }
        } else {
            setupSearchUser(finalValue)
        }
        binding.closeArrow.setOnClickListener { finish() }
    }

    private fun setupSearchUser(finalValue: String) {
        binding.searchUserButton.setOnClickListener {
            val toUserId = binding.searchUserEditText.text.toString().trim()
            if (toUserId.isEmpty()) {
                showToast("Please enter a valid user Id.")
            } else {
                getUserById(toUserId) { receiver ->
                    if (receiver != null) {
                        Log.d(TAG, "User ID: $receiver")
                        getUserName(receiver) { toUserName ->
                            if (toUserName != null) {
                                binding.selectedUserTextView.text = getString(R.string.sending_amount, finalValue, toUserName)
                                binding.sendMoneyButton.setOnClickListener {
                                    initiateTransferProcess(receiver, finalValue.toDoubleOrNull() ?: 0.0)
                                    updateUIOnSuccess(toUserName, finalValue.toDouble())
                                }
                            }
                        }
                    } else {
                        showUserNotFoundMessage(finalValue)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showUserNotFoundMessage(finalValue: String) {
        binding.selectedUserTextView.apply {
            postDelayed({
                text = "You are sending $finalValue"
            }, 3000)
            text = "User not Found."
        }
        showToast("User not found.")
    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
    }

    private fun getUserById(userId: String, onComplete: (String?) -> Unit) {
        db.collection("users").whereEqualTo("userId", userId).get()
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
                val currentTime = System.currentTimeMillis()
                val transactionSent = Transaction(amount, currentTime, toUserId, userName, UUID.randomUUID().toString(), userLastName, TransactionType.SENT.name)
                saveTransferSent(transactionSent)
                Log.d(TAG, "Transaction Sent: $transactionSent")

                getUserName(fromUserId) { fromUserName ->
                    fromUserName?.let {
                        getUserLastName(fromUserId) { fromUserLastName ->
                            fromUserLastName?.let {
                                val transactionReceived = Transaction(amount, currentTime, fromUserId, fromUserName, UUID.randomUUID().toString(), fromUserLastName, TransactionType.RECEIVED.name)
                                Log.d(TAG, "Transaction Received: $transactionReceived")
                                saveTransferReceived(transactionReceived, toUserId)
                            }
                        }
                    }
                }
            } else {
                showToast("Failed to transfer funds.")
            }
        }
    }

    private fun getUserName(userId: String, onComplete: (String?) -> Unit) {
        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            val userName = document.getString("userFirstName")
            if (userName.isNullOrEmpty()) {
                showToast("User not Found.")
                Log.d(TAG, "No user found for user ID $userId")
            } else {
                onComplete(userName)
            }
        }.addOnFailureListener { e ->
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

    private fun initiateTransferProcess(toUserId: String, amount: Double) {
        if (amount <= 0) {
            showToast("Invalid amount.")
            Log.d(TAG, "Amount: $amount")
            return
        }

        getUserLastName(toUserId) { toUserLastName ->
            toUserLastName?.let {
                getUserName(toUserId) { toUserName ->
                    toUserName?.let {
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
            Log.d(TAG, "Transaction snapshots: $fromAccountSnapshot $toAccountSnapshot")

            val fromAccountBalance = fromAccountSnapshot.getDouble("balance") ?: error("Balance of sending account not found")
            val toAccountBalance = toAccountSnapshot.getDouble("balance") ?: error("Balance not found")
            Log.d(TAG, "Balances: $fromAccountBalance $toAccountBalance")

            if (fromAccountBalance < amount) {
                throw FirebaseFirestoreException("Insufficient Funds", FirebaseFirestoreException.Code.ABORTED)
            }

            val newFromAccountBalance = fromAccountBalance - amount
            val newToAccountBalance = toAccountBalance + amount

            transaction.update(fromUserAccountRef, "balance", newFromAccountBalance)
            transaction.update(toUserAccountRef, "balance", newToAccountBalance)

            null
        }
            .addOnSuccessListener {
                Log.d(TAG, "Transaction successful")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Transaction failed", e)
                onComplete(false)
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

    private fun saveTransferReceived(transaction: Transaction, toUserId: String) {
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
            .document(toUserId)
            .collection("Transaction")
            .add(transferMap)
            .addOnSuccessListener { document ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${document.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document", exception)
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
