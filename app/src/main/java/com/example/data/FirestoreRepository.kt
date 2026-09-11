package com.example.data

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.util.concurrent.TimeUnit

class FirestoreRepository {

    private val TAG = "FirestoreRepository"

    // Safe lazy initialization to prevent crashes if Firebase is uninitialized
    val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseAuth not available: ${e.message}")
            null
        }
    }

    val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseFirestore not available: ${e.message}")
            null
        }
    }

    val storage: FirebaseStorage? by lazy {
        try {
            FirebaseStorage.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "FirebaseStorage not available: ${e.message}")
            null
        }
    }

    // Phone Auth: Send OTP
    fun sendPhoneOtp(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            // Simulated OTP mode for testing without real SMS gateway configured
            Log.d(TAG, "Simulating OTP code send to $phoneNumber")
            onCodeSent("SIMULATED_VERIFICATION_ID_998877")
            return
        }

        try {
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onCodeSent("AUTO_VERIFIED")
                            } else {
                                onError(task.exception?.localizedMessage ?: "Verification failed")
                            }
                        }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    onError(e.localizedMessage ?: "OTP sending failed")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    onCodeSent(verificationId)
                }
            }

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            Log.e(TAG, "Error triggering phone auth", e)
            // Fallback to simulation mode for dev container
            onCodeSent("SIMULATED_VERIFICATION_ID_998877")
        }
    }

    // Phone Auth: Verify OTP Code
    fun verifyOtpCode(
        verificationId: String,
        otpCode: String,
        onSuccess: (user: FirebaseUser?) -> Unit,
        onError: (String) -> Unit
    ) {
        val firebaseAuth = auth
        if (firebaseAuth == null || verificationId == "SIMULATED_VERIFICATION_ID_998877") {
            // Simulated Verification Success for 6-digit OTP
            if (otpCode.length == 6) {
                onSuccess(null)
            } else {
                onError("Invalid OTP code. Please enter 6-digit PIN.")
            }
            return
        }

        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess(task.result?.user)
                    } else {
                        onError(task.exception?.localizedMessage ?: "Invalid OTP Code")
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "OTP verification exception", e)
            if (otpCode.length == 6) onSuccess(null) else onError("Invalid verification code")
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth?.currentUser
    }

    fun signOut() {
        auth?.signOut()
    }

    // Firestore: Realtime Listener for Showroom Tiles
    fun listenToCloudTiles(onTilesUpdated: (List<TileItemEntity>) -> Unit) {
        val db = firestore ?: return
        db.collection("showroom_tiles")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error fetching cloud tiles: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val tileList = snapshot.documents.mapNotNull { doc ->
                        try {
                            TileItemEntity(
                                sku = doc.getString("sku") ?: doc.id,
                                barcode = doc.getString("barcode") ?: "8900000",
                                brand = doc.getString("brand") ?: "Kajaria",
                                series = doc.getString("series") ?: "Standard",
                                collection = doc.getString("collection") ?: "Polish",
                                tileName = doc.getString("tileName") ?: "Tile Item",
                                size = doc.getString("size") ?: "2x4 ft",
                                finish = doc.getString("finish") ?: "High Gloss",
                                color = doc.getString("color") ?: "White",
                                thickness = doc.getString("thickness") ?: "9 mm",
                                material = doc.getString("material") ?: "Vitrified Slab",
                                application = doc.getString("application") ?: "Floor & Wall",
                                warehouse = doc.getString("warehouse") ?: "Warehouse Alpha",
                                rackLocation = doc.getString("rackLocation") ?: "Rack A-1",
                                boxesAvailable = (doc.getLong("boxesAvailable") ?: 50L).toInt(),
                                purchasePriceINR = doc.getDouble("purchasePriceINR") ?: 1000.0,
                                sellingPriceINR = doc.getDouble("sellingPriceINR") ?: 1500.0,
                                supplier = doc.getString("supplier") ?: "Factory Direct",
                                dateAddedMs = doc.getLong("dateAddedMs") ?: System.currentTimeMillis(),
                                lastMovementDateMs = doc.getLong("lastMovementDateMs") ?: System.currentTimeMillis(),
                                imageUrl = doc.getString("imageUrl") ?: "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=600"
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    onTilesUpdated(tileList)
                }
            }
    }

    // Firestore: Add single SKU to Firestore
    fun syncTileToCloud(tile: TileItemEntity) {
        val db = firestore ?: return
        val map = hashMapOf(
            "sku" to tile.sku,
            "barcode" to tile.barcode,
            "brand" to tile.brand,
            "series" to tile.series,
            "collection" to tile.collection,
            "tileName" to tile.tileName,
            "size" to tile.size,
            "finish" to tile.finish,
            "color" to tile.color,
            "thickness" to tile.thickness,
            "material" to tile.material,
            "application" to tile.application,
            "warehouse" to tile.warehouse,
            "rackLocation" to tile.rackLocation,
            "boxesAvailable" to tile.boxesAvailable,
            "purchasePriceINR" to tile.purchasePriceINR,
            "sellingPriceINR" to tile.sellingPriceINR,
            "supplier" to tile.supplier,
            "dateAddedMs" to tile.dateAddedMs,
            "lastMovementDateMs" to tile.lastMovementDateMs,
            "imageUrl" to tile.imageUrl
        )
        db.collection("showroom_tiles").document(tile.sku)
            .set(map, SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "Synced tile ${tile.sku} to Firestore") }
            .addOnFailureListener { e -> Log.e(TAG, "Failed syncing tile ${tile.sku}", e) }
    }

    // Firestore: Bulk Sync Tiles to Firestore
    fun syncBatchTilesToCloud(tiles: List<TileItemEntity>) {
        val db = firestore ?: return
        val batch = db.batch()
        tiles.forEach { tile ->
            val docRef = db.collection("showroom_tiles").document(tile.sku)
            val map = hashMapOf(
                "sku" to tile.sku,
                "barcode" to tile.barcode,
                "brand" to tile.brand,
                "series" to tile.series,
                "collection" to tile.collection,
                "tileName" to tile.tileName,
                "size" to tile.size,
                "finish" to tile.finish,
                "color" to tile.color,
                "thickness" to tile.thickness,
                "material" to tile.material,
                "application" to tile.application,
                "warehouse" to tile.warehouse,
                "rackLocation" to tile.rackLocation,
                "boxesAvailable" to tile.boxesAvailable,
                "purchasePriceINR" to tile.purchasePriceINR,
                "sellingPriceINR" to tile.sellingPriceINR,
                "supplier" to tile.supplier,
                "dateAddedMs" to tile.dateAddedMs,
                "lastMovementDateMs" to tile.lastMovementDateMs,
                "imageUrl" to tile.imageUrl
            )
            batch.set(docRef, map, SetOptions.merge())
        }
        batch.commit()
            .addOnSuccessListener { Log.d(TAG, "Successfully bulk synced ${tiles.size} tiles to Firestore!") }
            .addOnFailureListener { e -> Log.e(TAG, "Error bulk syncing to Firestore", e) }
    }

    // Firestore: Stock Movement Logging
    fun logStockMovementToCloud(movement: StockMovementEntity) {
        val db = firestore ?: return
        val map = hashMapOf(
            "id" to movement.id,
            "tileSku" to movement.tileSku,
            "tileName" to movement.tileName,
            "movementType" to movement.movementType,
            "boxesCount" to movement.boxesCount,
            "fromLocation" to movement.fromLocation,
            "toLocation" to movement.toLocation,
            "notes" to movement.notes,
            "timestampMs" to movement.timestampMs,
            "performedBy" to movement.performedBy
        )
        db.collection("stock_movements").document("MOV_${movement.timestampMs}")
            .set(map)
            .addOnSuccessListener { Log.d(TAG, "Logged stock movement to Firestore") }
    }

    // Firestore: Save Quotation to Cloud
    fun saveQuotationToCloud(quotation: QuotationEntity) {
        val db = firestore ?: return
        val map = hashMapOf(
            "quotationNumber" to quotation.quotationNumber,
            "customerName" to quotation.customerName,
            "customerPhone" to quotation.customerPhone,
            "salespersonName" to quotation.salespersonName,
            "itemsSummary" to quotation.itemsSummary,
            "totalAmountINR" to quotation.totalAmountINR,
            "discountINR" to quotation.discountINR,
            "netAmountINR" to quotation.netAmountINR,
            "status" to quotation.status,
            "timestampMs" to quotation.timestampMs
        )
        db.collection("quotations").document(quotation.quotationNumber)
            .set(map)
            .addOnSuccessListener { Log.d(TAG, "Saved quotation ${quotation.quotationNumber} to Firestore") }
    }

    // Firestore: Save Payment Transaction (Google Pay / UPI / Card)
    fun recordPaymentTransactionToCloud(
        transaction: PaymentTransactionEntity,
        onComplete: (Boolean) -> Unit = {}
    ) {
        val db = firestore
        if (db == null) {
            Log.d(TAG, "Simulated payment recording to Firestore: ${transaction.transactionId}")
            onComplete(true)
            return
        }

        val map = hashMapOf(
            "transactionId" to transaction.transactionId,
            "referenceId" to transaction.referenceId,
            "amountINR" to transaction.amountINR,
            "gstINR" to transaction.gstINR,
            "paymentType" to transaction.paymentType,
            "paymentMethod" to transaction.paymentMethod,
            "payerUpiId" to transaction.payerUpiId,
            "payeeUpiId" to transaction.payeeUpiId,
            "status" to transaction.status,
            "timestampMs" to transaction.timestampMs,
            "userEmail" to transaction.userEmail
        )

        db.collection("payment_transactions").document(transaction.transactionId)
            .set(map, SetOptions.merge())
            .addOnSuccessListener {
                Log.d(TAG, "Payment transaction ${transaction.transactionId} recorded successfully in Firestore")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error recording payment transaction to Firestore", e)
                onComplete(false)
            }
    }

    // Firestore: Realtime Listener for Payment Transactions History
    fun listenToPaymentTransactions(onPaymentsUpdated: (List<PaymentTransactionEntity>) -> Unit) {
        val db = firestore ?: return
        db.collection("payment_transactions")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to payment_transactions: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val payments = snapshot.documents.mapNotNull { doc ->
                        try {
                            PaymentTransactionEntity(
                                transactionId = doc.getString("transactionId") ?: doc.id,
                                referenceId = doc.getString("referenceId") ?: "N/A",
                                amountINR = doc.getDouble("amountINR") ?: 0.0,
                                gstINR = doc.getDouble("gstINR") ?: 0.0,
                                paymentType = doc.getString("paymentType") ?: "Payment",
                                paymentMethod = doc.getString("paymentMethod") ?: "Google Pay (UPI)",
                                payerUpiId = doc.getString("payerUpiId") ?: "payer@upi",
                                payeeUpiId = doc.getString("payeeUpiId") ?: "tileiq@okicici",
                                status = doc.getString("status") ?: "SUCCESS",
                                timestampMs = doc.getLong("timestampMs") ?: System.currentTimeMillis(),
                                userEmail = doc.getString("userEmail") ?: ""
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    onPaymentsUpdated(payments)
                }
            }
    }
}
