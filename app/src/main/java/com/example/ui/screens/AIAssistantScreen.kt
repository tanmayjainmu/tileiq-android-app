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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ai.AIQueryResult
import com.example.data.TileItemEntity
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun AIAssistantScreen(
    aiResult: AIQueryResult?,
    isLoading: Boolean,
    onAskAI: (String) -> Unit,
    onSelectTile: (TileItemEntity) -> Unit
) {
    var promptInput by remember { mutableStateOf("") }

    val presetQueries = listOf(
        "Show me tiles unsold for 6 months",
        "Which brand has maximum dead stock?",
        "How much inventory is older than 1 year?",
        "Recommend discount strategy for high risk SKUs",
        "Suggest products to bundle for festive sale",
        "Generate weekly inventory executive report"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(16.dp)
            .testTag("ai_assistant_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Indigo500),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "TileIQ AI Natural Language Intelligence",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Ask questions in natural language about stock, aging, or discounts",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Search Input Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Indigo500.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        placeholder = { Text("Ask TileIQ AI anything...", color = Color.Gray, fontSize = 13.sp) },
                        trailingIcon = {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Indigo500,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Button(
                                    onClick = {
                                        if (promptInput.isNotBlank()) {
                                            onAskAI(promptInput)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(14.dp))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Slate900,
                            unfocusedContainerColor = Slate900,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("ai_prompt_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("PRESET PROMPTS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(presetQueries) { preset ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Slate900)
                                    .border(1.dp, Slate700, RoundedCornerShape(20.dp))
                                    .clickable {
                                        promptInput = preset
                                        onAskAI(preset)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(preset, fontSize = 11.sp, color = Color.LightGray)
                            }
                        }
                    }
                }
            }
        }

        // AI Response Result Card
        if (aiResult != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Emerald500.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .testTag("ai_result_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = Amber500)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI INSIGHT & STRATEGY", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Amber500)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = aiResult.answerText,
                            fontSize = 14.sp,
                            color = Color.White,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Indigo500)
                                .clickable { /* Suggested action triggered */ }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Recommended Action: ${aiResult.suggestedAction}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            if (aiResult.filteredTiles.isNotEmpty()) {
                item {
                    Text(
                        text = "ANALYZED TILE PRODUCTS (${aiResult.filteredTiles.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                }

                items(aiResult.filteredTiles) { tile ->
                    TileSearchCard(
                        tile = tile,
                        onClick = { onSelectTile(tile) },
                        onAddToCart = { /* Add to quote */ }
                    )
                }
            }
        }
    }
}
