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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.TileItemEntity
import com.example.ui.components.AgingBadge
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.CurrencyFormatter

@Composable
fun TileDetailSheet(
    tile: TileItemEntity,
    allTiles: List<TileItemEntity>,
    onClose: () -> Unit,
    onAddToCart: (TileItemEntity) -> Unit,
    onSelectTile: (TileItemEntity) -> Unit
) {
    val marginPercent = if (tile.sellingPriceINR > 0) ((tile.sellingPriceINR - tile.purchasePriceINR) / tile.sellingPriceINR) * 100 else 0.0
    val alternatives = allTiles.filter { it.id != tile.id && (it.color == tile.color || it.size == tile.size || it.brand == tile.brand) }.take(4)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(16.dp)
            .testTag("tile_detail_sheet"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Navigation Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tile.brand.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AgingBadge(agingBand = tile.agingBand)
                }

                IconButton(onClick = onClose) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        // Image Header & Title
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Slate800)
            ) {
                if (tile.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = tile.imageUrl,
                        contentDescription = tile.tileName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tile.tileName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "${tile.series} • ${tile.collection} • SKU: ${tile.sku}",
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }

        // Warehouse Rack Location & Instant Stock Card
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
                            Text("WAREHOUSE LOCATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text(tile.warehouse, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Emerald500, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("RACK LOCATION: ${tile.rackLocation}", fontWeight = FontWeight.Bold, color = Emerald500, fontSize = 13.sp)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("${tile.boxesAvailable} Boxes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Emerald500)
                            Text("${tile.totalSqftAvailable.toInt()} SQFT Available", fontSize = 11.sp, color = Color.LightGray)
                        }
                    }

                    if (tile.reservedBoxes > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Slate700)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Reserved Stock: ${tile.reservedBoxes} boxes allocated to pending quotes", fontSize = 11.sp, color = Amber500)
                    }
                }
            }
        }

        // Financials & Pricing (in INR)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate700, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PRICING & CAPITAL BREAKDOWN (INR)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Selling Price", fontSize = 11.sp, color = Color.Gray)
                            Text(CurrencyFormatter.formatINR(tile.sellingPriceINR), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            Text("Per box (${tile.sqftPerBox} sqft)", fontSize = 10.sp, color = Color.Gray)
                        }

                        Column {
                            Text("Purchase Cost", fontSize = 11.sp, color = Color.Gray)
                            Text(CurrencyFormatter.formatINR(tile.purchasePriceINR), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.LightGray)
                            Text("Supplier Basis", fontSize = 10.sp, color = Color.Gray)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Gross Margin", fontSize = 11.sp, color = Color.Gray)
                            Text("${String.format("%.1f", marginPercent)}%", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Emerald500)
                            Text("Profit Yield", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        // Aging Gauge & AI Suggested Discount
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("INVENTORY AGE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("${tile.daysInInventory} Days in Stock", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(tile.agingBand.colorHex))
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("AI Suggested Discount", fontSize = 11.sp, color = Color.Gray)
                            Text("${tile.suggestedDiscountPercent}% Off", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Amber500)
                        }
                    }
                }
            }
        }

        // Add to Quote & Catalog PDF Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onAddToCart(tile) },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Quotation", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { /* Share Catalog PDF */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Catalog PDF", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Related Tiles & Alternative Matching Options
        if (alternatives.isNotEmpty()) {
            item {
                Text("MATCHING & ALTERNATIVE TILES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            }

            items(alternatives) { alt ->
                TileSearchCard(
                    tile = alt,
                    onClick = { onSelectTile(alt) },
                    onAddToCart = { onAddToCart(alt) }
                )
            }
        }
    }
}
