package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AIQueryResult
import com.example.ai.GeminiTileIQService
import com.example.data.AppDatabase
import com.example.data.QuotationEntity
import com.example.data.StockMovementEntity
import com.example.data.TileItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class UserRole(val title: String, val subtitle: String) {
    SHOWROOM_OWNER("Showroom Owner", "Executive Financials, Dead Stock & AI Strategy"),
    SALESPERSON("Salesperson", "Instant Stock Lookup <1s, Alternatives & Quotations"),
    WAREHOUSE_MANAGER("Warehouse Manager", "Inward, Dispatch, Transfers & QR Scanning")
}

data class UserProfile(
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val showroomName: String,
    val showroomId: String
)

data class SubscriptionPlan(
    val planName: String = "Showroom Owner Pro License",
    val priceINR: Double = 5000.0,
    val billingCycle: String = "Monthly",
    val status: String = "Active", // "Active", "Trial", "Expired"
    val expiryDate: String = "04 Sep 2026",
    val autoRenew: Boolean = true,
    val features: List<String> = listOf(
        "Sub-second Instant Stock Search (<1s)",
        "AI Dead Stock Liquidator & Discount Advisor",
        "Unlimited WhatsApp Quotations & PDF Catalogs",
        "Multi-Warehouse Rack Location Mapping",
        "Multi-staff Logins (Sales & Warehouse Team)"
    )
)

data class BillingInvoice(
    val invoiceNumber: String,
    val date: String,
    val amountINR: Double,
    val gstINR: Double,
    val paymentMethod: String,
    val status: String
)

data class CartItem(
    val tile: TileItemEntity,
    var boxesCount: Int
)

class TileViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.tileDao()
    private val aiService = GeminiTileIQService()
    val firestoreRepo = com.example.data.FirestoreRepository()

    private val _paymentTransactions = MutableStateFlow<List<com.example.data.PaymentTransactionEntity>>(emptyList())
    val paymentTransactions: StateFlow<List<com.example.data.PaymentTransactionEntity>> = _paymentTransactions.asStateFlow()

    init {
        // Real-time listener for Firestore Cloud database updates
        firestoreRepo.listenToCloudTiles { cloudTiles ->
            viewModelScope.launch {
                dao.insertTiles(cloudTiles)
            }
        }

        // Real-time listener for Firebase Firestore Payment Receipts
        firestoreRepo.listenToPaymentTransactions { txns ->
            _paymentTransactions.value = txns
        }
    }

    // Authentication & Profile State
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow(
        UserProfile(
            name = "Rajesh Gupta",
            email = "rajesh.gupta@tileiq.in",
            phone = "+91 98765 43210",
            role = UserRole.SHOWROOM_OWNER,
            showroomName = "Gupta Ceramics & Tiles",
            showroomId = "SR-DEL-4012"
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    // Showroom Owner Subscription State (₹5,000 / month)
    private val _subscription = MutableStateFlow(SubscriptionPlan())
    val subscription: StateFlow<SubscriptionPlan> = _subscription.asStateFlow()

    private val _billingInvoices = MutableStateFlow(
        listOf(
            BillingInvoice("INV-2026-0801", "04 Aug 2026", 5000.0, 900.0, "UPI (rajesh@upi)", "Paid"),
            BillingInvoice("INV-2026-0701", "04 Jul 2026", 5000.0, 900.0, "Corporate Card ending 4012", "Paid"),
            BillingInvoice("INV-2026-0601", "04 Jun 2026", 5000.0, 900.0, "Razorpay NetBanking", "Paid")
        )
    )
    val billingInvoices: StateFlow<List<BillingInvoice>> = _billingInvoices.asStateFlow()

    fun loginUser(emailOrPhone: String, name: String = "", role: UserRole = UserRole.SHOWROOM_OWNER, showroomName: String = "Gupta Ceramics & Tiles") {
        val displayName = name.ifBlank {
            when (role) {
                UserRole.SHOWROOM_OWNER -> "Rajesh Gupta"
                UserRole.SALESPERSON -> "Rohan Sharma"
                UserRole.WAREHOUSE_MANAGER -> "Vikram Singh"
            }
        }
        _currentUser.value = UserProfile(
            name = displayName,
            email = if (emailOrPhone.contains("@")) emailOrPhone else "$emailOrPhone@tileiq.in",
            phone = if (emailOrPhone.startsWith("+91")) emailOrPhone else "+91 $emailOrPhone",
            role = role,
            showroomName = showroomName,
            showroomId = "SR-DEL-4012"
        )
        _currentRole.value = role
        _isLoggedIn.value = true
    }

    fun logoutUser() {
        _isLoggedIn.value = false
    }

    fun renewSubscription(
        months: Int = 1,
        paymentMethod: String = "Google Pay (UPI)",
        payerUpiId: String = "rajesh@upi",
        onSuccess: () -> Unit
    ) {
        val currentPlan = _subscription.value
        val totalINR = currentPlan.priceINR * months // All inclusive: ₹5,000 for 1 mo, ₹50,000 for 12 mo
        val baseINR = totalINR / 1.18
        val gstINR = totalINR - baseINR
        val invNum = "INV-2026-" + (1000..9999).random()
        val txnId = "TXN_GPAY_" + System.currentTimeMillis()

        _subscription.value = currentPlan.copy(
            status = "Active",
            expiryDate = if (months == 12) "04 Aug 2027" else "04 Oct 2026"
        )

        val newInvoice = BillingInvoice(
            invoiceNumber = invNum,
            date = "04 Aug 2026",
            amountINR = baseINR,
            gstINR = gstINR,
            paymentMethod = "$paymentMethod ($payerUpiId)",
            status = "Paid"
        )

        _billingInvoices.value = listOf(newInvoice) + _billingInvoices.value

        // Log payment transaction to Cloud Firestore
        val txnEntity = com.example.data.PaymentTransactionEntity(
            transactionId = txnId,
            referenceId = invNum,
            amountINR = totalINR,
            gstINR = gstINR,
            paymentType = "Showroom Pro Subscription Renewal ($months Months)",
            paymentMethod = paymentMethod,
            payerUpiId = payerUpiId.ifBlank { "rajesh@upi" },
            payeeUpiId = com.example.util.GooglePayUpiHelper.DEFAULT_SHOWROOM_VPA,
            status = "SUCCESS",
            timestampMs = System.currentTimeMillis(),
            userEmail = currentUser.value.email
        )
        firestoreRepo.recordPaymentTransactionToCloud(txnEntity) {
            onSuccess()
        }
    }

    // Process Customer Deposit / Quotation Payment via Google Pay / UPI
    fun recordCustomerQuotationPayment(
        quotationNum: String,
        amountINR: Double,
        payerUpiId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val txnId = "TXN_CUST_GPAY_" + System.currentTimeMillis()
            val txnEntity = com.example.data.PaymentTransactionEntity(
                transactionId = txnId,
                referenceId = quotationNum,
                amountINR = amountINR,
                gstINR = amountINR * 0.18,
                paymentType = "Customer Quotation Advance Payment",
                paymentMethod = "Google Pay (UPI)",
                payerUpiId = payerUpiId.ifBlank { "customer@okaxis" },
                payeeUpiId = com.example.util.GooglePayUpiHelper.DEFAULT_SHOWROOM_VPA,
                status = "SUCCESS",
                timestampMs = System.currentTimeMillis(),
                userEmail = currentUser.value.email
            )

            // Save to Firestore
            firestoreRepo.recordPaymentTransactionToCloud(txnEntity)

            // Update Quotation Status in Room DB
            val existing = allQuotations.value.find { it.quotationNumber == quotationNum }
            if (existing != null) {
                val updated = existing.copy(status = "Paid via GPay/UPI")
                dao.insertQuotation(updated)
                firestoreRepo.saveQuotationToCloud(updated)
            }
            onSuccess()
        }
    }

    fun toggleAutoRenew() {
        val curr = _subscription.value
        _subscription.value = curr.copy(autoRenew = !curr.autoRenew)
    }

    // Current Active Role
    private val _currentRole = MutableStateFlow(UserRole.SHOWROOM_OWNER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    fun setRole(role: UserRole) {
        _currentRole.value = role
        _currentUser.value = _currentUser.value.copy(role = role)
    }

    // Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedBrandFilter = MutableStateFlow<String?>(null)
    val selectedBrandFilter: StateFlow<String?> = _selectedBrandFilter.asStateFlow()

    private val _selectedWarehouseFilter = MutableStateFlow<String?>(null)
    val selectedWarehouseFilter: StateFlow<String?> = _selectedWarehouseFilter.asStateFlow()

    private val _selectedAgingFilter = MutableStateFlow<String?>(null) // e.g. "Dead Stock", "365+ Days"
    val selectedAgingFilter: StateFlow<String?> = _selectedAgingFilter.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setBrandFilter(brand: String?) {
        _selectedBrandFilter.value = brand
    }

    fun setWarehouseFilter(warehouse: String?) {
        _selectedWarehouseFilter.value = warehouse
    }

    fun setAgingFilter(aging: String?) {
        _selectedAgingFilter.value = aging
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedBrandFilter.value = null
        _selectedWarehouseFilter.value = null
        _selectedAgingFilter.value = null
    }

    // All Tiles Flow from Room
    val allTiles: StateFlow<List<TileItemEntity>> = dao.getAllTilesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Tiles combining Query & Filters
    val filteredTiles: StateFlow<List<TileItemEntity>> = combine(
        allTiles,
        _searchQuery,
        _selectedBrandFilter,
        _selectedWarehouseFilter,
        _selectedAgingFilter
    ) { tiles, query, brand, warehouse, aging ->
        tiles.filter { tile ->
            val matchesQuery = query.isBlank() ||
                    tile.tileName.contains(query, ignoreCase = true) ||
                    tile.sku.contains(query, ignoreCase = true) ||
                    tile.brand.contains(query, ignoreCase = true) ||
                    tile.size.contains(query, ignoreCase = true) ||
                    tile.color.contains(query, ignoreCase = true) ||
                    tile.finish.contains(query, ignoreCase = true) ||
                    tile.application.contains(query, ignoreCase = true) ||
                    tile.warehouse.contains(query, ignoreCase = true) ||
                    tile.rackLocation.contains(query, ignoreCase = true) ||
                    tile.barcode.contains(query, ignoreCase = true)

            val matchesBrand = brand == null || tile.brand.equals(brand, ignoreCase = true)
            val matchesWarehouse = warehouse == null || tile.warehouse.equals(warehouse, ignoreCase = true)
            val matchesAging = aging == null || tile.agingBand.label.equals(aging, ignoreCase = true) || (aging == "Dead Stock" && tile.daysInInventory >= 365)

            matchesQuery && matchesBrand && matchesWarehouse && matchesAging
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Tile for Detailed Sheet/Modal
    private val _selectedTile = MutableStateFlow<TileItemEntity?>(null)
    val selectedTile: StateFlow<TileItemEntity?> = _selectedTile.asStateFlow()

    fun selectTile(tile: TileItemEntity?) {
        _selectedTile.value = tile
    }

    // Quotation / Cart State
    private val _quotationCart = MutableStateFlow<List<CartItem>>(emptyList())
    val quotationCart: StateFlow<List<CartItem>> = _quotationCart.asStateFlow()

    fun addToCart(tile: TileItemEntity, boxes: Int = 1) {
        val current = _quotationCart.value.toMutableList()
        val index = current.indexOfFirst { it.tile.id == tile.id }
        if (index >= 0) {
            current[index] = current[index].copy(boxesCount = current[index].boxesCount + boxes)
        } else {
            current.add(CartItem(tile, boxes))
        }
        _quotationCart.value = current
    }

    fun updateCartQuantity(tileId: Long, boxes: Int) {
        val current = _quotationCart.value.toMutableList()
        if (boxes <= 0) {
            current.removeAll { it.tile.id == tileId }
        } else {
            val index = current.indexOfFirst { it.tile.id == tileId }
            if (index >= 0) {
                current[index] = current[index].copy(boxesCount = boxes)
            }
        }
        _quotationCart.value = current
    }

    fun clearCart() {
        _quotationCart.value = emptyList()
    }

    // Save Quotation to DB
    fun createQuotation(customerName: String, customerPhone: String, discountINR: Double, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val cart = _quotationCart.value
            if (cart.isEmpty()) return@launch

            val total = cart.sumOf { it.tile.sellingPriceINR * it.boxesCount }
            val finalAmount = (total - discountINR).coerceAtLeast(0.0)
            val summary = cart.joinToString(", ") { "${it.tile.tileName} (${it.boxesCount} boxes)" }
            val qNum = "TIQ-QT-" + (1000..9999).random()

            val entity = QuotationEntity(
                quotationNumber = qNum,
                customerName = customerName.ifBlank { "Walk-in Customer" },
                customerPhone = customerPhone.ifBlank { "N/A" },
                salespersonName = "Rohan Sharma (Sales)",
                itemsSummary = summary,
                totalAmountINR = total,
                discountINR = discountINR,
                netAmountINR = finalAmount,
                status = "Sent WhatsApp"
            )

            dao.insertQuotation(entity)
            clearCart()
            onSuccess(qNum)
        }
    }

    // Quotations List Flow
    val allQuotations: StateFlow<List<QuotationEntity>> = dao.getAllQuotationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recent Stock Movements Flow
    val recentMovements: StateFlow<List<StockMovementEntity>> = dao.getRecentMovementsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Perform Stock Movement (Inward, Outward, Transfer, Adjustment)
    fun processStockMovement(
        tile: TileItemEntity,
        type: String, // "RECEIVE", "ISSUE", "TRANSFER", "ADJUSTMENT"
        boxesDelta: Int,
        fromLoc: String,
        toLoc: String,
        notes: String,
        performedBy: String
    ) {
        viewModelScope.launch {
            val newBoxes = when (type) {
                "RECEIVE" -> tile.boxesAvailable + boxesDelta
                "ISSUE" -> (tile.boxesAvailable - boxesDelta).coerceAtLeast(0)
                "ADJUSTMENT" -> boxesDelta
                else -> tile.boxesAvailable
            }

            val updatedTile = tile.copy(
                boxesAvailable = newBoxes,
                lastMovementDateMs = System.currentTimeMillis(),
                status = if (newBoxes == 0) "Out of Stock" else if (newBoxes < tile.minStockLevel) "Low Stock" else "In Stock"
            )

            dao.updateTile(updatedTile)

            val movement = StockMovementEntity(
                tileId = tile.id,
                tileSku = tile.sku,
                tileName = tile.tileName,
                movementType = type,
                boxesCount = boxesDelta,
                fromLocation = fromLoc,
                toLocation = toLoc,
                notes = notes,
                performedBy = performedBy,
                timestampMs = System.currentTimeMillis()
            )
            dao.insertMovement(movement)

            // Refresh selected tile if active
            if (_selectedTile.value?.id == tile.id) {
                _selectedTile.value = updatedTile
            }
        }
    }

    // AI Natural Language Search & Insights State
    private val _aiResult = MutableStateFlow<AIQueryResult?>(null)
    val aiResult: StateFlow<AIQueryResult?> = _aiResult.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    fun askAI(query: String, apiKey: String = "") {
        viewModelScope.launch {
            _isAiLoading.value = true
            val result = aiService.processNaturalLanguageQuery(query, allTiles.value, apiKey)
            _aiResult.value = result
            _isAiLoading.value = false
        }
    }

    fun clearAiResult() {
        _aiResult.value = null
    }

    // Add New Product (Room Local + Cloud Firestore Sync)
    fun addNewTileProduct(tile: TileItemEntity) {
        viewModelScope.launch {
            dao.insertTile(tile)
            firestoreRepo.syncTileToCloud(tile)
        }
    }

    // Bulk Batch Add Products (Excel / CSV / PDF Import + Cloud Firestore Sync)
    fun addBatchTileProducts(tiles: List<TileItemEntity>, onComplete: (Int) -> Unit = {}) {
        viewModelScope.launch {
            dao.insertTiles(tiles)
            firestoreRepo.syncBatchTilesToCloud(tiles)
            onComplete(tiles.size)
        }
    }
}
