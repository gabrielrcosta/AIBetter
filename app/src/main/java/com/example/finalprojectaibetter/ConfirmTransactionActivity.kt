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

            userId = intent.getStringExtra("USER_ID") ?: ""
            userName = intent.getStringExtra("USER_NAME") ?: ""
            userLastName = intent.getStringExtra("USER_LAST_NAME") ?: ""
            Log.d("ConfirmTransactionActivity", "Received userId: $userId, userName: $userName, userLastName: $userLastName")

            val finalValue = intent.getStringExtra("value") ?: "0"
            selectedUserTextView.text = getString(R.string.sending_amount, finalValue, " ")

            if (userId.isNotEmpty() && userName.isNotEmpty() && userLastName.isNotEmpty()) {
                selectedUserTextView.text = getString(R.string.sending_amount, finalValue, userName)
                getUserById(userId) {idReceiver ->
                    if (idReceiver != null) {
                        Log.d("user Id agora?", idReceiver)
                        sendMoneyButton.setOnClickListener {
                            initiateTransferProcess(idReceiver, finalValue.toDoubleOrNull() ?: 0.0)
                            updateUIOnSuccess(userName, finalValue.toDouble())
                        }
                    }
                }
            } else {
                searchUserButton.setOnClickListener {
                    val toUserId = searchUserEditText.text.toString().trim()
                    if (toUserId.isEmpty()) {
                        Toast.makeText(this@ConfirmTransactionActivity, "Please enter a valid user Id.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    } else {
                        getUserById(toUserId) { receiver ->
                            if (receiver != null) {
                                Log.d("user Id agora?", receiver)
                                getUserName(receiver) { toUserName ->
                                    selectedUserTextView.text = getString(R.string.sending_amount, finalValue, toUserName)
                                    sendMoneyButton.setOnClickListener {
                                        if (toUserName != null) {
                                            initiateTransferProcess(receiver, finalValue.toDoubleOrNull() ?: 0.0)
                                            Log.d(TAG, "name: $toUserName")
                                            updateUIOnSuccess(userName = toUserName, amount = finalValue.toDouble())
                                        }
                                    }
                                }
                            } else {
                                selectedUserTextView.apply {
                                    postDelayed({
                                        text = "You are sending $finalValue"
                                    },3000)
                                    text = "User not Found."
                                }
                                Toast.makeText(this@ConfirmTransactionActivity, "User not found.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            closeArrow.setOnClickListener { finish() }  //finish() to close the current activity and return to the previous one
        }
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
                Log.d(TAG, "transactionSent: $transactionSent")

                getUserName(userId = fromUserId) { fromUserName ->
                    if (fromUserName != null) {
                        getUserLastName(userId = fromUserId) {fromUserLastName ->
                            if (fromUserLastName != null) {
                                val transactionReceived = Transaction(amount, currentTime, fromUserId, fromUserName, UUID.randomUUID().toString(), fromUserLastName, TransactionType.RECEIVED.name)
                                Log.d(TAG, "transactionReceived: $transactionReceived")
                                saveTransferReceived(transactionReceived, toUserId)
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
                Toast.makeText(this, "User not Found.", Toast.LENGTH_SHORT).show()
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
    private fun initiateTransferProcess(toUserId: String, amount: Double) {
        if (amount <= 0) {
            Toast.makeText(this, "Invalid amount.", Toast.LENGTH_LONG).show()
            Log.d(TAG, "amount: $amount")
            return
        } else {
            getUserLastName(toUserId) { toUserLastName ->
                Log.d(TAG, "toUserLastName: $toUserLastName")
                if (toUserLastName != null) {
                    getUserName(toUserId) {toUserName ->
                        if (toUserName != null) {
                            performTransaction(fromUserId, toUserId, amount, toUserName, toUserLastName)
                        }
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
                    throw FirebaseFirestoreException("Insuficient Funds", FirebaseFirestoreException.Code.ABORTED)
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
}