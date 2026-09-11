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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.components.InstantSearchBar
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.CurrencyFormatter

@Composable
fun InventorySearchScreen(
    query: String,
    onQueryChange: (String) -> Unit,
    tiles: List<TileItemEntity>,
    selectedBrand: String?,
    onBrandSelected: (String?) -> Unit,
    selectedWarehouse: String?,
    onWarehouseSelected: (String?) -> Unit,
    onSelectTile: (TileItemEntity) -> Unit,
    onAddToCart: (TileItemEntity) -> Unit,
    onClearFilters: () -> Unit,
    onAddNewProduct: () -> Unit
) {
    val brands = listOf("Kajaria", "Somany", "Nitco", "Asian Granito", "Simpolo", "Varmora", "Orientbell")
    val warehouses = listOf("Warehouse Alpha - Central", "Depot Beta - West", "Showroom Floor")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(16.dp)
            .testTag("inventory_search_screen")
    ) {
        // Search Header with Add Product Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INSTANT INVENTORY SEARCH",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )

            Button(
                onClick = onAddNewProduct,
                colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("add_product_inventory_btn")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Product", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Product", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Spacer(modifier = Modifier.height(6.dp))

        InstantSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            placeholderText = "Type SKU, Name, 2x4, White Gloss, Kajaria...",
            onClear = onClearFilters
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Brand & Warehouse Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = selectedBrand == null && selectedWarehouse == null,
                    onClick = { onClearFilters() },
                    label = { Text("All SKUs (${tiles.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Indigo500,
                        selectedLabelColor = Color.White,
                        containerColor = Slate800,
                        labelColor = Color.LightGray
                    )
                )
            }
            items(brands) { brand ->
                FilterChip(
                    selected = selectedBrand == brand,
                    onClick = { onBrandSelected(if (selectedBrand == brand) null else brand) },
                    label = { Text(brand) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Indigo500,
                        selectedLabelColor = Color.White,
                        containerColor = Slate800,
                        labelColor = Color.LightGray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (tiles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No matching tiles found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try searching for '2x4', 'Glossy', 'Statuario', or clear active filters.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onClearFilters,
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo500)
                    ) {
                        Text("Reset Search")
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(tiles, key = { it.id }) { tile ->
                    TileSearchCard(
                        tile = tile,
                        onClick = { onSelectTile(tile) },
                        onAddToCart = { onAddToCart(tile) }
                    )
                }
            }
        }
    }
}

@Composable
fun TileSearchCard(
    tile: TileItemEntity,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Slate800),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Slate700, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("tile_card_${tile.sku}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Image thumbnail + tile info
                Row(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Slate700)
                    ) {
                        if (tile.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = tile.imageUrl,
                                contentDescription = tile.tileName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tile.brand.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = tile.brand.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = tile.tileName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${tile.size} • ${tile.finish} • ${tile.color}",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Warehouse Rack",
                                tint = Emerald500,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${tile.warehouse} [${tile.rackLocation}]",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Emerald500
                            )
                        }
                    }
                }

                // Price Badge in INR
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyFormatter.formatINR(tile.sellingPriceINR),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "/ box (${tile.sqftPerBox} sqft)",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stock Availability Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate900)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Stock Ready",
                            tint = if (tile.boxesAvailable > 0) Emerald500 else Color.Red,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (tile.boxesAvailable > 0) "${tile.boxesAvailable} Boxes Available (${tile.totalSqftAvailable.toInt()} sqft)" else "Out of Stock",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                    if (tile.reservedBoxes > 0) {
                        Text(
                            text = "${tile.reservedBoxes} boxes currently reserved for customer orders",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(6.dp),
                    enabled = tile.boxesAvailable > 0,
                    modifier = Modifier.testTag("add_to_quote_${tile.sku}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to Quotation",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Quote", fontSize = 11.sp)
                }
            }
        }
    }
}
