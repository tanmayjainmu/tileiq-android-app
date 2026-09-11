package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_movements")
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tileId: Long,
    val tileSku: String,
    val tileName: String,
    val movementType: String, // "RECEIVE", "ISSUE", "TRANSFER", "ADJUSTMENT", "DAMAGE_RETURN"
    val boxesCount: Int,
    val fromLocation: String,
    val toLocation: String,
    val notes: String,
    val performedBy: String,
    val timestampMs: Long = System.currentTimeMillis()
)
