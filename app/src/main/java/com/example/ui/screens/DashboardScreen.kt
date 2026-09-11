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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun DashboardScreen(
    allTiles: List<TileItemEntity>,
    onNavigateToDeadStock: () -> Unit,
    onNavigateToAI: () -> Unit,
    onSelectTile: (TileItemEntity) -> Unit
) {
    val totalSKUs = allTiles.size
    val totalBrands = allTiles.map { it.brand }.distinct().size
    val totalBoxes = allTiles.sumOf { it.boxesAvailable }
    val reservedBoxes = allTiles.sumOf { it.reservedBoxes }
    val totalStockValueINR = allTiles.sumOf { it.totalStockValueINR }

    val deadStockTiles = allTiles.filter { it.agingBand == AgingBand.DEAD_STOCK }
    val deadStockValueINR = deadStockTiles.sumOf { it.totalStockValueINR }

    val outOfStockCount = allTiles.count { it.boxesAvailable == 0 }
    val lowStockCount = allTiles.count { it.boxesAvailable > 0 && it.boxesAvailable < it.minStockLevel }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Banner Prompt
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Indigo500.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Indigo500.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable { onNavigateToAI() }
                    .testTag("dashboard_ai_banner")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Indigo500),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TileIQ Natural Language Assistant",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Ask: \"Which brand has maximum dead stock?\" or \"Unsold tiles 6+ months\"",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Executive Financial KPI Grid
        item {
            Text(
                text = "EXECUTIVE OVERVIEW (INR)",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KPICard(
                    title = "Total Stock Value",
                    value = CurrencyFormatter.formatINRCompact(totalStockValueINR),
                    subtitle = "$totalBoxes Boxes in Inventory",
                    icon = Icons.Default.MonetizationOn,
                    accentColor = Emerald500,
                    modifier = Modifier.weight(1f)
                )
                KPICard(
                    title = "Dead Stock Risk",
                    value = CurrencyFormatter.formatINRCompact(deadStockValueINR),
                    subtitle = "${deadStockTiles.size} SKUs > 365 Days",
                    icon = Icons.Default.TrendingDown,
                    accentColor = Crimson500,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Operational Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KPICard(
                    title = "Total SKUs",
                    value = totalSKUs.toString(),
                    subtitle = "$totalBrands Leading Brands",
                    icon = Icons.Default.Inventory2,
                    accentColor = Indigo500,
                    modifier = Modifier.weight(1f)
                )
                KPICard(
                    title = "Reserved Stock",
                    value = "$reservedBoxes Boxes",
                    subtitle = "Locked for Quotations",
                    icon = Icons.Default.Category,
                    accentColor = Amber500,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Inventory Aging Breakdown Chart
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate700, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "INVENTORY AGE DISTRIBUTION",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Working Capital Aging Spectrum",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                        Button(
                            onClick = onNavigateToDeadStock,
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Analyze Dead Stock", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Aging bands progress bars
                    AgingBand.entries.forEach { band ->
                        val matching = allTiles.filter { it.agingBand == band }
                        val bandValueINR = matching.sumOf { it.totalStockValueINR }
                        val ratio = if (totalStockValueINR > 0) (bandValueINR / totalStockValueINR).toFloat() else 0f

                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(band.colorHex))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${band.label} (${band.rangeLabel})",
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = "${CurrencyFormatter.formatINRCompact(bandValueINR)} (${(ratio * 100).toInt()}%)",
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Slate700)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(ratio.coerceAtLeast(0.02f))
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(Color(band.colorHex))
                                )
                            }
                        }
                    }
                }
            }
        }

        // Low Stock & Critical Inventory Alerts
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate700, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Low Stock Alert",
                                tint = Amber500,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LOW & CRITICAL STOCK ALERTS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "$lowStockCount Items Low",
                            fontSize = 11.sp,
                            color = Amber500,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val alertTiles = allTiles.filter { it.boxesAvailable < it.minStockLevel }
                    if (alertTiles.isEmpty()) {
                        Text(
                            text = "All active products have adequate warehouse stock.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        alertTiles.forEach { tile ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelectTile(tile) }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tile.tileName,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${tile.brand} • ${tile.warehouse} (${tile.rackLocation})",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${tile.boxesAvailable} Boxes Left",
                                        fontWeight = FontWeight.Bold,
                                        color = Crimson500,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Min Level: ${tile.minStockLevel}",
                                        fontSize = 10.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }
                            Divider(color = Slate700, thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}
