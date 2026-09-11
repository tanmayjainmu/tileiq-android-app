package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.BuildConfig
import com.example.data.TileItemEntity
import com.example.ui.TileViewModel
import com.example.ui.components.AddProductDialog
import com.example.ui.components.RoleHeaderBar
import com.example.ui.screens.AIAssistantScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DeadStockIntelligenceScreen
import com.example.ui.screens.EnterprisePRDScreen
import com.example.ui.screens.InventorySearchScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.QuotationBuilderScreen
import com.example.ui.screens.SubscriptionSettingsScreen
import com.example.ui.screens.TileDetailSheet
import com.example.ui.screens.WarehouseOpsScreen
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.TileIQTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TileIQTheme {
                TileIQApp()
            }
        }
    }
}

enum class NavItem(val label: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Analytics),
    SEARCH("Search", Icons.Default.Search),
    DEAD_STOCK("Dead Stock", Icons.Default.Warning),
    WAREHOUSE("Warehouse", Icons.Default.Inventory),
    AI_ASSISTANT("TileIQ AI", Icons.Default.AutoAwesome),
    SETTINGS("Plan & License", Icons.Default.Settings),
    PRD("PRD Specs", Icons.Default.Description)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TileIQApp(viewModel: TileViewModel = viewModel()) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val subscriptionPlan by viewModel.subscription.collectAsState()
    val billingInvoices by viewModel.billingInvoices.collectAsState()

    val currentRole by viewModel.currentRole.collectAsState()
    val allTiles by viewModel.allTiles.collectAsState()
    val filteredTiles by viewModel.filteredTiles.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedBrand by viewModel.selectedBrandFilter.collectAsState()
    val selectedWarehouse by viewModel.selectedWarehouseFilter.collectAsState()
    val selectedTile by viewModel.selectedTile.collectAsState()
    val quotationCart by viewModel.quotationCart.collectAsState()
    val recentMovements by viewModel.recentMovements.collectAsState()
    val aiResult by viewModel.aiResult.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var activeTabNav by remember { mutableIntStateOf(0) }
    var showCartSheet by remember { mutableStateOf(false) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val apiKey = try {
        val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
        field.get(null) as? String ?: ""
    } catch (e: Exception) { "" }

    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { emailOrPhone, name, role, showroomName ->
                viewModel.loginUser(emailOrPhone, name, role, showroomName)
            },
            firestoreRepo = viewModel.firestoreRepo
        )
        return
    }

    Scaffold(
        topBar = {
            RoleHeaderBar(
                currentRole = currentRole,
                onRoleSelected = { viewModel.setRole(it) },
                cartItemCount = quotationCart.size,
                onOpenCart = { showCartSheet = true },
                subscriptionPlan = subscriptionPlan,
                onOpenSubscriptionSettings = { activeTabNav = 5 }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Slate800,
                contentColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavItem.entries.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = activeTabNav == index,
                        onClick = { activeTabNav = index },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 9.sp, fontWeight = if (activeTabNav == index) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            indicatorColor = Indigo500,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_item_${item.name}")
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Slate900)
        ) {
            when (activeTabNav) {
                0 -> DashboardScreen(
                    allTiles = allTiles,
                    onNavigateToDeadStock = { activeTabNav = 2 },
                    onNavigateToAI = { activeTabNav = 4 },
                    onSelectTile = { viewModel.selectTile(it) }
                )
                1 -> InventorySearchScreen(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    tiles = filteredTiles,
                    selectedBrand = selectedBrand,
                    onBrandSelected = { viewModel.setBrandFilter(it) },
                    selectedWarehouse = selectedWarehouse,
                    onWarehouseSelected = { viewModel.setWarehouseFilter(it) },
                    onSelectTile = { viewModel.selectTile(it) },
                    onAddToCart = { viewModel.addToCart(it) },
                    onClearFilters = { viewModel.clearFilters() },
                    onAddNewProduct = { showAddProductDialog = true }
                )
                2 -> DeadStockIntelligenceScreen(
                    tiles = allTiles,
                    onSelectTile = { viewModel.selectTile(it) },
                    onApplyDiscountPlan = { tile, discount ->
                        viewModel.addToCart(tile, 5)
                        showCartSheet = true
                    },
                    onNavigateToAI = { activeTabNav = 4 }
                )
                3 -> WarehouseOpsScreen(
                    tiles = allTiles,
                    recentMovements = recentMovements,
                    onProcessMovement = { tile, type, qty, from, to, notes, user ->
                        viewModel.processStockMovement(tile, type, qty, from, to, notes, user)
                    },
                    onAddNewProduct = { showAddProductDialog = true }
                )
                4 -> AIAssistantScreen(
                    aiResult = aiResult,
                    isLoading = isAiLoading,
                    onAskAI = { query -> viewModel.askAI(query, apiKey) },
                    onSelectTile = { viewModel.selectTile(it) }
                )
                5 -> SubscriptionSettingsScreen(
                    userProfile = currentUser,
                    subscriptionPlan = subscriptionPlan,
                    billingInvoices = billingInvoices,
                    onRenewSubscription = { months, method, payerUpiId, onDone ->
                        viewModel.renewSubscription(months, method, payerUpiId, onDone)
                    },
                    onToggleAutoRenew = { viewModel.toggleAutoRenew() },
                    onLogout = { viewModel.logoutUser() }
                )
                6 -> EnterprisePRDScreen()
            }

            // Modal Sheet for Tile Details
            if (selectedTile != null) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.selectTile(null) },
                    sheetState = sheetState,
                    containerColor = Slate900
                ) {
                    TileDetailSheet(
                        tile = selectedTile!!,
                        allTiles = allTiles,
                        onClose = { viewModel.selectTile(null) },
                        onAddToCart = {
                            viewModel.addToCart(it)
                            viewModel.selectTile(null)
                            showCartSheet = true
                        },
                        onSelectTile = { viewModel.selectTile(it) }
                    )
                }
            }

            // Modal Sheet for Quotation Builder Cart
            if (showCartSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showCartSheet = false },
                    sheetState = sheetState,
                    containerColor = Slate900
                ) {
                    QuotationBuilderScreen(
                        cartItems = quotationCart,
                        onUpdateQuantity = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                        onCreateQuotation = { name, phone, discount, onDone ->
                            viewModel.createQuotation(name, phone, discount) { qNum ->
                                onDone(qNum)
                            }
                        },
                        onClose = { showCartSheet = false }
                    )
                }
            }

            // Modal Dialog for Adding New Tile Product Arrival or Bulk Excel/PDF Ingestion
            if (showAddProductDialog) {
                AddProductDialog(
                    onDismiss = { showAddProductDialog = false },
                    onAddProduct = { newTile ->
                        viewModel.addNewTileProduct(newTile)
                        showAddProductDialog = false
                    },
                    onBatchAddProducts = { tileList ->
                        viewModel.addBatchTileProducts(tileList)
                        showAddProductDialog = false
                    }
                )
            }
        }
    }
}
