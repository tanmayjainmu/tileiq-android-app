package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AgingBand
import com.example.data.TileItemEntity
import com.example.ui.SubscriptionPlan
import com.example.ui.UserRole
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.CurrencyFormatter

@Composable
fun RoleHeaderBar(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    cartItemCount: Int,
    onOpenCart: () -> Unit,
    subscriptionPlan: SubscriptionPlan? = null,
    onOpenSubscriptionSettings: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        color = Slate900,
        contentColor = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Slate800)
                    .clickable { expanded = true }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("role_switcher_dropdown")
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (currentRole) {
                            UserRole.SHOWROOM_OWNER -> Icons.Default.BusinessCenter
                            UserRole.SALESPERSON -> Icons.Default.Person
                            UserRole.WAREHOUSE_MANAGER -> Icons.Default.Inventory
                        },
                        contentDescription = "Role Icon",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentRole.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = "Select Role",
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "TileIQ Showroom",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Slate800)
                ) {
                    UserRole.entries.forEach { role ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = role.title,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = role.subtitle,
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            },
                            onClick = {
                                onRoleSelected(role)
                                expanded = false
                            },
                            modifier = Modifier.testTag("role_item_${role.name}")
                        )
                    }
                }
            }

            // Right side: Pro Subscription Badge + Quotation Cart
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onOpenSubscriptionSettings != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(com.example.ui.theme.Emerald500.copy(alpha = 0.2f))
                            .border(1.dp, com.example.ui.theme.Emerald500, RoundedCornerShape(20.dp))
                            .clickable { onOpenSubscriptionSettings() }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("header_subscription_badge")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(com.example.ui.theme.Emerald500)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PRO ₹5k/mo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = com.example.ui.theme.Emerald500
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (cartItemCount > 0) {
                    IconButton(
                        onClick = onOpenCart,
                        modifier = Modifier.testTag("open_cart_button")
                    ) {
                        BadgedBox(
                            badge = {
                                Badge {
                                    Text(cartItemCount.toString())
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = "Quotation Cart",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KPICard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Slate800),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .border(1.dp, Slate700, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun AgingBadge(agingBand: AgingBand) {
    val bg = Color(agingBand.colorHex).copy(alpha = 0.18f)
    val textCol = Color(agingBand.colorHex)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textCol)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${agingBand.label} (${agingBand.rangeLabel})",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textCol
            )
        }
    }
}

@Composable
fun InstantSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String = "Search SKU, Name, 2x4, White Gloss, Kajaria...",
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                placeholderText,
                fontSize = 13.sp,
                color = Color.Gray
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Text(
                    text = "Clear",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onClear() }
                        .padding(horizontal = 8.dp)
                )
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Slate800,
            unfocusedContainerColor = Slate800,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Slate700,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("instant_search_input")
    )
}
