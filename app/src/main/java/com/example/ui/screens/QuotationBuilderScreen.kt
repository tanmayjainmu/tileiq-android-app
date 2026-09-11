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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CartItem
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import androidx.compose.material.icons.filled.Payment
import androidx.compose.ui.platform.LocalContext
import com.example.util.CurrencyFormatter
import com.example.util.GooglePayUpiHelper

@Composable
fun QuotationBuilderScreen(
    cartItems: List<CartItem>,
    onUpdateQuantity: (Long, Int) -> Unit,
    onCreateQuotation: (String, String, Double, (String) -> Unit) -> Unit,
    onClose: () -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var discountInput by remember { mutableStateOf("0") }
    var quotationCreatedNum by remember { mutableStateOf<String?>(null) }

    val totalBeforeDiscount = cartItems.sumOf { it.tile.sellingPriceINR * it.boxesCount }
    val discountVal = discountInput.toDoubleOrNull() ?: 0.0
    val netAmount = (totalBeforeDiscount - discountVal).coerceAtLeast(0.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(16.dp)
            .testTag("quotation_builder_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quotation Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "QUOTATION BUILDER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Instant Customer Estimate (INR)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(onClick = onClose) {
                    Text("Close", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (cartItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No items added to quotation", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Search tiles and click 'Quote' to build a customer proposal.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        } else {
            // Customer Details Form
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("CUSTOMER DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Customer Name") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate900,
                                unfocusedContainerColor = Slate900,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("cust_name_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("WhatsApp Phone Number (+91)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate900,
                                unfocusedContainerColor = Slate900,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("cust_phone_input")
                        )
                    }
                }
            }

            // Items List
            item {
                Text("SELECTED TILE SKUs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            }

            items(cartItems) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Slate700, RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.tile.tileName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                            Text("${item.tile.brand} • ${CurrencyFormatter.formatINR(item.tile.sellingPriceINR)}/box", fontSize = 11.sp, color = Color.Gray)
                            Text("Subtotal: ${CurrencyFormatter.formatINR(item.tile.sellingPriceINR * item.boxesCount)}", fontWeight = FontWeight.Bold, color = Emerald500, fontSize = 12.sp)
                        }

                        // Quantity adjustment controls
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onUpdateQuantity(item.tile.id, item.boxesCount - 1) }) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White)
                            }
                            Text("${item.boxesCount}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            IconButton(onClick = { onUpdateQuantity(item.tile.id, item.boxesCount + 1) }) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = Color.White)
                            }
                        }
                    }
                }
            }

            // Financial Summary & Action
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", color = Color.LightGray)
                            Text(CurrencyFormatter.formatINR(totalBeforeDiscount), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = discountInput,
                            onValueChange = { discountInput = it },
                            label = { Text("Festive Discount (₹)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate900,
                                unfocusedContainerColor = Slate900,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = Slate700)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Net Total Amount", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            Text(CurrencyFormatter.formatINR(netAmount), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Emerald500)
                        }

                        val context = LocalContext.current

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onCreateQuotation(customerName, customerPhone, discountVal) { qNum ->
                                    quotationCreatedNum = qNum
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("save_quotation_button")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate & Share WhatsApp Quotation", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                val ref = quotationCreatedNum ?: ("QT_" + System.currentTimeMillis())
                                GooglePayUpiHelper.launchGooglePayUpiIntent(
                                    context = context,
                                    payeeVpa = GooglePayUpiHelper.DEFAULT_SHOWROOM_VPA,
                                    payeeName = GooglePayUpiHelper.DEFAULT_SHOWROOM_NAME,
                                    amountINR = netAmount,
                                    transactionRefId = ref,
                                    transactionNote = "Deposit for Tile Quotation $ref",
                                    targetApp = "Google Pay"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("gpay_collect_button")
                        ) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Collect Payment via Google Pay (UPI)", fontWeight = FontWeight.Bold)
                        }

                        if (quotationCreatedNum != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Quotation #$quotationCreatedNum generated and saved to history!",
                                color = Emerald500,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
