package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TileDao {

    @Query("SELECT * FROM tiles ORDER BY id ASC")
    fun getAllTilesFlow(): Flow<List<TileItemEntity>>

    @Query("SELECT * FROM tiles ORDER BY id ASC")
    suspend fun getAllTiles(): List<TileItemEntity>

    @Query("SELECT * FROM tiles WHERE id = :id")
    suspend fun getTileById(id: Long): TileItemEntity?

    @Query("SELECT * FROM tiles WHERE sku = :sku OR barcode = :barcode LIMIT 1")
    suspend fun getTileBySkuOrBarcode(sku: String, barcode: String): TileItemEntity?

    @Query("""
        SELECT * FROM tiles 
        WHERE (:query IS NULL OR :query = '' OR 
               sku LIKE '%' || :query || '%' OR 
               tileName LIKE '%' || :query || '%' OR 
               brand LIKE '%' || :query || '%' OR 
               size LIKE '%' || :query || '%' OR 
               color LIKE '%' || :query || '%' OR 
               finish LIKE '%' || :query || '%' OR 
               collection LIKE '%' || :query || '%' OR 
               series LIKE '%' || :query || '%' OR 
               application LIKE '%' || :query || '%' OR 
               warehouse LIKE '%' || :query || '%' OR 
               rackLocation LIKE '%' || :query || '%' OR 
               barcode LIKE '%' || :query || '%')
        AND (:brand IS NULL OR brand = :brand)
        AND (:warehouse IS NULL OR warehouse = :warehouse)
        ORDER BY boxesAvailable DESC
    """)
    fun searchTilesFlow(
        query: String?,
        brand: String? = null,
        warehouse: String? = null
    ): Flow<List<TileItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTiles(tiles: List<TileItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTile(tile: TileItemEntity): Long

    @Update
    suspend fun updateTile(tile: TileItemEntity)

    @Query("DELETE FROM tiles WHERE id = :id")
    suspend fun deleteTileById(id: Long)

    // Movements
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(movement: StockMovementEntity)

    @Query("SELECT * FROM stock_movements ORDER BY timestampMs DESC LIMIT 50")
    fun getRecentMovementsFlow(): Flow<List<StockMovementEntity>>

    // Quotations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotation(quotation: QuotationEntity): Long

    @Query("SELECT * FROM quotations ORDER BY timestampMs DESC")
    fun getAllQuotationsFlow(): Flow<List<QuotationEntity>>
}
