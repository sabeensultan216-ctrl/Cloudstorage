package com.example.cloudstorage

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloudstorage.ui.theme.CloudStorageTheme
import com.example.cloudstorage.ui.theme.NeutralWhite

// ── Models ────────────────────────────────────────────────────────────────────

data class SettingsMenuItem(
    val title: String,
    val icon: ImageVector,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit = {}
)

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiles: () -> Unit = {},
    onNavigateToShared: () -> Unit = {},
    onNavigateToUploadManager: () -> Unit = {},
    onNavigateToAutoBackup: () -> Unit = {},
    onNavigateToOffline: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onUpgrade: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onProfileInformationClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSecurityPrivacyClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onHelpSupportClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onLogOut: () -> Unit = {}
) {
    var selectedNav by remember { mutableIntStateOf(3) } // Profile tab is active
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedDrawerItem by remember { mutableIntStateOf(-1) }
    val drawerItems = getLocalizedDrawerNavItems()

    val menuItems = listOf(
        SettingsMenuItem("Profile Information", Icons.Outlined.Person, onClick = onProfileInformationClick),
        SettingsMenuItem(stringResource(R.string.profile_notifications), Icons.Outlined.Notifications, onClick = onNotificationsClick),
        SettingsMenuItem(stringResource(R.string.profile_security_privacy), Icons.Outlined.Security, onClick = onSecurityPrivacyClick),
        SettingsMenuItem(stringResource(R.string.profile_language), Icons.Outlined.Language, onClick = onLanguageClick),
        SettingsMenuItem(stringResource(R.string.profile_help_support), Icons.AutoMirrored.Outlined.HelpOutline, onClick = onHelpSupportClick),
        SettingsMenuItem(stringResource(R.string.profile_about_cloudbox), Icons.Outlined.Info, onClick = onAboutClick),
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                items = drawerItems,
                selectedIndex = selectedDrawerItem,
                onItemSelected = { index ->
                    selectedDrawerItem = index
                    scope.launch { drawerState.close() }
                    when (drawerItems[index].id) {
                        "Home"          -> onNavigateToHome()
                        "My Files"      -> onNavigateToFiles()
                        "Shared"        -> onNavigateToShared()
                        "Upload"        -> onNavigateToUploadManager()
                        "Auto Backup"   -> onNavigateToAutoBackup()
                        "Offline Files" -> onNavigateToOffline()
                        "Go Premium"    -> onNavigateToPremium()
                        "About"         -> onAboutClick()
                        else            -> {}
                    }
                },
                onClose = { scope.launch { drawerState.close() } }
            )
        },
        scrimColor = Color.Black.copy(alpha = 0.45f)
    ) {
        Scaffold(
            containerColor = Color(0xFFF8FAFC),
            bottomBar = {
                AppBottomNav(
                    selectedIndex = selectedNav,
                    onItemSelected = { index ->
                        selectedNav = index
                        when (index) {
                            0 -> onNavigateToHome()
                            1 -> onNavigateToFiles()
                            2 -> onNavigateToShared()
                            3 -> {} // Current screen
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Ambient soft background glows (sky blue on top-left, lavender purple on top-right)
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .align(Alignment.TopStart)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF38BDF8).copy(alpha = 0.12f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFA855F7).copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // ── Single screen layout (no scrolling, fits viewport) ─────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // ── Standard Header ──────────────────────────────────────────────
                    StandardHeader(
                        title = stringResource(R.string.profile_title),
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )

                    // ── Storage Used Card ─────────────────────────────────────────
                    StorageGaugeCard(
                        percentage = 0f,
                        usedText = "0 GB",
                        totalText = "0 GB",
                        onUpgrade = onUpgrade
                    )

                    // ── Account Settings Section ─────────────────────────────────
                    Text(
                        text = stringResource(R.string.profile_account_settings),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    // ── Menu List Items (6 items, no logout) ─────────────────────
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        menuItems.forEach { item ->
                            ProfileMenuItemCard(item = item)
                        }
                    }
                }
            }
        }
    }
}

// ── Profile Header Section ────────────────────────────────────────────────────

@Composable
private fun ProfileHeaderSection() {
    Spacer(modifier = Modifier.height(16.dp))
}

// ── Feature Item Component ────────────────────────────────────────────────────

@Composable
private fun FeatureItem(
    icon: ImageVector,
    title: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ── Storage Gauge Card ────────────────────────────────────────────────────────

@Composable
private fun StorageGaugeCard(
    percentage: Float,
    usedText: String,
    totalText: String,
    onUpgrade: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Circular Progress Gauge with Center Percentage Text
            Box(
                modifier = Modifier.size(56.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidthPx = 6.dp.toPx()

                    // Background Track
                    drawArc(
                        color = Color(0xFFEEF2F6),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )

                    // Gradient Progress Arc
                    val gradientBrush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF00B4D8),
                            Color(0xFF0077B6),
                            Color(0xFF7209B7)
                        )
                    )

                    drawArc(
                        brush = gradientBrush,
                        startAngle = -90f,
                        sweepAngle = 360f * percentage,
                        useCenter = false,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "${(percentage * 100).toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Middle: Label and Storage details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_storage_used),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$usedText " + stringResource(R.string.home_of) + " $totalText",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Right: Gradient Upgrade Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF00A3FF),
                                Color(0xFF8F00FF)
                            )
                        )
                    )
                    .clickable { onUpgrade() }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.home_upgrade),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeutralWhite
                )
            }
        }
    }
}

// ── Profile Menu Item Card ────────────────────────────────────────────────────

@Composable
private fun ProfileMenuItemCard(item: SettingsMenuItem) {
    val backgroundColor = if (item.isDestructive) Color(0xFFFFF1F2) else NeutralWhite
    val borderColor = if (item.isDestructive) Color(0xFFFFE4E6) else Color(0xFFF1F5F9)
    val contentColor = if (item.isDestructive) Color(0xFFE11D48) else Color(0xFF1E293B)
    val chevronColor = if (item.isDestructive) Color(0xFFE11D48) else Color(0xFF94A3B8)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 20.dp)
            .clickable { item.onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = chevronColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun ProfileScreenPreview() {
    CloudStorageTheme {
        ProfileScreen()
    }
}