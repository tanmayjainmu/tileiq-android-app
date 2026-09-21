package com.example.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FirestoreRepository
import com.example.ui.UserRole
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Indigo500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun LoginScreen(
    onLoginSuccess: (emailOrPhone: String, name: String, role: UserRole, showroomName: String) -> Unit,
    firestoreRepo: FirestoreRepository = remember { FirestoreRepository() }
) {
    var authTab by remember { mutableIntStateOf(0) } // 0 = Phone OTP (Firebase Auth), 1 = Staff Credentials

    // Phone Auth State
    var phoneNumber by remember { mutableStateOf("+91 98765 43210") }
    var otpCode by remember { mutableStateOf("123456") }
    var verificationId by remember { mutableStateOf<String?>(null) }
    var isOtpSent by remember { mutableStateOf(false) }
    var isSendingOtp by remember { mutableStateOf(false) }
    var isVerifyingOtp by remember { mutableStateOf(false) }

    // Staff Credentials State
    var showroomIdOrEmail by remember { mutableStateOf("rajesh.gupta@tileiq.in") }
    var pinOrPass by remember { mutableStateOf("1234") }
    var showroomNameInput by remember { mutableStateOf("Gupta Ceramics & Tiles") }
    var selectedRole by remember { mutableStateOf(UserRole.SHOWROOM_OWNER) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMsg by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val activity = context as? Activity

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(20.dp)
            .testTag("login_screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo Branding
        item {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Indigo500),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BusinessCenter,
                    contentDescription = "TileIQ Logo",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "TileIQ Showroom Platform",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = Emerald500, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Firebase Auth • Cloud Firestore Sync Enabled",
                    fontSize = 11.sp,
                    color = Emerald500,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Auth Method Selector Tabs (Phone OTP vs Staff Demo Roles)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Slate800)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (authTab == 0) Emerald500 else Color.Transparent)
                        .clickable { authTab = 0 }
                        .padding(vertical = 10.dp)
                        .testTag("phone_otp_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = if (authTab == 0) Color.White else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Phone OTP Auth",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (authTab == 0) Color.White else Color.Gray
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (authTab == 1) Indigo500 else Color.Transparent)
                        .clickable { authTab = 1 }
                        .padding(vertical = 10.dp)
                        .testTag("staff_credentials_tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (authTab == 1) Color.White else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Staff Quick Login",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (authTab == 1) Color.White else Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (authTab == 0) {
            // PHONE NUMBER OTP AUTHENTICATION (FIREBASE AUTH)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Slate700, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FIREBASE PHONE AUTHENTICATION",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Emerald500
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Emerald500.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                Text("SECURE OTP", fontSize = 10.sp, color = Emerald500, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Mobile Phone Number (+91)") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Emerald500) },
                            singleLine = true,
                            enabled = !isOtpSent,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate900,
                                unfocusedContainerColor = Slate900,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("phone_number_input")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!isOtpSent) {
                            Button(
                                onClick = {
                                    if (phoneNumber.isBlank()) {
                                        errorMessage = "Please enter valid phone number with country code"
                                    } else {
                                        errorMessage = null
                                        isSendingOtp = true
                                        if (activity != null) {
                                            firestoreRepo.sendPhoneOtp(
                                                phoneNumber = phoneNumber,
                                                activity = activity,
                                                onCodeSent = { vId ->
                                                    verificationId = vId
                                                    isSendingOtp = false
                                                    isOtpSent = true
                                                    successMsg = "OTP verification code sent to $phoneNumber via SMS!"
                                                },
                                                onError = { err ->
                                                    isSendingOtp = false
                                                    errorMessage = err
                                                }
                                            )
                                        } else {
                                            // Fallback simulation mode
                                            isSendingOtp = false
                                            isOtpSent = true
                                            verificationId = "SIMULATED_V_ID"
                                            successMsg = "6-Digit SMS OTP Code sent to $phoneNumber! (Simulated: 123456)"
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("send_otp_button")
                            ) {
                                if (isSendingOtp) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sending OTP SMS...", fontSize = 13.sp)
                                } else {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Send 6-Digit OTP Code", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        } else {
                            // OTP VERIFICATION STEP
                            OutlinedTextField(
                                value = otpCode,
                                onValueChange = { if (it.length <= 6) otpCode = it },
                                label = { Text("Enter 6-Digit OTP Code") },
                                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = Emerald500) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Slate900,
                                    unfocusedContainerColor = Slate900,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_code_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (otpCode.length < 6) {
                                        errorMessage = "Please enter 6-digit OTP code"
                                    } else {
                                        errorMessage = null
                                        isVerifyingOtp = true
                                        firestoreRepo.verifyOtpCode(
                                            verificationId = verificationId ?: "",
                                            otpCode = otpCode,
                                            onSuccess = { firebaseUser ->
                                                isVerifyingOtp = false
                                                onLoginSuccess(
                                                    phoneNumber,
                                                    "Showroom Admin",
                                                    UserRole.SHOWROOM_OWNER,
                                                    showroomNameInput
                                                )
                                            },
                                            onError = { err ->
                                                isVerifyingOtp = false
                                                errorMessage = err
                                            }
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("verify_otp_button")
                            ) {
                                if (isVerifyingOtp) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verifying Credentials...", fontSize = 13.sp)
                                } else {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verify & Launch Dashboard", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    isOtpSent = false
                                    verificationId = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Change Phone Number / Resend SMS", color = Color.White, fontSize = 12.sp)
                            }
                        }

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(errorMessage!!, color = Color.Red, fontSize = 12.sp)
                        }

                        if (successMsg != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(successMsg!!, color = Emerald500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            // Preset Quick Login Accounts
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Slate700, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "SELECT QUICK DEMO LOGIN PROFILE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val presets = listOf(
                            Triple("Rajesh Gupta", UserRole.SHOWROOM_OWNER, "Showroom Owner (Pro ₹5k/mo Plan)"),
                            Triple("Rohan Sharma", UserRole.SALESPERSON, "Salesperson (Instant Quotations)"),
                            Triple("Vikram Singh", UserRole.WAREHOUSE_MANAGER, "Warehouse Manager (Rack Ops)")
                        )

                        presets.forEach { (name, role, desc) ->
                            val isSelected = selectedRole == role
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Indigo500.copy(alpha = 0.2f) else Slate900)
                                    .border(
                                        1.dp,
                                        if (isSelected) Indigo500 else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedRole = role
                                        showroomIdOrEmail = when (role) {
                                            UserRole.SHOWROOM_OWNER -> "rajesh.gupta@tileiq.in"
                                            UserRole.SALESPERSON -> "rohan.sharma@tileiq.in"
                                            UserRole.WAREHOUSE_MANAGER -> "vikram.singh@tileiq.in"
                                        }
                                    }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (role) {
                                        UserRole.SHOWROOM_OWNER -> Icons.Default.BusinessCenter
                                        UserRole.SALESPERSON -> Icons.Default.Person
                                        UserRole.WAREHOUSE_MANAGER -> Icons.Default.Inventory
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) Emerald500 else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                    Text(desc, color = Color.LightGray, fontSize = 11.sp)
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = Emerald500,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Credentials Form
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Slate700, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "SHOWROOM CREDENTIALS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = showroomNameInput,
                            onValueChange = { showroomNameInput = it },
                            label = { Text("Showroom Name") },
                            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = Indigo500) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate900,
                                unfocusedContainerColor = Slate900,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("showroom_name_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = showroomIdOrEmail,
                            onValueChange = { showroomIdOrEmail = it },
                            label = { Text("Email / Showroom ID / Phone") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Indigo500) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate900,
                                unfocusedContainerColor = Slate900,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = pinOrPass,
                            onValueChange = { pinOrPass = it },
                            label = { Text("Security PIN / Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Indigo500) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Slate900,
                                unfocusedContainerColor = Slate900,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_pin_input")
                        )

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(errorMessage!!, color = Color.Red, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (showroomIdOrEmail.isBlank()) {
                                    errorMessage = "Please enter email or showroom ID"
                                } else {
                                    onLoginSuccess(showroomIdOrEmail, "", selectedRole, showroomNameInput)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("submit_login_button")
                        ) {
                            Text(
                                text = "Login & Launch TileIQ Dashboard",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Subscription Model Card info
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Indigo500.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Indigo500.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Pro Plan",
                        tint = Emerald500,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Showroom Owner Pro License • ₹5,000 / Month (Incl. Taxes)",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Includes Sub-second Stock Lookup, AI Dead Stock Liquidator, Firebase Cloud Sync & GST Invoices.",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
