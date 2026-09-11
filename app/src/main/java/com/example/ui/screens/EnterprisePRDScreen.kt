package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun EnterprisePRDScreen() {
    var selectedSection by remember { mutableStateOf(0) }
    val sections = listOf("PRD & Vision", "User Journeys", "Database Schema", "Architecture", "API Contracts", "India Roadmap")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(16.dp)
            .testTag("prd_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "ENTERPRISE ARCHITECTURE & DELIVERABLES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "TileIQ Platform Blueprint",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Section Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedSection,
                containerColor = Slate800,
                contentColor = Color.White,
                edgePadding = 0.dp
            ) {
                sections.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSection == index,
                        onClick = { selectedSection = index },
                        text = { Text(title, fontSize = 12.sp) }
                    )
                }
            }
        }

        when (selectedSection) {
            0 -> { // PRD & Vision
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("1. PRODUCT REQUIREMENTS DOCUMENT (PRD)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Indigo500)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Problem Statement: Tile showrooms experience friction where salespeople must make phone calls to warehouse staff to confirm stock, resulting in delays, lost sales, and unmonitored dead stock locking up working capital.\n\n" +
                                        "Solution: TileIQ provides sub-second multi-warehouse inventory search, real-time box/SQFT location tracking, dead stock classification, and AI-driven discount & bundle recommendations.\n\n" +
                                        "Core Objectives:\n" +
                                        "• Instant Stock Lookup <1s across SKU, Brand, Finish, Size\n" +
                                        "• Dead Stock Intelligence (0-30d Healthy, 31-60d Slow, 61-180d Attention, 181-365d High Risk, 365+d Dead)\n" +
                                        "• Instant Quotation Builder with WhatsApp integration\n" +
                                        "• All Financial Figures formatted strictly in INR (₹)",
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            1 -> { // User Journeys
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("2. USER JOURNEYS BY ROLE", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Emerald500)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "• Showroom Owner: Opens app -> Views Executive Dashboard -> Analyzes Dead Stock Capital Lock (₹) -> Reviews AI Discount Recommendations -> Sets Reorder Policies.\n\n" +
                                        "• Salesperson: Customer asks for '2x4 White Gloss' -> Types query in Instant Search (<1s) -> Sees 42 Boxes available in Warehouse A / Rack B-12 -> Builds Quotation -> Shares via WhatsApp.\n\n" +
                                        "• Warehouse Manager: Scans box QR code -> Receives factory stock -> Assigns Rack ID -> Issues stock for customer dispatch -> Prints QR labels.",
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            2 -> { // DB Schema
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("3. DATABASE SCHEMA (ROOM / SQLITE)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Indigo500)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "TABLE tiles (\n" +
                                        "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                        "  sku TEXT UNIQUE NOT NULL,\n" +
                                        "  barcode TEXT,\n" +
                                        "  brand TEXT, series TEXT, collection TEXT, tileName TEXT,\n" +
                                        "  size TEXT, finish TEXT, color TEXT, thickness TEXT, material TEXT,\n" +
                                        "  application TEXT, warehouse TEXT, rackLocation TEXT,\n" +
                                        "  boxesAvailable INTEGER, reservedBoxes INTEGER,\n" +
                                        "  purchasePriceINR REAL, sellingPriceINR REAL,\n" +
                                        "  lastMovementDateMs INTEGER, dateAddedMs INTEGER\n" +
                                        ");\n\n" +
                                        "TABLE stock_movements (\n" +
                                        "  id INTEGER PRIMARY KEY,\n" +
                                        "  tileId INTEGER, movementType TEXT, boxesCount INTEGER,\n" +
                                        "  fromLocation TEXT, toLocation TEXT, timestampMs INTEGER\n" +
                                        ");",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Emerald500
                            )
                        }
                    }
                }
            }

            3 -> { // Architecture
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("4. SYSTEM ARCHITECTURE", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Indigo500)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "• Frontend: Jetpack Compose + Kotlin + Material Design 3\n" +
                                        "• Local DB: Room SQLite with Coroutines Flow for offline persistence\n" +
                                        "• AI Layer: Gemini API / Local Natural Language Query Engine\n" +
                                        "• Architecture: Clean MVVM with Unidirectional Data Flow (StateFlow)\n" +
                                        "• Image Caching: Coil Compose integration",
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            4 -> { // API Contracts
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("5. REST API CONTRACTS", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Emerald500)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "GET /api/v1/tiles/search?q={query}&brand={brand}\n" +
                                        "POST /api/v1/inventory/movements\n" +
                                        "POST /api/v1/quotations/create\n" +
                                        "POST /api/v1/ai/natural-language-query",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Indigo500
                            )
                        }
                    }
                }
            }

            5 -> { // India Roadmap
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("6. INDIA SCALING ROADMAP", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Emerald500)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "• Phase 1: Showroom Single-Branch Inventory Intelligence (Current)\n" +
                                        "• Phase 2: Multi-Branch & Central Morbi Warehouse Sync\n" +
                                        "• Phase 3: GST E-Invoicing & WhatsApp Quotation Bot\n" +
                                        "• Phase 4: AR Room Tile Visualizer & AI Sales Assistant",
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
