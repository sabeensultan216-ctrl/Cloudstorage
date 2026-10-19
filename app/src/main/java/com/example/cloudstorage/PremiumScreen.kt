package com.example.cloudstorage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloudstorage.ui.theme.CloudStorageTheme

// ── Models ────────────────────────────────────────────────────────────────────

private enum class BillingCycle { MONTHLY, YEARLY }

private data class PremiumFeature(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconBrush: Brush
)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun PremiumScreen(
    onBack: () -> Unit = {},
    onUpgrade: () -> Unit = {}
) {
    var selectedCycle by remember { mutableStateOf(BillingCycle.YEARLY) }

    val primaryGradient = Brush.horizontalGradient(
        listOf(Color(0xFF0088FF), Color(0xFF8B5CF6))
    )

    val features = listOf(
        PremiumFeature(
            title = "More Storage",
            subtitle = "Get up to 2 TB of storage",
            icon = Icons.Filled.Cloud,
            iconBrush = Brush.verticalGradient(listOf(Color(0xFF38BDF8), Color(0xFF7C3AED)))
        ),
        PremiumFeature(
            title = "Faster Uploads",
            subtitle = "Save time with high-speed uploads",
            icon = Icons.Filled.Bolt,
            iconBrush = Brush.verticalGradient(listOf(Color(0xFF2563EB), Color(0xFF60A5FA)))
        ),
        PremiumFeature(
            title = "No Ads",
            subtitle = "Enjoy a clean, distraction-free experience",
            icon = Icons.Filled.Block,
            iconBrush = Brush.verticalGradient(listOf(Color(0xFF4F46E5), Color(0xFF818CF8)))
        ),
        PremiumFeature(
            title = "Secure Vault",
            subtitle = "Keep your sensitive files extra safe",
            icon = Icons.Filled.Shield,
            iconBrush = Brush.verticalGradient(listOf(Color(0xFF6366F1), Color(0xFFA855F7)))
        )
    )

    Scaffold(containerColor = Color.White) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Ambient soft background glows (sky blue on top-left, lavender on top-right)
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.TopStart)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF38BDF8).copy(alpha = 0.22f), Color.Transparent)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.TopEnd)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFA855F7).copy(alpha = 0.20f), Color.Transparent)
                        )
                    )
            )

            // ── Single screen layout (no scrolling, fits perfectly) ───────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp)
                    .padding(top = 4.dp, bottom = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Section 1: Illustration + Title + Subtitle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Illustration with soft ambient blur background glow
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(134.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Soft blurred radial glow aura directly behind cloud
                        Box(
                            modifier = Modifier
                                .size(width = 250.dp, height = 110.dp)
                                .align(Alignment.Center)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF38BDF8).copy(alpha = 0.35f),
                                            Color(0xFFA855F7).copy(alpha = 0.22f),
                                            Color(0xFF818CF8).copy(alpha = 0.08f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Warm golden ambient aura right behind crown
                        Box(
                            modifier = Modifier
                                .size(width = 110.dp, height = 65.dp)
                                .align(Alignment.TopCenter)
                                .padding(top = 2.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFBBF24).copy(alpha = 0.30f),
                                            Color(0xFFF59E0B).copy(alpha = 0.12f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Ambient soft cyan flare on left
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .align(Alignment.CenterStart)
                                .padding(start = 12.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF38BDF8).copy(alpha = 0.22f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Ambient soft lilac flare on right
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .align(Alignment.CenterEnd)
                                .padding(end = 12.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFA855F7).copy(alpha = 0.22f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Hero cloud & crown illustration with transparent feathered background
                        Image(
                            painter = painterResource(id = R.drawable.img_premium_cloud_crown),
                            contentDescription = "Premium Crown Cloud",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Go ",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Premium",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            style = TextStyle(
                                brush = Brush.horizontalGradient(
                                    listOf(Color(0xFF007AFF), Color(0xFF7C3AED), Color(0xFF8B5CF6))
                                )
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Unlock more possibilities with CloudBox",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }

                // Section 2: 4 Feature Rows
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    features.forEach { feature ->
                        FeatureRow(feature = feature)
                    }
                }

                // Section 3: Monthly / Yearly Pill Switcher
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(3.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // Monthly tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .then(
                                    if (selectedCycle == BillingCycle.MONTHLY)
                                        Modifier.background(primaryGradient)
                                    else Modifier
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { selectedCycle = BillingCycle.MONTHLY },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Monthly",
                                fontSize = 13.sp,
                                fontWeight = if (selectedCycle == BillingCycle.MONTHLY) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedCycle == BillingCycle.MONTHLY) Color.White else Color(0xFF64748B)
                            )
                        }

                        // Yearly tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .then(
                                    if (selectedCycle == BillingCycle.YEARLY)
                                        Modifier.background(primaryGradient)
                                    else Modifier
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { selectedCycle = BillingCycle.YEARLY },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Yearly",
                                fontSize = 13.sp,
                                fontWeight = if (selectedCycle == BillingCycle.YEARLY) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedCycle == BillingCycle.YEARLY) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }

                // Section 4: Pricing Cards (Side by side, no overlap)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PricePlanCard(
                        title = "Monthly",
                        price = "$4.99",
                        unit = "per month",
                        badge = null,
                        isSelected = selectedCycle == BillingCycle.MONTHLY,
                        gradient = primaryGradient,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedCycle = BillingCycle.MONTHLY }
                    )

                    PricePlanCard(
                        title = "Yearly",
                        price = "$29.99",
                        unit = "per year",
                        badge = "Save 49%",
                        isSelected = selectedCycle == BillingCycle.YEARLY,
                        gradient = primaryGradient,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedCycle = BillingCycle.YEARLY }
                    )
                }

                // Section 5: Upgrade Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(primaryGradient)
                        .clickable { onUpgrade() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Upgrade Now",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // ── Back Button ────────────────────────────────────────────────
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 8.dp, top = 2.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// ── Feature Row ───────────────────────────────────────────────────────────────

@Composable
private fun FeatureRow(feature: PremiumFeature) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFF6FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(brush = feature.iconBrush, blendMode = BlendMode.SrcAtop)
                        }
                    }
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = feature.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = feature.subtitle,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

// ── Price Plan Card (Clean, perfectly aligned without overlap) ────────────────

@Composable
private fun PricePlanCard(
    title: String,
    price: String,
    unit: String,
    badge: String?,
    isSelected: Boolean,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(98.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) Color(0xFFF0F7FF) else Color(0xFFFAFAFC))
            .then(
                if (isSelected)
                    Modifier.border(2.dp, gradient, RoundedCornerShape(16.dp))
                else
                    Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Title + Badge (Clean layout, prevents any overlap)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (badge != null) Arrangement.SpaceBetween else Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B),
                    modifier = if (badge != null) Modifier.padding(start = 2.dp) else Modifier
                )
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFDE047))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }

            // Middle: Price
            Text(
                text = price,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A)
            )

            // Bottom: Unit
            Text(
                text = unit,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun PremiumScreenPreview() {
    CloudStorageTheme {
        PremiumScreen()
    }
}
