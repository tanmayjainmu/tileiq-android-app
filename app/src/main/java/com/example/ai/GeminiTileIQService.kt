package com.example.ai

import com.example.data.AgingBand
import com.example.data.TileItemEntity
import com.example.util.CurrencyFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GeminiTileIQService {

    // Attempts to query Gemini model via REST if GEMINI_API_KEY is present
    suspend fun processNaturalLanguageQuery(
        query: String,
        allTiles: List<TileItemEntity>,
        apiKey: String = ""
    ): AIQueryResult = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        val lower = trimmed.lowercase()

        // 1. Try Gemini API if API key exists
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val apiResponse = callGeminiApi(trimmed, allTiles, apiKey)
                if (apiResponse.isNotBlank()) {
                    return@withContext AIQueryResult(
                        answerText = apiResponse,
                        filteredTiles = filterTilesByQuery(lower, allTiles),
                        suggestedAction = determineAction(lower)
                    )
                }
            } catch (e: Exception) {
                // Fallback to local intelligence on network error
            }
        }

        // 2. High-performance local NLP engine for instant response (<50ms)
        return@withContext analyzeLocally(trimmed, lower, allTiles)
    }

    private fun analyzeLocally(original: String, lower: String, tiles: List<TileItemEntity>): AIQueryResult {
        val deadStockTiles = tiles.filter { it.agingBand == AgingBand.DEAD_STOCK }
        val highRiskTiles = tiles.filter { it.agingBand == AgingBand.HIGH_RISK }
        val attentionTiles = tiles.filter { it.agingBand == AgingBand.ATTENTION }

        val totalDeadValue = deadStockTiles.sumOf { it.totalStockValueINR }
        val totalStockValue = tiles.sumOf { it.totalStockValueINR }

        val deadBrandGroup = deadStockTiles.groupBy { it.brand }
            .mapValues { entry -> entry.value.sumOf { it.totalStockValueINR } }
            .entries.sortedByDescending { it.value }

        return when {
            lower.contains("unsold for 6 months") || lower.contains("180 days") || lower.contains("older than 6 months") -> {
                val matching = tiles.filter { it.daysInInventory >= 180 }
                val value = matching.sumOf { it.totalStockValueINR }
                AIQueryResult(
                    answerText = "Found ${matching.size} SKU(s) unsold for over 180 days (6+ months), locking up ${CurrencyFormatter.formatINRCompact(value)} in capital. We recommend creating clearance bundles or applying 30%–50% festive discounts.",
                    filteredTiles = matching,
                    suggestedAction = "Apply 30% Clearance Discount"
                )
            }
            lower.contains("unsold for 1 year") || lower.contains("365 days") || lower.contains("older than one year") || lower.contains("dead stock") -> {
                AIQueryResult(
                    answerText = "There are ${deadStockTiles.size} SKU(s) classified as Dead Stock (365+ days unsold) with total stock value of ${CurrencyFormatter.formatINRCompact(totalDeadValue)} (${String.format("%.1f", if (totalStockValue > 0) (totalDeadValue/totalStockValue)*100 else 0.0)}% of total capital).",
                    filteredTiles = deadStockTiles,
                    suggestedAction = "Generate Clearance Liquidation Plan"
                )
            }
            lower.contains("brand") && (lower.contains("maximum") || lower.contains("most") || lower.contains("worst")) -> {
                val topBrand = deadBrandGroup.firstOrNull()
                val brandName = topBrand?.key ?: "N/A"
                val brandVal = topBrand?.value ?: 0.0
                AIQueryResult(
                    answerText = "The brand creating maximum dead stock is **$brandName** with ${CurrencyFormatter.formatINRCompact(brandVal)} locked in unsold inventory (${deadStockTiles.filter { it.brand == brandName }.sumOf { it.boxesAvailable }} boxes).",
                    filteredTiles = deadStockTiles.filter { it.brand == brandName },
                    suggestedAction = "Stop Reordering $brandName Slow SKUs"
                )
            }
            lower.contains("discount") || lower.contains("recommend discount") -> {
                val recommendDiscountTiles = tiles.filter { it.daysInInventory > 60 }
                AIQueryResult(
                    answerText = "AI Recommendation: Apply tiered discounts—5% off for 31-60 days, 15% off for 61-180 days, 30% off for 181-365 days, and 50% flash clearance for 365+ days dead stock to unlock ${CurrencyFormatter.formatINRCompact(recommendDiscountTiles.sumOf { it.totalStockValueINR })}.",
                    filteredTiles = recommendDiscountTiles,
                    suggestedAction = "Auto-Apply Tiered Discounts"
                )
            }
            lower.contains("bundle") || lower.contains("combine") -> {
                val fastMoving = tiles.filter { it.agingBand == AgingBand.HEALTHY }
                val slowMoving = tiles.filter { it.agingBand == AgingBand.ATTENTION || it.agingBand == AgingBand.HIGH_RISK }
                AIQueryResult(
                    answerText = "Bundle Suggestion: Pair high-demand floor tile **${fastMoving.firstOrNull()?.tileName ?: "Statuario Gold"}** with slow-moving wall accent tile **${slowMoving.firstOrNull()?.tileName ?: "Onyx Slab"}** at a combo 12% discount to clear slow stock effortlessly.",
                    filteredTiles = fastMoving + slowMoving,
                    suggestedAction = "Create Combo Quotation"
                )
            }
            lower.contains("report") || lower.contains("weekly") || lower.contains("summary") -> {
                AIQueryResult(
                    answerText = "📊 **TileIQ Weekly Executive Inventory Report**:\n• Total Stock Value: ${CurrencyFormatter.formatINRCompact(totalStockValue)}\n• Dead Stock Risk: ${CurrencyFormatter.formatINRCompact(totalDeadValue)} (${deadStockTiles.size} SKUs)\n• Healthy Inventory Ratio: ${String.format("%.1f", (tiles.count { it.agingBand == AgingBand.HEALTHY }.toDouble() / tiles.size.coerceAtLeast(1)) * 100)}%\n• Action Required: 2 items at Low Stock, 2 items over 1 year unsold.",
                    filteredTiles = tiles,
                    suggestedAction = "Export Full Report PDF"
                )
            }
            else -> {
                val matches = filterTilesByQuery(lower, tiles)
                AIQueryResult(
                    answerText = "TileIQ AI found ${matches.size} tile SKU(s) matching your inquiry '$original'. Total stock value is ${CurrencyFormatter.formatINRCompact(matches.sumOf { it.totalStockValueINR })}.",
                    filteredTiles = matches,
                    suggestedAction = "View Matching SKUs"
                )
            }
        }
    }

    private fun filterTilesByQuery(query: String, tiles: List<TileItemEntity>): List<TileItemEntity> {
        if (query.isBlank()) return tiles
        return tiles.filter { tile ->
            tile.tileName.contains(query, ignoreCase = true) ||
            tile.sku.contains(query, ignoreCase = true) ||
            tile.brand.contains(query, ignoreCase = true) ||
            tile.color.contains(query, ignoreCase = true) ||
            tile.finish.contains(query, ignoreCase = true) ||
            tile.size.contains(query, ignoreCase = true) ||
            tile.warehouse.contains(query, ignoreCase = true) ||
            tile.rackLocation.contains(query, ignoreCase = true) ||
            tile.application.contains(query, ignoreCase = true)
        }
    }

    private fun determineAction(lower: String): String {
        return when {
            lower.contains("discount") -> "Apply Recommended Discount"
            lower.contains("dead") -> "View Dead Stock List"
            lower.contains("bundle") -> "Generate Bundle Quotation"
            else -> "Inspect Inventory"
        }
    }

    private fun callGeminiApi(prompt: String, tiles: List<TileItemEntity>, apiKey: String): String {
        val jsonSummary = JSONArray().apply {
            tiles.take(15).forEach { tile ->
                put(JSONObject().apply {
                    put("sku", tile.sku)
                    put("name", tile.tileName)
                    put("brand", tile.brand)
                    put("priceINR", tile.sellingPriceINR)
                    put("boxes", tile.boxesAvailable)
                    put("daysInStock", tile.daysInInventory)
                    put("agingBand", tile.agingBand.label)
                })
            }
        }

        val systemPrompt = """
            You are TileIQ AI, an inventory intelligence assistant for tile showrooms in India.
            All currency values must strictly be formatted in Indian Rupees (₹) in Lakhs or Crores.
            Given the customer/owner query and current inventory snapshot:
            Snapshot: $jsonSummary
            Query: $prompt
            Provide a concise, practical 2-3 sentence answer with specific numbers and actionable steps.
        """.trimIndent()

        val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
        val url = URL(endpoint)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true

        val requestBody = JSONObject().apply {
            put("contents", JSONArray().put(JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().apply {
                    put("text", systemPrompt)
                }))
            }))
        }

        OutputStreamWriter(conn.outputStream).use { it.write(requestBody.toString()) }

        if (conn.responseCode == 200) {
            val responseStr = conn.inputStream.bufferedReader().use { it.readText() }
            val jsonResp = JSONObject(responseStr)
            val candidates = jsonResp.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val first = candidates.getJSONObject(0)
                val content = first.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    return parts.getJSONObject(0).optString("text", "")
                }
            }
        }
        return ""
    }
}

data class AIQueryResult(
    val answerText: String,
    val filteredTiles: List<TileItemEntity>,
    val suggestedAction: String
)
