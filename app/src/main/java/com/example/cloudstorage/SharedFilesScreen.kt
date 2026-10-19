package com.example.cloudstorage

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloudstorage.ui.theme.CloudBlue
import com.example.cloudstorage.ui.theme.CloudBlueLight
import com.example.cloudstorage.ui.theme.CloudBlueSplash
import com.example.cloudstorage.ui.theme.DividerColor
import com.example.cloudstorage.ui.theme.NeutralDark
import com.example.cloudstorage.ui.theme.NeutralLight
import com.example.cloudstorage.ui.theme.NeutralMedium
import com.example.cloudstorage.ui.theme.NeutralWhite

import androidx.compose.ui.res.stringResource

// ── Models ────────────────────────────────────────────────────────────────────

data class SharedPerson(
    val name: String,
    val initials: String,
    val avatarColor: Color,
    val fileCount: Int
)

data class SharedFileItem(
    val name: String,
    val size: String,
    val date: String,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val permission: String,          // "View" | "Edit" | "Owner"
    val sharedWith: List<SharedPerson> = emptyList(),
    val sharedBy: SharedPerson? = null,
    val isLink: Boolean = false
)

// ── Sample data ───────────────────────────────────────────────────────────────

private val samplePeople = emptyList<SharedPerson>()

private val sharedByMe = emptyList<SharedFileItem>()

private val sharedWithMe = emptyList<SharedFileItem>()

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedFilesScreen(
    onBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToFiles: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToUploadManager: () -> Unit = {},
    onNavigateToAutoBackup: () -> Unit = {},
    onNavigateToOffline: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedDrawerItem by remember { mutableIntStateOf(2) }

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
                        "home"          -> onNavigateToHome()
                        "my_files"      -> onNavigateToFiles()
                        "shared"        -> {} // Already on Shared
                        "upload"        -> onNavigateToUploadManager()
                        "auto_backup"   -> onNavigateToAutoBackup()
                        "offline_files" -> onNavigateToOffline()
                        "go_premium"    -> onNavigateToPremium()
                        "profile"       -> onNavigateToProfile()
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
                    selectedIndex = 2,
                    onItemSelected = { index ->
                        when (index) {
                            0 -> onNavigateToHome()
                            1 -> onNavigateToFiles()
                            2 -> {} // Already on Shared
                            3 -> onNavigateToProfile()
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // ── Standard Header ──────────────────────────────────────────────
                StandardHeader(
                    title = stringResource(R.string.shared_files_title),
                    showSearch = true,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSearchClick = { /* search functionality */ }
                )

                // ── Tab Row for Shared Files ─────────────────────────────────────
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = CloudBlue,
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(3.dp)
                                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                .background(CloudBlue)
                        )
                    },
                    divider = { HorizontalDivider(color = DividerColor) }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = stringResource(R.string.shared_by_me),
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) CloudBlue else NeutralMedium
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = stringResource(R.string.shared_with_me),
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) CloudBlue else NeutralMedium
                            )
                        }
                    )
                }

                // ── People row ─────────────────────────────────────────────────
                PeopleRow(people = samplePeople)

                // ── File list ──────────────────────────────────────────────────
                val list = if (selectedTab == 0) sharedByMe else sharedWithMe

                if (list.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.People,
                                contentDescription = null,
                                tint = NeutralMedium.copy(alpha = 0.35f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(stringResource(R.string.shared_empty), fontSize = 16.sp, color = NeutralMedium)
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(list) { file ->
                            SharedFileCard(file = file, isByMe = selectedTab == 0)
                        }
                    }
                }
            }
        }
    }
}

// ── People row ────────────────────────────────────────────────────────────────

@Composable
private fun PeopleRow(people: List<SharedPerson>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NeutralWhite)
            .padding(vertical = 14.dp)
    ) {
        Text(
            text = stringResource(R.string.shared_people),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = NeutralMedium,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(people) { person ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(person.avatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = person.initials,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeutralWhite
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = person.name.split(" ").first(),
                        fontSize = 11.sp,
                        color = NeutralDark,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${person.fileCount} files",
                        fontSize = 10.sp,
                        color = NeutralMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = Color(0xFFE2E8F0))
    }
}

// ── Shared file card ──────────────────────────────────────────────────────────

@Composable
private fun SharedFileCard(file: SharedFileItem, isByMe: Boolean) {
    val permissionColor = when (file.permission) {
        "Edit"  -> Color(0xFF10B981)
        "Owner" -> CloudBlue
        else    -> Color(0xFFF59E0B)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable {},
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // File icon
                Box(
                    modifier = Modifier
                        .size(46.dp)
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

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = file.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeutralDark,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (file.isLink) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Default.Link,
                                contentDescription = "Link share",
                                tint = NeutralMedium,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${file.size}  ·  ${file.date}",
                        fontSize = 12.sp,
                        color = NeutralMedium
                    )
                }

                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = NeutralMedium)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Avatar stack or "by person"
                if (isByMe && file.sharedWith.isNotEmpty()) {
                    AvatarStack(people = file.sharedWith)
                } else if (!isByMe && file.sharedBy != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(file.sharedBy.avatarColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = file.sharedBy.initials,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeutralWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Shared by ${file.sharedBy.name}",
                            fontSize = 12.sp,
                            color = NeutralMedium
                        )
                    }
                }

                // Permission badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(permissionColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Can ${file.permission}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = permissionColor
                    )
                }
            }
        }
    }
}

// ── Avatar stack ──────────────────────────────────────────────────────────────

@Composable
private fun AvatarStack(people: List<SharedPerson>) {
    val show = people.take(4)
    val extra = people.size - show.size

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.height(28.dp).width((show.size * 20 + 8).dp)) {
            show.forEachIndexed { i, person ->
                Box(
                    modifier = Modifier
                        .offset(x = (i * 20).dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .border(2.dp, NeutralWhite, CircleShape)
                        .background(person.avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = person.initials.first().toString(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralWhite
                    )
                }
            }
        }
        if (extra > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "+$extra", fontSize = 11.sp, color = NeutralMedium)
        }
    }
}
