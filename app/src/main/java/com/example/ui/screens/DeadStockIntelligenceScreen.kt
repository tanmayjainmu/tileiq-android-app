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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import com.example.data.AgingBand
import com.example.data.TileItemEntity
import com.example.ui.components.AgingBadge
import com.example.ui.components.KPICard
import com.example.ui.theme.Amber500
import com.example.ui.theme.Crimson500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.CurrencyFormatter

@Composable
fun DeadStockIntelligenceScreen(
    tiles: List<TileItemEntity>,
    onSelectTile: (TileItemEntity) -> Unit,
    onApplyDiscountPlan: (TileItemEntity, Int) -> Unit,
    onNavigateToAI: () -> Unit
) {
    var selectedTabBand by remember { mutableStateOf<AgingBand?>(AgingBand.DEAD_STOCK) }

    val totalCapitalValueINR = tiles.sumOf { it.totalStockValueINR }
    val deadStockTiles = tiles.filter { it.agingBand == AgingBand.DEAD_STOCK }
    val deadStockValueINR = deadStockTiles.sumOf { it.totalStockValueINR }
    val deadStockRatio = if (totalCapitalValueINR > 0) (deadStockValueINR / totalCapitalValueINR) * 100 else 0.0

    val filteredList = if (selectedTabBand == null) tiles else tiles.filter { it.agingBand == selectedTabBand }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(16.dp)
            .testTag("dead_stock_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Executive Dead Stock Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Crimson500.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Crimson500.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Crimson500),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Dead Stock Alert",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "DEAD STOCK CAPITAL LOCK",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Crimson500,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${CurrencyFormatter.formatINRCompact(deadStockValueINR)} Locked",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToAI,
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Strategy",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AI Plan", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Dead stock accounts for ${String.format("%.1f", deadStockRatio)}% of total showroom capital (${deadStockTiles.size} SKUs unsold for >365 days).",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
            }
        }

        // Breakdown Tabs by Aging Spectrum
        item {
            ScrollableTabRow(
                selectedTabIndex = if (selectedTabBand == null) 0 else selectedTabBand!!.ordinal + 1,
                containerColor = Slate800,
                contentColor = Color.White,
                edgePadding = 0.dp
            ) {
                Tab(
                    selected = selectedTabBand == null,
                    onClick = { selectedTabBand = null },
                    text = { Text("All (${tiles.size})", fontSize = 12.sp) }
                )
                AgingBand.entries.forEach { band ->
                    val count = tiles.count { it.agingBand == band }
                    Tab(
                        selected = selectedTabBand == band,
                        onClick = { selectedTabBand = band },
                        text = {
                            Text(
                                text = "${band.label} ($count)",
                                fontSize = 12.sp,
                                color = Color(band.colorHex)
                            )
                        }
                    )
                }
            }
        }

        // Brand-Wise & Warehouse-Wise Analytics Summary
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate700, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "BRAND-WISE DEAD STOCK ANALYSIS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val brandGroups = deadStockTiles.groupBy { it.brand }
                        .mapValues { entry -> entry.value.sumOf { it.totalStockValueINR } }
                        .entries.sortedByDescending { it.value }

                    if (brandGroups.isEmpty()) {
                        Text(
                            text = "No dead stock found in current filter.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    } else {
                        brandGroups.forEach { (brand, value) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = brand,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    text = CurrencyFormatter.formatINRCompact(value),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Crimson500
                                )
                            }
                        }
                    }
                }
            }
        }

        // List of Matching Inventory Items with AI Suggested Discount
        item {
            Text(
                text = "INVENTORY SPECTRUM (${filteredList.size} ITEMS)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
        }

        items(filteredList, key = { it.id }) { tile ->
            DeadStockItemCard(
                tile = tile,
                onClick = { onSelectTile(tile) },
                onApplyDiscount = { discount -> onApplyDiscountPlan(tile, discount) }
            )
        }
    }
}

@Composable
fun DeadStockItemCard(
    tile: TileItemEntity,
    onClick: () -> Unit,
    onApplyDiscount: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Slate800),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Slate700, RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = tile.brand.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        AgingBadge(agingBand = tile.agingBand)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tile.tileName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${tile.size} • ${tile.warehouse} [${tile.rackLocation}]",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyFormatter.formatINR(tile.totalStockValueINR),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${tile.daysInInventory} Days Unsold",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(tile.agingBand.colorHex)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // AI Clearance Recommendation Row
            if (tile.suggestedDiscountPercent > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate900)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = "AI Suggested Discount",
                                tint = Amber500,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI Rec: ${tile.suggestedDiscountPercent}% Off Clearance",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Amber500
                            )
                        }
                        val discountedPrice = tile.sellingPriceINR * (1 - (tile.suggestedDiscountPercent / 100.0))
                        Text(
                            text = "New Selling Price: ${CurrencyFormatter.formatINR(discountedPrice)} / box",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }

                    Button(
                        onClick = { onApplyDiscount(tile.suggestedDiscountPercent) },
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Apply Deal", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
