package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotations")
data class QuotationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quotationNumber: String,
    val customerName: String,
    val customerPhone: String,
    val salespersonName: String,
    val itemsSummary: String, // e.g. "Statuario Gold 2x4 (30 boxes), Slate Grey 2x2 (12 boxes)"
    val totalAmountINR: Double,
    val discountINR: Double,
    val netAmountINR: Double,
    val status: String = "Draft", // "Draft", "Sent WhatsApp", "Confirmed", "Delivered"
    val timestampMs: Long = System.currentTimeMillis()
)
