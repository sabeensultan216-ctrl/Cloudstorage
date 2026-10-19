package com.example.cloudstorage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloudstorage.ui.theme.CloudBlue
import com.example.cloudstorage.ui.theme.CloudBlueLight
import com.example.cloudstorage.ui.theme.CloudBlueSplash
import com.example.cloudstorage.ui.theme.CloudSky
import com.example.cloudstorage.ui.theme.NeutralDark
import com.example.cloudstorage.ui.theme.NeutralLight
import com.example.cloudstorage.ui.theme.NeutralMedium
import com.example.cloudstorage.ui.theme.NeutralWhite
import kotlinx.coroutines.launch

// ── Data models ───────────────────────────────────────────────────────────────

data class QuickAction(val label: String, val icon: ImageVector, val tint: Color, val bg: Color)

data class RecentFile(
    val name: String,
    val size: String,
    val date: String,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val isImage: Boolean = false
)

data class StorageCategory(val label: String, val used: String, val color: Color, val fraction: Float)

data class BottomNavItem(val label: String, val icon: ImageVector)

// ── Static data ───────────────────────────────────────────────────────────────

private val quickActions = listOf(
    QuickAction("Upload",     Icons.Default.CloudQueue, CloudBlue,         CloudBlueLight),
    QuickAction("New Folder", Icons.Default.FolderOpen, Color(0xFFF59E0B), Color(0xFFFEF3C7)),
    QuickAction("Share",      Icons.Default.People,     Color(0xFF10B981), Color(0xFFD1FAE5)),
    QuickAction("Starred",    Icons.Default.Star,       Color(0xFFEC4899), Color(0xFFFCE7F3)),
)

private val recentFiles = emptyList<RecentFile>()

private val storageCategories = emptyList<StorageCategory>()

private val bottomNavItems = listOf(
    BottomNavItem("Home",    Icons.Default.Home),
    BottomNavItem("Files",   Icons.Default.Folder),
    BottomNavItem("Shared",  Icons.Default.People),
    BottomNavItem("Profile", Icons.Default.Person),
)

// ── Root screen ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(
    onSignOut: () -> Unit = {},
    onNavigateToFiles: () -> Unit = {},
    onNavigateToUploadManager: () -> Unit = {},
    onNavigateToFilePreview: () -> Unit = {},
    onNavigateToShared: () -> Unit = {},
    onNavigateToAutoBackup: () -> Unit = {},
    onNavigateToOffline: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    var selectedNav by remember { mutableIntStateOf(0) }
    var selectedDrawerItem by remember { mutableIntStateOf(0) }
    var showShortcutsPopup by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val drawerItems = getLocalizedDrawerNavItems()

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
                        "Home"          -> {}
                        "My Files"      -> onNavigateToFiles()
                        "Upload"        -> onNavigateToUploadManager()
                        "Shared"        -> onNavigateToShared()
                        "Auto Backup"   -> onNavigateToAutoBackup()
                        "Offline Files" -> onNavigateToOffline()
                        "Go Premium"    -> onNavigateToPremium()
                        "Profile"       -> onNavigateToProfile()
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
                            1 -> onNavigateToFiles()
                            2 -> onNavigateToShared()
                            3 -> onNavigateToProfile()
                            else -> {}
                        }
                    }
                )
            },
            floatingActionButton = {
                AnimatedFloatingActionButton(
                    onClick = { showShortcutsPopup = true },
                    isExpanded = showShortcutsPopup
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Top header (now uses standard component)
                item {
                    StandardHeader(
                        title = "Home",
                        showLogo = true,
                        showSearch = false,
                        showNotifications = false,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                }

                // Storage card
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        StorageCard()
                    }
                }

                // Quick actions
                item {
                    SectionTitle(title = stringResource(R.string.home_quick_actions), modifier = Modifier.padding(top = 20.dp))
                    QuickActionsRow(onUploadClick = onNavigateToUploadManager)
                }

                // Recent files header
                item {
                    SectionTitle(
                        title = stringResource(R.string.home_recent_files),
                        actionLabel = stringResource(R.string.home_see_all),
                        onActionClick = onNavigateToFiles,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }

                // Recent file items
                items(recentFiles) { file ->
                    RecentFileItem(file = file, onFileClick = onNavigateToFilePreview)
                }
            }
        }
        
        // Shortcuts Popup
        ShortcutsPopupScreen(
            isVisible = showShortcutsPopup,
            onDismiss = { showShortcutsPopup = false },
            onUploadFile = { 
                showShortcutsPopup = false
                onNavigateToUploadManager() 
            },
            onUploadFolder = { 
                showShortcutsPopup = false
                onNavigateToUploadManager() 
            },
            onCreateFolder = { 
                showShortcutsPopup = false
                onNavigateToFiles() 
            },
            onTakePhoto = { 
                showShortcutsPopup = false
                // Handle camera
            },
            onScanDocument = { 
                showShortcutsPopup = false
                // Handle document scan
            },
            onCreateDocument = { 
                showShortcutsPopup = false
                // Handle document creation
            }
        )
    }
}

