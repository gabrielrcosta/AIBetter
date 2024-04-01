package com.example.finalprojectaibetter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.finalprojectaibetter.databinding.ActivityConfirmTransactionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
class ConfirmTransactionActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var binding: ActivityConfirmTransactionBinding
    private val fromUserId by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmTransactionBinding.inflate(layoutInflater)
        binding.apply {
            setContentView(binding.root)
            selectedUserTextView.text = getString(R.string.sending_amount, intent.getStringExtra("value") ?: "0")

            searchUserButton.setOnClickListener {
                val toUser = searchUserEditText.text.toString().trim()
                if (toUser.isEmpty()) return@setOnClickListener
                getUserId(toUser) { userId ->
                    if (userId == null) {
                        Toast.makeText(this@ConfirmTransactionActivity, "User not found", Toast.LENGTH_LONG).show()
                        return@getUserId
                    }
                    sendMoneyButton.setOnClickListener {
                        val amount = intent.getStringExtra("value")?.toDoubleOrNull() ?: return@setOnClickListener
                        transfer(fromUserId, userId, amount)
                        overlayText.text = getString(R.string.sent_amount_to, amount.toString(), toUser)
                        overlay.apply {
                            visibility = View.VISIBLE
                            Handler(Looper.getMainLooper()).postDelayed({ visibility = View.GONE
                                navigateTo(MainScreenActivity::class.java)} , 4000)
                        }
                    }
                }
            }
            closeArrow.setOnClickListener { navigateTo(TransferActivity::class.java) }
            }
        }
    private fun Context.navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
    }
    @SuppressLint("SetTextI18n")
    private fun getUserId(userName: String, onComplete: (String?) -> Unit) {
        db.collection("users")
            .whereEqualTo("userFirstName", userName)
            .get()
            .addOnSuccessListener {documents->
                for (document in documents) {
                    onComplete(document.id)
                    return@addOnSuccessListener
                }
                onComplete(null) //will be null if there is no user.
            }
            .addOnFailureListener {
                Log.w(TAG, "Error getting documents: ", it)
                onComplete(null) //in case of failure call it null.
            }
    }

    private fun transfer(fromUserId: String, toUserId: String, amount: Double) {
            val fromUserAccountRef = db.collection("users").document(fromUserId).collection("Account").document(fromUserId)
            val toUserAccountRef = db.collection("users").document(toUserId).collection("Account").document(toUserId)

            db.runTransaction { transaction ->
                val fromAccountSnapshot = transaction.get(fromUserAccountRef)
                val toAccountSnapshot = transaction.get(toUserAccountRef)
                Log.d(TAG, "run transaction method $fromAccountSnapshot $toAccountSnapshot")

                // Obter o saldo atual das contas
                val fromAccountBalance = fromAccountSnapshot.getDouble("balance") ?: error("Saldo da conta de origem não encontrado")
                val toAccountBalance = toAccountSnapshot.getDouble("balance") ?: error("Saldo da conta de destino não encontrado")
                Log.d(TAG, "$fromAccountBalance $toAccountBalance")

                // Verificar se a conta de origem tem saldo suficiente
                if (fromAccountBalance < amount) {
                    throw FirebaseFirestoreException("Saldo insuficiente", FirebaseFirestoreException.Code.ABORTED)
                }
                // Calcular os novos saldos
                val newFromAccountBalance = fromAccountBalance - amount
                val newToAccountBalance = toAccountBalance + amount

                // Atualizar os saldos das contas na transação
                transaction.update(fromUserAccountRef, "balance", newFromAccountBalance)
                transaction.update(toUserAccountRef, "balance", newToAccountBalance)

                // O retorno aqui pode ser usado para passar informações fora da transação, se necessário
                null // No valor de retorno necessário para transações do tipo void
            }
                .addOnSuccessListener {
                    Log.d(TAG, "Success")
                }
                .addOnFailureListener { e ->
                Log.e(TAG, "Transfer Failed", e)
            }
    }
}