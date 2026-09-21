package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast

object GooglePayUpiHelper {

    private const val TAG = "GooglePayUpiHelper"
    const val DEFAULT_SHOWROOM_VPA = "tileiq@okicici"
    const val DEFAULT_SHOWROOM_NAME = "TileIQ Showroom Corp"

    /**
     * Builds standard UPI Deep Link URI for Google Pay / PhonePe / Paytm / BHIM
     * Format: upi://pay?pa=VPA&pn=NAME&tr=TXN_REF&tn=NOTE&am=AMOUNT&cu=INR
     */
    fun buildUpiUri(
        payeeVpa: String = DEFAULT_SHOWROOM_VPA,
        payeeName: String = DEFAULT_SHOWROOM_NAME,
        amountINR: Double,
        transactionRefId: String,
        transactionNote: String
    ): Uri {
        val formattedAmount = String.format("%.2f", amountINR)
        val uriString = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", payeeVpa)
            .appendQueryParameter("pn", payeeName)
            .appendQueryParameter("tr", transactionRefId)
            .appendQueryParameter("tn", transactionNote)
            .appendQueryParameter("am", formattedAmount)
            .appendQueryParameter("cu", "INR")
            .build()
            .toString()

        return Uri.parse(uriString)
    }

    /**
     * Generates a dynamic QR Code image URL for scanning GPay / UPI payments on screen
     */
    fun getUpiQrCodeImageUrl(
        payeeVpa: String = DEFAULT_SHOWROOM_VPA,
        payeeName: String = DEFAULT_SHOWROOM_NAME,
        amountINR: Double,
        transactionNote: String
    ): String {
        val upiUri = buildUpiUri(
            payeeVpa = payeeVpa,
            payeeName = payeeName,
            amountINR = amountINR,
            transactionRefId = "TXN_${System.currentTimeMillis()}",
            transactionNote = transactionNote
        )
        val encodedUri = Uri.encode(upiUri.toString())
        return "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=$encodedUri"
    }

    /**
     * Launches Native Android Intent for Google Pay / UPI
     */
    fun launchGooglePayUpiIntent(
        context: Context,
        payeeVpa: String = DEFAULT_SHOWROOM_VPA,
        payeeName: String = DEFAULT_SHOWROOM_NAME,
        amountINR: Double,
        transactionRefId: String,
        transactionNote: String,
        targetApp: String = "Google Pay"
    ): Boolean {
        val uri = buildUpiUri(payeeVpa, payeeName, amountINR, transactionRefId, transactionNote)
        val intent = Intent(Intent.ACTION_VIEW, uri)

        if (targetApp.contains("Google Pay", ignoreCase = true)) {
            intent.setPackage("com.google.android.apps.nbu.paisa.user")
        } else if (targetApp.contains("PhonePe", ignoreCase = true)) {
            intent.setPackage("com.phonepe.app")
        } else if (targetApp.contains("Paytm", ignoreCase = true)) {
            intent.setPackage("net.one97.paytm")
        }

        return try {
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.w(TAG, "Target app $targetApp not installed or intent chooser required: ${e.message}")
            try {
                // Fallback to standard generic UPI intent chooser
                val genericIntent = Intent(Intent.ACTION_VIEW, uri)
                val chooser = Intent.createChooser(genericIntent, "Pay ₹${amountINR.toInt()} with Google Pay / UPI")
                context.startActivity(chooser)
                true
            } catch (ex: Exception) {
                Log.e(TAG, "No UPI apps installed on device: ${ex.message}")
                Toast.makeText(context, "Opening Google Pay UPI gateway simulation for container environment", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }
}