// ── Storage card ──────────────────────────────────────────────────────────────

@Composable
private fun StorageCard() {
    Column(modifier = Modifier.padding(20.dp)) {
        // Title row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_storage_used),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralDark
            )
            Text(
                text = "0 GB " + stringResource(R.string.home_of) + " 0 GB",
                fontSize = 14.sp,
                color = NeutralMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main progress bar
        LinearProgressIndicator(
            progress = { 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CircleShape),
            color = CloudBlue,
            trackColor = CloudBlueLight,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "0% " + stringResource(R.string.home_storage_used) + " · 0 GB " + stringResource(R.string.storage_permanent),
            fontSize = 12.sp,
            color = NeutralMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category breakdown
        storageCategories.forEach { cat ->
            StorageCategoryRow(category = cat)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StorageCategoryRow(category: StorageCategory) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(category.color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = category.label,
            fontSize = 13.sp,
            color = NeutralDark,
            modifier = Modifier.weight(1f)
        )
        Text(text = category.used, fontSize = 12.sp, color = NeutralMedium)
        Spacer(modifier = Modifier.width(12.dp))
        LinearProgressIndicator(
            progress = { category.fraction },
            modifier = Modifier
                .width(72.dp)
                .height(6.dp)
                .clip(CircleShape),
            color = category.color,
            trackColor = category.color.copy(alpha = 0.15f),
            strokeCap = StrokeCap.Round
        )
    }
}

// ── Quick actions ─────────────────────────────────────────────────────────────

@Composable
private fun QuickActionsRow(onUploadClick: () -> Unit = {}) {
    val actions = listOf(
        QuickAction(stringResource(R.string.menu_upload), Icons.Default.CloudQueue, CloudBlue, CloudBlueLight),
        QuickAction(stringResource(R.string.quick_action_create_folder), Icons.Default.FolderOpen, Color(0xFFF59E0B), Color(0xFFFEF3C7)),
        QuickAction(stringResource(R.string.profile_share), Icons.Default.People, Color(0xFF10B981), Color(0xFFD1FAE5)),
        QuickAction(stringResource(R.string.my_files_starred), Icons.Default.Star, Color(0xFFEC4899), Color(0xFFFCE7F3)),
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(actions) { action ->
            QuickActionChip(
                action = action,
                onClick = if (actions.indexOf(action) == 0) onUploadClick else ({})
            )
        }
    }
}

@Composable
private fun QuickActionChip(action: QuickAction, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        action.bg,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.label,
                    tint = action.tint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = NeutralDark,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

// ── Recent files ──────────────────────────────────────────────────────────────

@Composable
private fun RecentFileItem(file: RecentFile, onFileClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clickable { onFileClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (file.isImage) {
                Image(
                    painter = painterResource(id = R.drawable.img_vacation_thumb),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(file.iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = file.icon,
                        contentDescription = null,
                        tint = file.iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeutralDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${file.size}  ·  ${file.date}",
                    fontSize = 12.sp,
                    color = NeutralMedium
                )
            }

            IconButton(onClick = { /* options */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = NeutralMedium)
            }
        }
    }
}

// ── Section title ─────────────────────────────────────────────────────────────

@Composable
private fun SectionTitle(
    title: String,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = NeutralDark
        )
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = CloudBlue,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

// ── Bottom navigation ─────────────────────────────────────────────────────────

@Composable
private fun DashboardBottomNav(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = NeutralWhite,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (selectedIndex == index) FontWeight.SemiBold
                                     else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = CloudBlue,
                    selectedTextColor   = CloudBlue,
                    indicatorColor      = CloudBlueLight,
                    unselectedIconColor = NeutralMedium,
                    unselectedTextColor = NeutralMedium
                )
            )
        }
    }
}
