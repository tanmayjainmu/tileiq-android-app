package com.example.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    /**
     * Formats an amount in Indian Rupees (₹).
     * Example: 125000 -> "₹1,25,000"
     */
    fun formatINR(amount: Double, includeDecimals: Boolean = false): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        if (!includeDecimals) {
            format.maximumFractionDigits = 0
            format.minimumFractionDigits = 0
        } else {
            format.maximumFractionDigits = 2
            format.minimumFractionDigits = 2
        }
        val formatted = format.format(amount)
        // Ensure symbol is ₹
        return if (formatted.startsWith("INR") || formatted.startsWith("Rs.")) {
            "₹" + formatted.substring(3).trim()
        } else formatted
    }

    fun formatINR(amount: Int): String = formatINR(amount.toDouble())
    fun formatINR(amount: Long): String = formatINR(amount.toDouble())

    /**
     * Compact formatting in Indian numbering format: Lakhs (L) and Crores (Cr).
     * Example: 14500000 -> "₹1.45 Cr", 382000 -> "₹3.82 L"
     */
    fun formatINRCompact(amount: Double): String {
        return when {
            amount >= 10_000_000 -> { // 1 Crore
                val cr = amount / 10_000_000.0
                String.format(Locale("en", "IN"), "₹%.2f Cr", cr)
            }
            amount >= 100_000 -> { // 1 Lakh
                val lakh = amount / 100_000.0
                String.format(Locale("en", "IN"), "₹%.2f L", lakh)
            }
            else -> formatINR(amount)
        }
    }
}
