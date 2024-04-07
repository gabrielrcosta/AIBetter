package com.example.finalprojectaibetter.Model
data class Transaction(var amount: Double = 1.0,
                       var dateOfTransaction: Long = 11,
                       var toUserId: String = "",
                       var toUserName: String = "",
                       var transferId: String = "",
                       var toUserLastName: String = "",
                       var transactionType: String = ""
)
enum class TransactionType {
    SENT, RECEIVED
}