package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_transactions")
data class PaymentTransactionEntity(
    @PrimaryKey val transactionId: String,
    val referenceId: String, // Invoice # or Quotation #
    val amountINR: Double,
    val gstINR: Double,
    val paymentType: String, // "Subscription Renewal", "Customer Quotation Advance"
    val paymentMethod: String, // "Google Pay (UPI)", "Razorpay NetBanking", etc.
    val payerUpiId: String,
    val payeeUpiId: String,
    val status: String, // "SUCCESS", "PENDING", "FAILED"
    val timestampMs: Long = System.currentTimeMillis(),
    val userEmail: String = ""
)
