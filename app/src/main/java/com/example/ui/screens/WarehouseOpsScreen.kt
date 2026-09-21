package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StockMovementEntity
import com.example.data.TileItemEntity
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WarehouseOpsScreen(
    tiles: List<TileItemEntity>,
    recentMovements: List<StockMovementEntity>,
    onProcessMovement: (TileItemEntity, String, Int, String, String, String, String) -> Unit,
    onAddNewProduct: () -> Unit
) {
    var selectedOpTab by remember { mutableStateOf(0) } // 0: Fast Movements, 1: QR Scanner & Label, 2: History Logs
    var selectedTileId by remember { mutableStateOf<Long?>(tiles.firstOrNull()?.id) }
    var boxesInput by remember { mutableStateOf("10") }
    var notesInput by remember { mutableStateOf("Standard inward PO verification") }
    var showScanSuccess by remember { mutableStateOf(false) }

    val activeTile = tiles.find { it.id == selectedTileId } ?: tiles.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(16.dp)
            .testTag("warehouse_ops_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Warehouse Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WAREHOUSE & RACK MANAGEMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Central Depot & Showroom Stocks",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = onAddNewProduct,
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add SKU", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New SKU", fontSize = 11.sp)
                }
            }
        }

        // Operation Tabs
        item {
            TabRow(
                selectedTabIndex = selectedOpTab,
                containerColor = Slate800,
                contentColor = Color.White
            ) {
                Tab(
                    selected = selectedOpTab == 0,
                    onClick = { selectedOpTab = 0 },
                    text = { Text("Stock Movements", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedOpTab == 1,
                    onClick = { selectedOpTab = 1 },
                    text = { Text("QR Scanner & Labels", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedOpTab == 2,
                    onClick = { selectedOpTab = 2 },
                    text = { Text("Movement History", fontSize = 12.sp) }
                )
            }
        }

        when (selectedOpTab) {
            0 -> { // Stock Movement Entry Form
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Slate700, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "LOG INWARD / OUTWARD / TRANSFER",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Select SKU
                            Text("Select Tile Product SKU", fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyColumn(
                                modifier = Modifier.height(140.dp)
                            ) {
                                items(tiles) { tile ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (tile.id == activeTile?.id) Indigo500.copy(alpha = 0.2f) else Slate900)
                                            .clickable { selectedTileId = tile.id }
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(tile.tileName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                            Text("${tile.sku} • ${tile.warehouse} [${tile.rackLocation}]", fontSize = 11.sp, color = Color.Gray)
                                        }
                                        Text("${tile.boxesAvailable} Boxes", fontWeight = FontWeight.Bold, color = Emerald500, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Box count input
                            OutlinedTextField(
                                value = boxesInput,
                                onValueChange = { boxesInput = it },
                                label = { Text("Quantity (Boxes)") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Slate900,
                                    unfocusedContainerColor = Slate900,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = notesInput,
                                onValueChange = { notesInput = it },
                                label = { Text("Notes / Invoice / Gate Pass #") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Slate900,
                                    unfocusedContainerColor = Slate900,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        activeTile?.let {
                                            val qty = boxesInput.toIntOrNull() ?: 1
                                            onProcessMovement(
                                                it, "RECEIVE", qty, "Factory Direct Inward", it.warehouse + " [" + it.rackLocation + "]", notesInput, "Rajesh (Warehouse Mgr)"
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Input, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Inward", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        activeTile?.let {
                                            val qty = boxesInput.toIntOrNull() ?: 1
                                            onProcessMovement(
                                                it, "ISSUE", qty, it.warehouse + " [" + it.rackLocation + "]", "Customer Dispatch Site", notesInput, "Rajesh (Warehouse Mgr)"
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Output, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Dispatch", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            1 -> { // QR Scanner Simulation & Label Printable Sheet
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Slate700, RoundedCornerShape(12.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "QR / BARCODE SCANNER SIMULATOR",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Slate900)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "Scan Barcode",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(54.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Point camera at box QR code",
                                        fontSize = 10.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = { showScanSuccess = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.QrCode, contentDescription = "Simulate Scan", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Simulate Barcode Scan")
                            }

                            if (showScanSuccess) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Emerald500.copy(alpha = 0.2f))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Emerald500)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Scanned SKU: ${activeTile?.sku}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                            Text("${activeTile?.tileName} • Location: ${activeTile?.rackLocation}", fontSize = 11.sp, color = Color.LightGray)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            Divider(color = Slate700)
                            Spacer(modifier = Modifier.height(16.dp))

                            // Printable Label Sheet Preview
                            Text("PRINTABLE WAREHOUSE LABEL PREVIEW", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(activeTile?.brand?.uppercase() ?: "TILEIQ", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                                        Text("RACK: ${activeTile?.rackLocation}", fontWeight = FontWeight.Bold, color = Color.Blue, fontSize = 12.sp)
                                    }
                                    Text(activeTile?.tileName ?: "Tile Product Name", fontWeight = FontWeight.Bold, color = Color.DarkGray, fontSize = 14.sp)
                                    Text("SKU: ${activeTile?.sku} | Size: ${activeTile?.size}", fontSize = 11.sp, color = Color.Gray)
                                    Text("MRP: ${CurrencyFormatter.formatINR(activeTile?.sellingPriceINR ?: 0.0)} / box", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            2 -> { // Movement History Logs
                item {
                    Text("RECENT STOCK LOGS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                }

                items(recentMovements) { m ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Slate700, RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = m.movementType,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (m.movementType == "RECEIVE") Emerald500 else Indigo500
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = m.tileName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                }
                                Text("${m.boxesCount} Boxes • ${m.notes}", fontSize = 11.sp, color = Color.LightGray)
                                Text("By ${m.performedBy} on ${SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(m.timestampMs))}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
