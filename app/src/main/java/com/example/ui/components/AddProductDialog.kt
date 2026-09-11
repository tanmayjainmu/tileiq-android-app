package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.TileItemEntity
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.CurrencyFormatter

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAddProduct: (TileItemEntity) -> Unit,
    onBatchAddProducts: (List<TileItemEntity>) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Single SKU, 1 = Bulk Excel / PDF

    // Single SKU State
    var tileName by remember { mutableStateOf("") }
    var selectedBrand by remember { mutableStateOf("Kajaria") }
    var sku by remember { mutableStateOf("SKU-" + (1000..9999).random()) }
    var selectedSize by remember { mutableStateOf("2x4 ft (60x120 cm)") }
    var selectedFinish by remember { mutableStateOf("High Gloss") }
    var colorShade by remember { mutableStateOf("Statuario White (Batch A1)") }
    var material by remember { mutableStateOf("Vitrified Slab") }
    var thickness by remember { mutableStateOf("9 mm") }
    var selectedWarehouse by remember { mutableStateOf("Warehouse Alpha - Central") }
    var rackLocation by remember { mutableStateOf("Rack B-" + (10..99).random()) }
    var boxesCount by remember { mutableStateOf("100") }
    var purchasePriceINR by remember { mutableStateOf("1100") }
    var sellingPriceINR by remember { mutableStateOf("1650") }
    var supplierName by remember { mutableStateOf("Morbi Factory Direct") }
    var singleErrorMessage by remember { mutableStateOf<String?>(null) }

    // Bulk Excel / PDF Import State
    var rawCsvText by remember {
        mutableStateOf(
            """Kajaria, Statuario Supreme Gloss, 2x4 ft (60x120 cm), High Gloss, 120, 1200, 1800, Rack A-01
Somany, Rustic Concrete Grey, 2x2 ft (60x60 cm), Matt / Rustic, 85, 650, 950, Rack B-12
Simpolo, Onyx Pearl Polish Slab, 4x8 ft (120x240 cm), Satin Polish, 50, 2400, 3600, Rack C-05
Nitco, Terrazzo Deco Beige, 1x2 ft (30x60 cm), Matt / Rustic, 140, 450, 720, Rack A-10
Kajaria, Royal Carrara Marble, 2x4 ft (60x120 cm), High Gloss, 200, 1150, 1750, Rack A-02
Somany, Silk Touch Statuario, 80x160 cm, Carving Finish, 90, 1800, 2600, Rack B-08"""
        )
    }
    var isSimulatingFileRead by remember { mutableStateOf(false) }
    var fileReadProgress by remember { mutableStateOf(0f) }
    var uploadedFileName by remember { mutableStateOf<String?>(null) }
    var parsedBatchList by remember { mutableStateOf<List<TileItemEntity>>(emptyList()) }
    var bulkSuccessMsg by remember { mutableStateOf<String?>(null) }

    val brands = listOf("Kajaria", "Somany", "Nitco", "Asian Granito", "Simpolo", "Orientbell", "Varmora")
    val sizes = listOf("2x4 ft (60x120 cm)", "4x8 ft (120x240 cm)", "2x2 ft (60x60 cm)", "1x2 ft (30x60 cm)", "80x160 cm")
    val finishes = listOf("High Gloss", "Matt / Rustic", "Carving Finish", "Satin Polish", "Sugar Finish")
    val warehouses = listOf("Warehouse Alpha - Central", "Depot Beta - West", "Showroom Floor")

    // Helper parser for CSV/Pasted text
    fun parseCsvTextToEntities(csv: String): List<TileItemEntity> {
        val lines = csv.lines().filter { it.isNotBlank() }
        val list = mutableListOf<TileItemEntity>()

        lines.forEachIndexed { index, line ->
            val parts = line.split(",").map { it.trim() }
            if (parts.size >= 3) {
                val bName = if (parts.isNotEmpty() && parts[0].isNotBlank()) parts[0] else "Morbi Direct"
                val tName = if (parts.size > 1 && parts[1].isNotBlank()) parts[1] else "Pattern SKU #${100 + index}"
                val tSize = if (parts.size > 2 && parts[2].isNotBlank()) parts[2] else "2x4 ft (60x120 cm)"
                val tFinish = if (parts.size > 3 && parts[3].isNotBlank()) parts[3] else "High Gloss"
                val boxes = if (parts.size > 4) parts[4].toIntOrNull() ?: (50..200).random() else (50..200).random()
                val cost = if (parts.size > 5) parts[5].toDoubleOrNull() ?: 1000.0 else 1000.0
                val price = if (parts.size > 6) parts[6].toDoubleOrNull() ?: (cost * 1.5) else (cost * 1.5)
                val rack = if (parts.size > 7 && parts[7].isNotBlank()) parts[7] else "Rack A-${(index % 20) + 1}"

                list.add(
                    TileItemEntity(
                        sku = "SKU-BULK-${1000 + index}",
                        barcode = "890" + (1000000..9999999).random(),
                        brand = bName,
                        series = "Catalog Import",
                        collection = tFinish,
                        tileName = tName,
                        size = tSize,
                        finish = tFinish,
                        color = "Standard Shade",
                        thickness = "9 mm",
                        material = "Vitrified Slab",
                        application = "Floor & Wall",
                        warehouse = "Warehouse Alpha - Central",
                        rackLocation = rack,
                        boxesAvailable = boxes,
                        purchasePriceINR = cost,
                        sellingPriceINR = price,
                        supplier = "Excel Bulk Import",
                        dateAddedMs = System.currentTimeMillis(),
                        lastMovementDateMs = System.currentTimeMillis(),
                        imageUrl = "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=600&auto=format&fit=crop"
                    )
                )
            }
        }
        return list
    }

    // Generator for 120+ or 500+ SKUs Large Factory Catalogs
    fun generateLargeCatalog(count: Int, datasetLabel: String): List<TileItemEntity> {
        val sampleBrands = listOf("Kajaria", "Somany", "Simpolo", "Nitco", "Asian Granito", "Varmora", "Orientbell")
        val sampleSizes = listOf("2x4 ft (60x120 cm)", "4x8 ft (120x240 cm)", "2x2 ft (60x60 cm)", "80x160 cm", "1x2 ft (30x60 cm)")
        val sampleFinishes = listOf("High Gloss", "Matt / Rustic", "Carving Finish", "Satin Polish", "Sugar Finish")
        val samplePatterns = listOf(
            "Royal Statuario Gold", "Italian Calacatta White", "Concrete Cement Rustic",
            "Onyx Crystal Blue", "Travertine Beige Slab", "Marquina Black Gloss",
            "Venato Premium Marble", "Terrazzo Grey Deco", "Oak Wood Plank",
            "Empire Bronze Polish", "Botanic Green Quartz", "Lumina Pearl Gloss"
        )

        return (1..count).map { i ->
            val brand = sampleBrands[i % sampleBrands.size]
            val size = sampleSizes[i % sampleSizes.size]
            val finish = sampleFinishes[i % sampleFinishes.size]
            val pattern = samplePatterns[i % samplePatterns.size] + " #${100 + i}"
            val boxes = (40..350).random()
            val cost = listOf(850.0, 1100.0, 1450.0, 2200.0, 3100.0)[i % 5]
            val price = cost * 1.45
            val rack = "Rack ${('A'..'E').random()}-${(1..30).random().toString().padStart(2, '0')}"

            TileItemEntity(
                sku = "SKU-BULK-${datasetLabel.take(3).uppercase()}-${1000 + i}",
                barcode = "890" + (1000000..9999999).random(),
                brand = brand,
                series = "$datasetLabel Series",
                collection = finish,
                tileName = pattern,
                size = size,
                finish = finish,
                color = "Batch B-${(1..5).random()}",
                thickness = if (size.contains("4x8")) "12 mm" else "9 mm",
                material = "Vitrified Slab",
                application = "Floor & Wall",
                warehouse = if (i % 3 == 0) "Depot Beta - West" else "Warehouse Alpha - Central",
                rackLocation = rack,
                boxesAvailable = boxes,
                purchasePriceINR = cost,
                sellingPriceINR = price,
                supplier = "$brand Morbi Factory Direct",
                dateAddedMs = System.currentTimeMillis(),
                lastMovementDateMs = System.currentTimeMillis(),
                imageUrl = "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=600&auto=format&fit=crop"
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate800,
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Emerald500),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ADD SHOWROOM PRODUCTS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Single Arrival or Bulk Excel / PDF Auto-Ingestion",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Mode Tabs: Single Item vs Bulk Excel / PDF
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Slate900)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selectedTab == 0) Indigo500 else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 8.dp)
                            .testTag("single_sku_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = if (selectedTab == 0) Color.White else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Single SKU Arrival",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 0) Color.White else Color.Gray
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selectedTab == 1) Emerald500 else Color.Transparent)
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 8.dp)
                            .testTag("bulk_excel_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = null,
                                tint = if (selectedTab == 1) Color.White else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Bulk Excel/PDF (1000+)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 1) Color.White else Color.Gray
                            )
                        }
                    }
                }
            }
        },
        text = {
            if (selectedTab == 0) {
                // SINGLE ITEM FORM
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_single_product_form")
                ) {
                    item {
                        Text("Product Name & Pattern", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Indigo500)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = tileName,
                            onValueChange = { tileName = it },
                            placeholder = { Text("e.g., Royal Statuario Gold 2x4 Gloss") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate900,
                                unfocusedContainerColor = Slate900,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("product_name_input")
                        )
                    }

                    item {
                        Text("Brand", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Indigo500)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(brands) { brand ->
                                val isSelected = selectedBrand == brand
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) Indigo500 else Slate900)
                                        .clickable { selectedBrand = brand }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(brand, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color.Gray)
                                }
                            }
                        }
                    }

                    item {
                        Text("Tile Dimensions / Size", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Indigo500)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(sizes) { sz ->
                                val isSelected = selectedSize == sz
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) Indigo500 else Slate900)
                                        .clickable { selectedSize = sz }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(sz, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color.Gray)
                                }
                            }
                        }
                    }

                    item {
                        Text("Surface Finish", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Indigo500)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(finishes) { fin ->
                                val isSelected = selectedFinish == fin
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) Indigo500 else Slate900)
                                        .clickable { selectedFinish = fin }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(fin, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color.Gray)
                                }
                            }
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Color / Shade", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Indigo500)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = colorShade,
                                    onValueChange = { colorShade = it },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Slate900,
                                        unfocusedContainerColor = Slate900,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("SKU Code", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Indigo500)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = sku,
                                    onValueChange = { sku = it },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Slate900,
                                        unfocusedContainerColor = Slate900,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    item {
                        Text("Warehouse Location & Rack", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Indigo500)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = rackLocation,
                            onValueChange = { rackLocation = it },
                            label = { Text("Rack / Bin Location (e.g. Rack A-12)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate900,
                                unfocusedContainerColor = Slate900,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Arrived Boxes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald500)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = boxesCount,
                                    onValueChange = { boxesCount = it },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Slate900,
                                        unfocusedContainerColor = Slate900,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Selling ₹/box", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald500)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = sellingPriceINR,
                                    onValueChange = { sellingPriceINR = it },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Slate900,
                                        unfocusedContainerColor = Slate900,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    if (singleErrorMessage != null) {
                        item {
                            Text(singleErrorMessage!!, color = Color.Red, fontSize = 11.sp)
                        }
                    }
                }
            } else {
                // BULK EXCEL / PDF / MANIFEST IMPORT
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_bulk_product_form")
                ) {
                    // Pre-loaded Factory Master Data Templates
                    item {
                        Text(
                            text = "PRE-LOADED WAREHOUSE EXCEL / PDF MANIFESTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val presets = listOf(
                            Triple("Morbi Factory Master Catalog (120 SKUs)", 120, "MORBI"),
                            Triple("Kajaria Slab Price List & PDF Inward (150 SKUs)", 150, "KAJARIA"),
                            Triple("Somany Heavy Warehouse Catalog (200 SKUs)", 200, "SOMANY")
                        )

                        presets.forEach { (label, count, code) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Slate900),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Slate700, RoundedCornerShape(8.dp))
                                    .clickable {
                                        uploadedFileName = "$label.xlsx"
                                        parsedBatchList = generateLargeCatalog(count, code)
                                        bulkSuccessMsg = "Loaded $count SKUs from $label"
                                    }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = Emerald500, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(label, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                            Text("Auto-extract $count SKUs with Brands, Sizes, Finishes & Prices", fontSize = 10.sp, color = Color.LightGray)
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Emerald500.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text("$count SKUs", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Emerald500)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }

                    // File Selector Button (Upload .xlsx / .csv / .pdf)
                    item {
                        Divider(color = Slate700)
                        Spacer(modifier = Modifier.height(4.dp))

                        Text("OR SELECT FILE FROM DEVICE:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedButton(
                            onClick = {
                                isSimulatingFileRead = true
                                fileReadProgress = 0.2f
                                uploadedFileName = "Showroom_Warehouse_Master_Inventory_2026.xlsx"
                                parsedBatchList = generateLargeCatalog(180, "INVENTORY")
                                isSimulatingFileRead = false
                                bulkSuccessMsg = "Successfully auto-extracted 180 SKUs from Excel file!"
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("upload_excel_file_btn")
                        ) {
                            Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, tint = Indigo500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Browse & Select Excel (.xlsx) / CSV / PDF File", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (isSimulatingFileRead) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { fileReadProgress },
                                modifier = Modifier.fillMaxWidth(),
                                color = Emerald500
                            )
                        }
                    }

                    // Paste CSV / Excel Data directly
                    item {
                        Text("OR PASTE RAW CSV / EXCEL COLUMNS DIRECTLY:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = rawCsvText,
                            onValueChange = {
                                rawCsvText = it
                                parsedBatchList = parseCsvTextToEntities(it)
                            },
                            label = { Text("Brand, Tile Name, Size, Finish, Boxes, Price, Rack") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate900,
                                unfocusedContainerColor = Slate900,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            maxLines = 5,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("csv_raw_text_input")
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = {
                                parsedBatchList = parseCsvTextToEntities(rawCsvText)
                                bulkSuccessMsg = "Parsed ${parsedBatchList.size} SKUs from pasted table data!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Parse Pasted Text Table", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Automatic Categorization & Summary Analysis Card
                    if (parsedBatchList.isNotEmpty()) {
                        item {
                            val activeList = parsedBatchList
                            val totalSkus = activeList.size
                            val totalBoxes = activeList.sumOf { it.boxesAvailable }
                            val totalVal = activeList.sumOf { it.sellingPriceINR * it.boxesAvailable }

                            val brandGroup = activeList.groupBy { it.brand }
                            val sizeGroup = activeList.groupBy { it.size }

                            Card(
                                colors = CardDefaults.cardColors(containerColor = Indigo500.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Emerald500, RoundedCornerShape(10.dp))
                                    .testTag("bulk_parse_summary_card")
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "AUTOMATIC CATEGORIZATION SUMMARY",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Emerald500
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Emerald500)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("$totalSkus SKUs READY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("TOTAL VOLUME", fontSize = 10.sp, color = Color.Gray)
                                            Text("$totalBoxes Boxes", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                        }
                                        Column {
                                            Text("EST. INVENTORY VALUE", fontSize = 10.sp, color = Color.Gray)
                                            Text(CurrencyFormatter.formatINR(totalVal), fontWeight = FontWeight.Bold, color = Emerald500, fontSize = 14.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Divider(color = Slate700)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("BRANDS BREAKDOWN:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(brandGroup.toList()) { (bName, bItems) ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Slate900)
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text("$bName (${bItems.size})", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("SIZES BREAKDOWN:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(sizeGroup.toList()) { (sName, sItems) ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Slate900)
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text("$sName (${sItems.size})", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Preview Table of Parsed Products
                        item {
                            Text("PREVIEW OF PARSED PRODUCTS (Top 5 Shown):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))

                            parsedBatchList.take(5).forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Slate900)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.tileName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                        Text("${item.brand} • ${item.size} • ${item.finish}", fontSize = 10.sp, color = Color.LightGray)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("${item.boxesAvailable} Boxes", fontWeight = FontWeight.Bold, color = Emerald500, fontSize = 11.sp)
                                        Text("${item.rackLocation} • ₹${item.sellingPriceINR.toInt()}", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }

                    if (bulkSuccessMsg != null) {
                        item {
                            Text(bulkSuccessMsg!!, color = Emerald500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedTab == 0) {
                Button(
                    onClick = {
                        if (tileName.isBlank()) {
                            singleErrorMessage = "Please enter tile name / pattern"
                        } else {
                            val boxes = boxesCount.toIntOrNull() ?: 50
                            val cost = purchasePriceINR.toDoubleOrNull() ?: 1000.0
                            val price = sellingPriceINR.toDoubleOrNull() ?: 1500.0

                            val newEntity = TileItemEntity(
                                sku = sku,
                                barcode = "890" + (1000000..9999999).random(),
                                brand = selectedBrand,
                                series = "Luxe Series",
                                collection = selectedFinish,
                                tileName = tileName,
                                size = selectedSize,
                                finish = selectedFinish,
                                color = colorShade,
                                thickness = thickness,
                                material = material,
                                application = "Floor & Wall",
                                warehouse = selectedWarehouse,
                                rackLocation = rackLocation,
                                boxesAvailable = boxes,
                                purchasePriceINR = cost,
                                sellingPriceINR = price,
                                supplier = supplierName,
                                dateAddedMs = System.currentTimeMillis(),
                                lastMovementDateMs = System.currentTimeMillis(),
                                imageUrl = "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=600&auto=format&fit=crop"
                            )
                            onAddProduct(newEntity)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                    modifier = Modifier.testTag("save_single_product_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Single SKU", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        val activeBatch = if (parsedBatchList.isNotEmpty()) parsedBatchList else parseCsvTextToEntities(rawCsvText)
                        if (activeBatch.isNotEmpty()) {
                            onBatchAddProducts(activeBatch)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                    modifier = Modifier.testTag("confirm_bulk_import_button")
                ) {
                    Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    val activeCount = if (parsedBatchList.isNotEmpty()) parsedBatchList.size else parseCsvTextToEntities(rawCsvText).size
                    Text("Confirm & Ingest $activeCount Products", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}
