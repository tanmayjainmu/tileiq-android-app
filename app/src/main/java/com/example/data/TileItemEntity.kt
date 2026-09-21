package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tiles")
data class TileItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sku: String,
    val barcode: String,
    val brand: String,
    val series: String,
    val collection: String,
    val tileName: String,
    val size: String,             // e.g. "2x4 ft (60x120 cm)"
    val finish: String,           // e.g. "Glossy", "High Gloss", "Satin", "Matte", "Carving", "Rustic", "Anti-Skid"
    val color: String,            // e.g. "Statuario White", "Onyx Beige", "Slate Grey", "Charcoal", "Calacatta Gold"
    val thickness: String,        // e.g. "9 mm"
    val material: String,         // e.g. "GVT/PGVT Vitrified", "Glazed Porcelain", "Full Body Vitrified"
    val application: String,      // e.g. "Floor & Wall", "Bathroom & Kitchen", "Outdoor Heavy Duty", "Commercial"
    val warehouse: String,        // e.g. "Warehouse Alpha - Central", "Depot Beta - West", "Showroom Floor"
    val rackLocation: String,     // e.g. "Rack B-12", "Rack A-05", "Bay C-02"
    val boxesAvailable: Int,
    val reservedBoxes: Int = 0,
    val piecesPerBox: Int = 2,
    val sqftPerBox: Double = 15.5,
    val purchasePriceINR: Double, // Price in ₹ per box
    val sellingPriceINR: Double,  // Selling Price in ₹ per box
    val supplier: String,
    val dateAddedMs: Long,
    val lastMovementDateMs: Long,
    val minStockLevel: Int = 10,
    val maxStockLevel: Int = 100,
    val imageUrl: String = "",
    val catalogPdfUrl: String = "",
    val status: String = "In Stock" // "In Stock", "Low Stock", "Out of Stock", "Reserved"
) {
    // Computed property for total sqft available
    val totalSqftAvailable: Double
        get() = boxesAvailable * sqftPerBox

    // Total stock value in INR
    val totalStockValueINR: Double
        get() = boxesAvailable * sellingPriceINR

    // Cost basis stock value in INR
    val costStockValueINR: Double
        get() = boxesAvailable * purchasePriceINR

    // Days since last movement
    val daysInInventory: Int
        get() {
            val now = System.currentTimeMillis()
            val diffMs = now - lastMovementDateMs
            val days = (diffMs / (1000 * 60 * 60 * 24)).toInt()
            return if (days < 0) 0 else days
        }

    // Dead stock risk band classification
    val agingBand: AgingBand
        get() = when (daysInInventory) {
            in 0..30 -> AgingBand.HEALTHY
            in 31..60 -> AgingBand.SLOW_MOVING
            in 61..180 -> AgingBand.ATTENTION
            in 181..365 -> AgingBand.HIGH_RISK
            else -> AgingBand.DEAD_STOCK
        }

    // Suggested discount percentage based on dead stock band
    val suggestedDiscountPercent: Int
        get() = when (agingBand) {
            AgingBand.HEALTHY -> 0
            AgingBand.SLOW_MOVING -> 5
            AgingBand.ATTENTION -> 15
            AgingBand.HIGH_RISK -> 30
            AgingBand.DEAD_STOCK -> 50
        }
}

enum class AgingBand(val label: String, val rangeLabel: String, val colorHex: Long) {
    HEALTHY("Healthy", "0–30 Days", 0xFF10B981),        // Emerald Green
    SLOW_MOVING("Slow Moving", "31–60 Days", 0xFF0284C7), // Sky Blue
    ATTENTION("Attention", "61–180 Days", 0xFFF59E0B),  // Amber
    HIGH_RISK("High Risk", "181–365 Days", 0xFFEA580C),  // Deep Orange
    DEAD_STOCK("Dead Stock", "365+ Days", 0xFFEF4444)    // Red
}
