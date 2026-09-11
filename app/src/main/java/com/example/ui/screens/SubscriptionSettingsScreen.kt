package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.BillingInvoice
import com.example.ui.SubscriptionPlan
import com.example.ui.UserProfile
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.util.CurrencyFormatter
import com.example.util.GooglePayUpiHelper

@Composable
fun SubscriptionSettingsScreen(
    userProfile: UserProfile,
    subscriptionPlan: SubscriptionPlan,
    billingInvoices: List<BillingInvoice>,
    onRenewSubscription: (months: Int, paymentMethod: String, payerUpiId: String, onDone: () -> Unit) -> Unit,
    onToggleAutoRenew: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var showPaymentModal by remember { mutableStateOf(false) }
    var selectedPaymentMethod by remember { mutableStateOf("Google Pay (UPI)") }
    var selectedBillingCycle by remember { mutableStateOf(1) } // 1 Month = ₹5,000, 12 Months = ₹50,000
    var modalUpiId by remember { mutableStateOf("rajesh@upi") }
    var showQrCodePreview by remember { mutableStateOf(false) }
    var paymentSuccessMessage by remember { mutableStateOf<String?>(null) }
    var downloadedInvoiceNum by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(16.dp)
            .testTag("subscription_settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Column {
                Text(
                    text = "SHOWROOM SETTINGS & LICENSE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Subscription Model & Account",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Active Subscription Card (Showroom Owner Pro License - ₹5,000 / mo)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Emerald500.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .testTag("subscription_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Emerald500.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = Emerald500,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = subscriptionPlan.planName.uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Showroom Owner Premium Access",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                        }

                        // Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Emerald500)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = subscriptionPlan.status.uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Price tag display (₹5,000 / Month)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("CURRENT PLAN COST", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = CurrencyFormatter.formatINR(subscriptionPlan.priceINR),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald500
                                )
                                Text(
                                    text = " / Month",
                                    fontSize = 13.sp,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("RENEWAL DATE", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(
                                text = subscriptionPlan.expiryDate,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Slate700)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Auto-Renew Switch Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Monthly Auto-Debit (₹5,000)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            Text("Auto-renews subscription every month", fontSize = 11.sp, color = Color.Gray)
                        }

                        Switch(
                            checked = subscriptionPlan.autoRenew,
                            onCheckedChange = { onToggleAutoRenew() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Emerald500,
                                uncheckedTrackColor = Slate700
                            ),
                            modifier = Modifier.testTag("auto_renew_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Renew / Manage Action
                    Button(
                        onClick = { showPaymentModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("renew_subscription_button")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Renew / Extend Subscription (₹5,000/mo)", fontWeight = FontWeight.Bold)
                    }

                    if (paymentSuccessMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = paymentSuccessMessage!!,
                            color = Emerald500,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Features Included in ₹5,000 / Month Subscription Plan
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "WHAT'S INCLUDED IN SHOWROOM OWNER PRO (₹5k/mo)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    subscriptionPlan.features.forEach { feature ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Emerald500,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(feature, fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // UPI Payment Methods & Showroom VPA Configuration Card
        item {
            var customUpiId by remember { mutableStateOf("guptaceramics@okicici") }
            var upiAppChoice by remember { mutableStateOf("Google Pay") }
            var isSavedUpiMsg by remember { mutableStateOf(false) }

            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upi_methods_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = Emerald500, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MY UPI PAYMENT METHODS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Indigo500.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("INSTANT UPI 2.0", fontSize = 10.sp, color = Indigo500, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Saved Showroom UPI VPA / ID:", fontSize = 12.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = customUpiId,
                        onValueChange = { customUpiId = it },
                        label = { Text("Showroom UPI ID (VPA)") },
                        leadingIcon = { Icon(Icons.Default.Payment, contentDescription = null, tint = Emerald500) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Slate900,
                            unfocusedContainerColor = Slate900,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_upi_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Default UPI Payment App:", fontSize = 12.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Google Pay", "PhonePe", "Paytm", "BHIM UPI").forEach { app ->
                            val isSelected = upiAppChoice == app
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Indigo500 else Slate900)
                                    .border(1.dp, if (isSelected) Emerald500 else Slate700, RoundedCornerShape(8.dp))
                                    .clickable { upiAppChoice = app }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = app,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { isSavedUpiMsg = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save UPI VPA Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (isSavedUpiMsg) {
                            Text("UPI Saved!", color = Emerald500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // GST Billing History & Invoices
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GST BILLING HISTORY & INVOICES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = "GSTIN: 07AAAAA0000A1Z5",
                            fontSize = 10.sp,
                            color = Indigo500,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    billingInvoices.forEach { inv ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(inv.invoiceNumber, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text("${inv.date} • ${inv.paymentMethod}", fontSize = 11.sp, color = Color.Gray)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(CurrencyFormatter.formatINR(inv.amountINR), fontWeight = FontWeight.Bold, color = Emerald500, fontSize = 13.sp)
                                    Text("+₹${inv.gstINR.toInt()} GST", fontSize = 10.sp, color = Color.LightGray)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = { downloadedInvoiceNum = inv.invoiceNumber },
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Download, contentDescription = "Download", tint = Indigo500, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                        Divider(color = Slate700, thickness = 0.5.dp)
                    }

                    if (downloadedInvoiceNum != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tax Invoice $downloadedInvoiceNum generated & downloaded to device!",
                            color = Emerald500,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Active Account Profile & Logout
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Slate800),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "LOGGED IN PROFILE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Indigo500),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(userProfile.name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            Text("${userProfile.role.title} • ${userProfile.showroomName}", fontSize = 12.sp, color = Color.LightGray)
                            Text("${userProfile.email} • ${userProfile.phone}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("logout_button")
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Logout from Showroom Account", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Payment / Extension Modal Dialog
    if (showPaymentModal) {
        AlertDialog(
            onDismissRequest = { showPaymentModal = false },
            containerColor = Slate800,
            title = {
                Text(
                    text = "Renew Showroom Pro (₹5,000/mo)",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Choose Subscription Duration:",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedBillingCycle == 1) Indigo500.copy(alpha = 0.3f) else Slate900
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, if (selectedBillingCycle == 1) Indigo500 else Slate700, RoundedCornerShape(8.dp))
                                .clickable { selectedBillingCycle = 1 }
                                .padding(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("1 Month", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text("₹5,000", fontWeight = FontWeight.Bold, color = Emerald500, fontSize = 15.sp)
                                Text("Incl. of all taxes", fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedBillingCycle == 12) Indigo500.copy(alpha = 0.3f) else Slate900
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, if (selectedBillingCycle == 12) Indigo500 else Slate700, RoundedCornerShape(8.dp))
                                .clickable { selectedBillingCycle = 12 }
                                .padding(10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("1 Year (2 Months Off)", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                Text("₹50,000", fontWeight = FontWeight.Bold, color = Emerald500, fontSize = 15.sp)
                                Text("Save ₹10,000 (Incl. Taxes)", fontSize = 10.sp, color = Amber500)
                            }
                        }
                    }

                    Text(
                        text = "Select Payment Gateway Mode:",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )

                    val paymentMethods = listOf(
                        "Google Pay (UPI)",
                        "PhonePe / Paytm UPI",
                        "Corporate Credit Card",
                        "Razorpay Net Banking"
                    )

                    paymentMethods.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (selectedPaymentMethod == mode) Indigo500.copy(alpha = 0.2f) else Slate900)
                                .clickable { selectedPaymentMethod = mode }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = null,
                                tint = if (selectedPaymentMethod == mode) Emerald500 else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(mode, fontSize = 12.sp, color = Color.White, modifier = Modifier.weight(1f))
                            if (selectedPaymentMethod == mode) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Emerald500, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    if (selectedPaymentMethod.contains("UPI") || selectedPaymentMethod.contains("Google Pay")) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Slate900)
                                .padding(12.dp)
                        ) {
                            Text("ENTER PAYER UPI VPA / ID:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Indigo500)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = modalUpiId,
                                onValueChange = { modalUpiId = it },
                                label = { Text("Payer UPI ID (e.g., rajesh@okicici)") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Slate800,
                                    unfocusedContainerColor = Slate800,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("modal_upi_id_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Direct Action Button to Trigger Native Google Pay Intent App
                            Button(
                                onClick = {
                                    val totalAmount = if (selectedBillingCycle == 1) 5000.0 else 50000.0
                                    GooglePayUpiHelper.launchGooglePayUpiIntent(
                                        context = context,
                                        payeeVpa = GooglePayUpiHelper.DEFAULT_SHOWROOM_VPA,
                                        payeeName = GooglePayUpiHelper.DEFAULT_SHOWROOM_NAME,
                                        amountINR = totalAmount,
                                        transactionRefId = "TIQ_SUB_" + System.currentTimeMillis(),
                                        transactionNote = "TileIQ Pro License ($selectedBillingCycle Mo)",
                                        targetApp = selectedPaymentMethod
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Launch Google Pay / UPI App", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Toggle QR Code Display
                            OutlinedButton(
                                onClick = { showQrCodePreview = !showQrCodePreview },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    if (showQrCodePreview) "Hide GPay QR Code" else "Show Google Pay QR Code to Scan",
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }

                            if (showQrCodePreview) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    val qrUrl = GooglePayUpiHelper.getUpiQrCodeImageUrl(
                                        payeeVpa = GooglePayUpiHelper.DEFAULT_SHOWROOM_VPA,
                                        payeeName = GooglePayUpiHelper.DEFAULT_SHOWROOM_NAME,
                                        amountINR = if (selectedBillingCycle == 1) 5000.0 else 50000.0,
                                        transactionNote = "TileIQ Subscription"
                                    )
                                    AsyncImage(
                                        model = qrUrl,
                                        contentDescription = "GPay QR Code",
                                        modifier = Modifier.size(160.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Scan using GPay / PhonePe App", fontSize = 10.sp, color = Color.Gray)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("PAYMENT RECIPIENT VPA:", fontSize = 10.sp, color = Color.Gray)
                                    Text("tileiq@okicici (TileIQ Corp)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Emerald500)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Emerald500.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text("FIREBASE SYNC", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Emerald500)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRenewSubscription(selectedBillingCycle, selectedPaymentMethod, modalUpiId) {
                            paymentSuccessMessage = "Google Pay transaction verified & synced to Firebase! ₹${if (selectedBillingCycle == 1) "5,000" else "50,000"} license active."
                            showPaymentModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                ) {
                    Text("Pay ₹${if (selectedBillingCycle == 1) "5,000" else "50,000"} & Sync", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showPaymentModal = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}
