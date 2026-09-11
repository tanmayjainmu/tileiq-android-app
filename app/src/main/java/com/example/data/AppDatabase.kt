package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TileItemEntity::class, StockMovementEntity::class, QuotationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tileDao(): TileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tile_iq_database"
                )
                    .addCallback(DatabaseCallback(context))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                populateDatabase(getDatabase(context).tileDao())
            }
        }

        suspend fun populateDatabase(dao: TileDao) {
            val now = System.currentTimeMillis()
            val dayMs = 86400000L

            val seedTiles = listOf(
                TileItemEntity(
                    sku = "KAJ-STAT-2X4-01",
                    barcode = "890100100201",
                    brand = "Kajaria",
                    series = "Royal Italian Marble",
                    collection = "Statuario Luxe",
                    tileName = "Statuario Gold Polished 2x4",
                    size = "2x4 ft (60x120 cm)",
                    finish = "High Gloss",
                    color = "Statuario White",
                    thickness = "9 mm",
                    material = "GVT/PGVT Vitrified",
                    application = "Floor & Wall",
                    warehouse = "Warehouse Alpha - Central",
                    rackLocation = "Rack A-12",
                    boxesAvailable = 142,
                    reservedBoxes = 10,
                    piecesPerBox = 2,
                    sqftPerBox = 15.5,
                    purchasePriceINR = 850.0,
                    sellingPriceINR = 1450.0,
                    supplier = "Kajaria Ceramics Ltd",
                    dateAddedMs = now - (15 * dayMs),
                    lastMovementDateMs = now - (12 * dayMs), // 12 days -> Healthy
                    imageUrl = "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=600&auto=format&fit=crop",
                    status = "In Stock"
                ),
                TileItemEntity(
                    sku = "SOM-CALA-2X4-02",
                    barcode = "890200100202",
                    brand = "Somany",
                    series = "VC Shield",
                    collection = "Calacatta Pure",
                    tileName = "Calacatta White Gloss 2x4",
                    size = "2x4 ft (60x120 cm)",
                    finish = "Glossy",
                    color = "White & Gold Vein",
                    thickness = "9 mm",
                    material = "Glazed Vitrified",
                    application = "Bathroom & Living",
                    warehouse = "Warehouse Alpha - Central",
                    rackLocation = "Rack B-08",
                    boxesAvailable = 88,
                    reservedBoxes = 4,
                    piecesPerBox = 2,
                    sqftPerBox = 15.5,
                    purchasePriceINR = 920.0,
                    sellingPriceINR = 1520.0,
                    supplier = "Somany Ceramics",
                    dateAddedMs = now - (45 * dayMs),
                    lastMovementDateMs = now - (42 * dayMs), // 42 days -> Slow Moving
                    imageUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600&auto=format&fit=crop",
                    status = "In Stock"
                ),
                TileItemEntity(
                    sku = "NIT-SLAT-2X2-03",
                    barcode = "890300100203",
                    brand = "Nitco",
                    series = "Urban Stone",
                    collection = "Slate Series",
                    tileName = "Slate Grey Anti-Skid 2x2",
                    size = "2x2 ft (60x60 cm)",
                    finish = "Anti-Skid",
                    color = "Slate Grey",
                    thickness = "10 mm",
                    material = "Full Body Vitrified",
                    application = "Outdoor & Bathroom",
                    warehouse = "Depot Beta - West",
                    rackLocation = "Rack C-02",
                    boxesAvailable = 210,
                    reservedBoxes = 15,
                    piecesPerBox = 4,
                    sqftPerBox = 15.5,
                    purchasePriceINR = 620.0,
                    sellingPriceINR = 980.0,
                    supplier = "Nitco Tiles Ltd",
                    dateAddedMs = now - (140 * dayMs),
                    lastMovementDateMs = now - (125 * dayMs), // 125 days -> Attention
                    imageUrl = "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=600&auto=format&fit=crop",
                    status = "In Stock"
                ),
                TileItemEntity(
                    sku = "AGL-WOOD-1X4-04",
                    barcode = "890400100204",
                    brand = "Asian Granito",
                    series = "Timber Craft",
                    collection = "Nordic Oak",
                    tileName = "Rustic Oak Wood Plank 8x40 inch",
                    size = "1x3.3 ft (20x100 cm)",
                    finish = "Rustic Matt",
                    color = "Warm Oak Brown",
                    thickness = "9 mm",
                    material = "Glazed Vitrified",
                    application = "Bedroom & Patio",
                    warehouse = "Warehouse Alpha - Central",
                    rackLocation = "Rack D-15",
                    boxesAvailable = 350,
                    reservedBoxes = 0,
                    piecesPerBox = 5,
                    sqftPerBox = 10.76,
                    purchasePriceINR = 710.0,
                    sellingPriceINR = 1150.0,
                    supplier = "Asian Granito India Ltd",
                    dateAddedMs = now - (220 * dayMs),
                    lastMovementDateMs = now - (210 * dayMs), // 210 days -> High Risk
                    imageUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=600&auto=format&fit=crop",
                    status = "In Stock"
                ),
                TileItemEntity(
                    sku = "SIM-ONYX-4X8-05",
                    barcode = "890500100205",
                    brand = "Simpolo",
                    series = "Slabtech",
                    collection = "Onyx Royale",
                    tileName = "Onyx Beige Polished Slab 4x8",
                    size = "4x8 ft (120x240 cm)",
                    finish = "High Gloss",
                    color = "Honey Onyx Beige",
                    thickness = "15 mm",
                    material = "Large Format Porcelain Slab",
                    application = "Accent Wall & Countertop",
                    warehouse = "Showroom Rack - Floor",
                    rackLocation = "Bay A-01",
                    boxesAvailable = 28,
                    reservedBoxes = 2,
                    piecesPerBox = 1,
                    sqftPerBox = 31.0,
                    purchasePriceINR = 2800.0,
                    sellingPriceINR = 4500.0,
                    supplier = "Simpolo Vitrified",
                    dateAddedMs = now - (420 * dayMs),
                    lastMovementDateMs = now - (410 * dayMs), // 410 days -> DEAD STOCK!
                    imageUrl = "https://images.unsplash.com/photo-1502005229762-cf1b2da7c5d6?w=600&auto=format&fit=crop",
                    status = "In Stock"
                ),
                TileItemEntity(
                    sku = "VAR-CEMT-2X4-06",
                    barcode = "890600100206",
                    brand = "Varmora",
                    series = "Metropolitan",
                    collection = "Urban Cement",
                    tileName = "Industrial Grey Cement Matt 2x4",
                    size = "2x4 ft (60x120 cm)",
                    finish = "Matte",
                    color = "Concrete Grey",
                    thickness = "9 mm",
                    material = "Glazed Vitrified",
                    application = "Commercial & Living",
                    warehouse = "Depot Beta - West",
                    rackLocation = "Rack E-04",
                    boxesAvailable = 195,
                    reservedBoxes = 0,
                    piecesPerBox = 2,
                    sqftPerBox = 15.5,
                    purchasePriceINR = 780.0,
                    sellingPriceINR = 1280.0,
                    supplier = "Varmora Granito",
                    dateAddedMs = now - (380 * dayMs),
                    lastMovementDateMs = now - (370 * dayMs), // 370 days -> DEAD STOCK!
                    imageUrl = "https://images.unsplash.com/photo-1518640467707-6811f4a6ab73?w=600&auto=format&fit=crop",
                    status = "In Stock"
                ),
                TileItemEntity(
                    sku = "ORI-MOSA-1X1-07",
                    barcode = "890700100207",
                    brand = "Orientbell",
                    series = "Craftsman",
                    collection = "Moroccan Hexagon",
                    tileName = "Moroccan Blue Accent Mosaic 1x1",
                    size = "1x1 ft (30x30 cm)",
                    finish = "Satin",
                    color = "Royal Cobalt Blue",
                    thickness = "8 mm",
                    material = "Ceramic Wall Tile",
                    application = "Kitchen Backsplash & Wall",
                    warehouse = "Warehouse Alpha - Central",
                    rackLocation = "Rack F-09",
                    boxesAvailable = 64,
                    reservedBoxes = 0,
                    piecesPerBox = 10,
                    sqftPerBox = 10.0,
                    purchasePriceINR = 450.0,
                    sellingPriceINR = 750.0,
                    supplier = "Orientbell Ltd",
                    dateAddedMs = now - (25 * dayMs),
                    lastMovementDateMs = now - (18 * dayMs), // 18 days -> Healthy
                    imageUrl = "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=600&auto=format&fit=crop",
                    status = "In Stock"
                ),
                TileItemEntity(
                    sku = "KAJ-MARB-2X2-08",
                    barcode = "890100100208",
                    brand = "Kajaria",
                    series = "Elegance",
                    collection = "Botticino Classic",
                    tileName = "Botticino Beige Polished 2x2",
                    size = "2x2 ft (60x60 cm)",
                    finish = "Glossy",
                    color = "Cream Botticino",
                    thickness = "9 mm",
                    material = "GVT Vitrified",
                    application = "Living Room & Corridor",
                    warehouse = "Warehouse Alpha - Central",
                    rackLocation = "Rack A-02",
                    boxesAvailable = 6,
                    reservedBoxes = 0,
                    piecesPerBox = 4,
                    sqftPerBox = 15.5,
                    purchasePriceINR = 580.0,
                    sellingPriceINR = 920.0,
                    supplier = "Kajaria Ceramics Ltd",
                    dateAddedMs = now - (90 * dayMs),
                    lastMovementDateMs = now - (85 * dayMs), // 85 days -> Attention, Low Stock alert!
                    imageUrl = "https://images.unsplash.com/photo-1615873968403-89e068629265?w=600&auto=format&fit=crop",
                    status = "Low Stock"
                )
            )

            dao.insertTiles(seedTiles)

            // Seed initial movement logs
            dao.insertMovement(
                StockMovementEntity(
                    tileId = 1,
                    tileSku = "KAJ-STAT-2X4-01",
                    tileName = "Statuario Gold Polished 2x4",
                    movementType = "RECEIVE",
                    boxesCount = 50,
                    fromLocation = "Kajaria Factory - Morbi",
                    toLocation = "Warehouse Alpha - Central (Rack A-12)",
                    notes = "PO #9821 Inward verification completed",
                    performedBy = "Rajesh Sharma (Warehouse Mgr)"
                )
            )

            dao.insertMovement(
                StockMovementEntity(
                    tileId = 3,
                    tileSku = "NIT-SLAT-2X2-03",
                    tileName = "Slate Grey Anti-Skid 2x2",
                    movementType = "ISSUE",
                    boxesCount = 15,
                    fromLocation = "Depot Beta - West (Rack C-02)",
                    toLocation = "Prestige Heights Villa Site",
                    notes = "Invoice #8839 Dispatch to Site",
                    performedBy = "Amit Verma (Sales Exec)"
                )
            )
        }
    }
}
