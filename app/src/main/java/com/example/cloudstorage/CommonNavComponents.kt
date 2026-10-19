package com.example.cloudstorage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.cloudstorage.ui.theme.CloudBlueSplash
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloudstorage.ui.theme.CloudBlue
import com.example.cloudstorage.ui.theme.CloudBlueLight
import com.example.cloudstorage.ui.theme.NeutralDark
import com.example.cloudstorage.ui.theme.NeutralMedium
import com.example.cloudstorage.ui.theme.NeutralWhite

// ── Bottom Nav Item Data ──────────────────────────────────────────────────────

data class NavTabItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

val defaultBottomNavTabs = listOf(
    NavTabItem("Home", Icons.Outlined.Home, Icons.Default.Home),
    NavTabItem("Files", Icons.Outlined.Folder, Icons.Default.Folder),
    NavTabItem("Shared", Icons.Outlined.People, Icons.Default.People),
    NavTabItem("Profile", Icons.Outlined.Person, Icons.Default.Person)
)

@Composable
fun getLocalizedBottomNavTabs(): List<NavTabItem> = listOf(
    NavTabItem(stringResource(R.string.nav_home), Icons.Outlined.Home, Icons.Default.Home),
    NavTabItem(stringResource(R.string.nav_files), Icons.Outlined.Folder, Icons.Default.Folder),
    NavTabItem(stringResource(R.string.nav_shared), Icons.Outlined.People, Icons.Default.People),
    NavTabItem(stringResource(R.string.nav_profile), Icons.Outlined.Person, Icons.Default.Person)
)

// ── Shared App Top Header ─────────────────────────────────────────────────────

@Composable
fun AppTopHeader(
    greeting: String = "Good morning,\nAlex! 👋",
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9FAFC))
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Greeting with dark navy text
        Text(
            text = greeting,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            lineHeight = 32.sp
        )

        // Right: Notification Bell and Profile Avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Notification Bell with Cyan Badge Dot
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(24.dp)
                )
                // Cyan/blue notification dot on top right
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = 8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00A3FF))
                        .border(1.5.dp, Color.White, CircleShape)
                )
            }

            // Profile Avatar with Blue-to-Purple Gradient Accent
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF00B2FE), // Vivid cyan / blue
                                Color(0xFF8A2BE2)  // Vivid purple
                            )
                        )
                    )
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ── Shared Bottom Navigation Bar ──────────────────────────────────────────────

@Composable
fun AppBottomNav(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val tabs = getLocalizedBottomNavTabs()
    NavigationBar(
        containerColor = NeutralWhite,
        tonalElevation = 8.dp,
        modifier = Modifier.shadow(8.dp)
    ) {
        tabs.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Color(0xFF007AFF),
                    selectedTextColor   = Color(0xFF007AFF),
                    indicatorColor      = Color.Transparent,
                    unselectedIconColor = Color(0xFF94A3B8),
                    unselectedTextColor = Color(0xFF94A3B8)
                )
            )
        }
    }
}

// ── Shared Sidebar Drawer ─────────────────────────────────────────────────────

data class DrawerNavItem(val label: String, val icon: ImageVector, val id: String = label)

val defaultDrawerNavItems = listOf(
    DrawerNavItem("Home",             Icons.Default.Home),
    DrawerNavItem("My Files",         Icons.Default.Folder),
    DrawerNavItem("Shared",           Icons.Default.People),
    DrawerNavItem("Starred",          Icons.Default.Star),
    DrawerNavItem("Upload",           Icons.Default.CloudQueue),
    DrawerNavItem("Auto Backup",      Icons.Default.Sync),
    DrawerNavItem("Offline Files",    Icons.Default.OfflinePin),
    DrawerNavItem("Go Premium",       Icons.Default.AutoAwesome),
)

@Composable
fun getLocalizedDrawerNavItems(): List<DrawerNavItem> = listOf(
    DrawerNavItem(stringResource(R.string.nav_home),             Icons.Default.Home,         "Home"),
    DrawerNavItem(stringResource(R.string.menu_my_files),         Icons.Default.Folder,       "My Files"),
    DrawerNavItem(stringResource(R.string.menu_shared),           Icons.Default.People,       "Shared"),
    DrawerNavItem(stringResource(R.string.my_files_starred),      Icons.Default.Star,         "Starred"),
    DrawerNavItem(stringResource(R.string.menu_upload),           Icons.Default.CloudQueue,   "Upload"),
    DrawerNavItem(stringResource(R.string.menu_auto_backup),      Icons.Default.Sync,         "Auto Backup"),
    DrawerNavItem(stringResource(R.string.menu_offline_files),    Icons.Default.OfflinePin,   "Offline Files"),
    DrawerNavItem(stringResource(R.string.menu_go_premium),       Icons.Default.AutoAwesome,  "Go Premium"),
)

@Composable
fun AppDrawer(
    items: List<DrawerNavItem> = getLocalizedDrawerNavItems(),
    selectedIndex: Int = 0,
    onItemSelected: (Int) -> Unit,
    onClose: () -> Unit = {}
) {
    ModalDrawerSheet(
        drawerContainerColor = NeutralWhite,
        modifier = Modifier
            .fillMaxHeight()
            .width(290.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Drawer header — gradient with logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(CloudBlueSplash, CloudBlue)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_cloudbox_logo),
                        contentDescription = "CloudBox",
                        modifier = Modifier.size(68.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "CloudBox",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralWhite,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = stringResource(R.string.splash_tagline),
                        fontSize = 12.sp,
                        color = NeutralWhite.copy(alpha = 0.72f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Nav items — top section and bottom section
            val bottomLabels = setOf("Profile", "Settings", "About")
            val topItems = items.filter { it.id !in bottomLabels }
            val bottomItems = items.filter { it.id in bottomLabels }

            topItems.forEach { item ->
                val index = items.indexOf(item)
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 15.sp,
                            fontWeight = if (selectedIndex == index) FontWeight.SemiBold
                                         else FontWeight.Normal
                        )
                    },
                    selected = selectedIndex == index,
                    onClick = { onItemSelected(index) },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = CloudBlueLight,
                        selectedIconColor = CloudBlue,
                        selectedTextColor = CloudBlue,
                        unselectedIconColor = NeutralMedium,
                        unselectedTextColor = NeutralDark
                    )
                )
            }

            if (bottomItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = Color(0xFFE2E8F0)
                )
                Spacer(modifier = Modifier.height(8.dp))

                bottomItems.forEach { item ->
                    val index = items.indexOf(item)
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 15.sp,
                                fontWeight = if (selectedIndex == index) FontWeight.SemiBold
                                             else FontWeight.Normal
                            )
                        },
                        selected = selectedIndex == index,
                        onClick = { onItemSelected(index) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = CloudBlueLight,
                            selectedIconColor = CloudBlue,
                            selectedTextColor = CloudBlue,
                            unselectedIconColor = NeutralMedium,
                            unselectedTextColor = NeutralDark
                        )
                    )
                }
            }

            // App version at the very bottom
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Version 1.0.0",
                fontSize = 11.sp,
                color = NeutralMedium.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
            )
        }
    }
}