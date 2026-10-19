package com.example.cloudstorage

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
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

// ── Data models ───────────────────────────────────────────────────────────────

data class FileItem(
    val id: Int,
    val name: String,
    val size: String,
    val date: String,
    val type: FileType,
    val isFolder: Boolean = false,
    val itemCount: Int = 0    // only for folders
)

enum class FileType { IMAGE, VIDEO, DOCUMENT, FOLDER, OTHER }

private val allFiles = emptyList<FileItem>()

private val sortOptions = listOf("Name", "Date", "Size", "Type")

// ── File type helpers ─────────────────────────────────────────────────────────

fun fileIcon(type: FileType): ImageVector = when (type) {
    FileType.IMAGE    -> Icons.Default.Image
    FileType.VIDEO    -> Icons.Default.VideoLibrary
    FileType.DOCUMENT -> Icons.Default.Description
    FileType.FOLDER   -> Icons.Default.Folder
    FileType.OTHER    -> Icons.Default.Description
}

fun fileIconTint(type: FileType): Color = when (type) {
    FileType.IMAGE    -> CloudBlue
    FileType.VIDEO    -> Color(0xFF8B5CF6)
    FileType.DOCUMENT -> Color(0xFFEF4444)
    FileType.FOLDER   -> Color(0xFFF59E0B)
    FileType.OTHER    -> Color(0xFF64748B)
}

fun fileIconBg(type: FileType): Color = when (type) {
    FileType.IMAGE    -> CloudBlueLight
    FileType.VIDEO    -> Color(0xFFEDE9FE)
    FileType.DOCUMENT -> Color(0xFFFEE2E2)
    FileType.FOLDER   -> Color(0xFFFEF3C7)
    FileType.OTHER    -> Color(0xFFF1F5F9)
}

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyFilesScreen(
    onFileClick: (FileItem) -> Unit = {},
    onBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToShared: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToUploadManager: () -> Unit = {},
    onNavigateToAutoBackup: () -> Unit = {},
    onNavigateToOffline: () -> Unit = {},
    onNavigateToPremium: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedDrawerItem by remember { mutableIntStateOf(1) }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var isGridView by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("Date") }

    val drawerItems = getLocalizedDrawerNavItems()

    val filterTabs = listOf(
        stringResource(R.string.my_files_all),
        stringResource(R.string.my_files_photos),
        stringResource(R.string.my_files_videos),
        stringResource(R.string.my_files_documents),
        stringResource(R.string.quick_action_upload_folder)
    )

    val filteredFiles = allFiles.filter { file ->
        // Tab filter
        val tabMatch = when (selectedTab) {
            0 -> true
            1 -> file.type == FileType.IMAGE
            2 -> file.type == FileType.VIDEO
            3 -> file.type == FileType.DOCUMENT
            4 -> file.isFolder
            else -> true
        }
        // Search filter
        val searchMatch = searchQuery.isBlank() ||
                file.name.contains(searchQuery, ignoreCase = true)
        tabMatch && searchMatch
    }

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
                        "my_files"      -> {} // Already on Files
                        "shared"        -> onNavigateToShared()
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
                    selectedIndex = 1,
                    onItemSelected = { index ->
                        when (index) {
                            0 -> onNavigateToHome()
                            1 -> {} // Already on Files
                            2 -> onNavigateToShared()
                            3 -> onNavigateToProfile()
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { /* show upload options */ },
                    containerColor = CloudBlue,
                    contentColor = NeutralWhite,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Upload")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // ── Standard Header ──────────────────────────────────────────────
                StandardHeader(
                    title = stringResource(R.string.my_files_title),
                    showSearch = true,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSearchClick = { isSearchActive = !isSearchActive; if (!isSearchActive) searchQuery = "" }
                )

            // ── Filter tabs ────────────────────────────────────────────────
            FilterTabRow(
                tabs = filterTabs,
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // ── Breadcrumb ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.my_files_title), fontSize = 13.sp, color = CloudBlue, fontWeight = FontWeight.Medium)
                Text(" / ", fontSize = 13.sp, color = NeutralMedium)
                Text(filterTabs.getOrElse(selectedTab) { stringResource(R.string.my_files_all) }, fontSize = 13.sp, color = NeutralMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${filteredFiles.size} items",
                    fontSize = 12.sp,
                    color = NeutralMedium
                )
            }

            // ── File list / grid ───────────────────────────────────────────
            if (filteredFiles.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Folder,
                            contentDescription = null,
                            tint = NeutralMedium.copy(alpha = 0.35f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.my_files_empty),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeutralMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.my_files_empty_desc),
                            fontSize = 13.sp,
                            color = NeutralMedium.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                AnimatedContent(
                    targetState = isGridView,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "viewToggle"
                ) { grid ->
                    if (grid) {
                        FilesGrid(files = filteredFiles, onFileClick = onFileClick)
                    } else {
                        FilesList(files = filteredFiles, onFileClick = onFileClick)
                    }
                }
            }
        }
    }
}
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun FilesTopBar(
    isSearchActive: Boolean,
    searchQuery: String,
    isGridView: Boolean,
    selectedSort: String,
    showSortMenu: Boolean,
    onSearchToggle: () -> Unit,
    onQueryChange: (String) -> Unit,
    onViewToggle: () -> Unit,
    onSortMenuToggle: () -> Unit,
    onSortSelected: (String) -> Unit,
    onMenuClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CloudBlueSplash)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = NeutralWhite)
            }
            if (!isSearchActive) {
                Text(
                    text = stringResource(R.string.my_files_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeutralWhite,
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )
                IconButton(onClick = onSearchToggle) {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = NeutralWhite)
                }
                IconButton(onClick = onViewToggle) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                        contentDescription = "Toggle view",
                        tint = NeutralWhite
                    )
                }
                Box {
                    IconButton(onClick = onSortMenuToggle) {
                        Icon(Icons.Default.FilterList, contentDescription = "Sort", tint = NeutralWhite)
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { onSortMenuToggle() }
                    ) {
                        sortOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (option == selectedSort) {
                                            Icon(
                                                Icons.Default.ArrowUpward,
                                                contentDescription = null,
                                                tint = CloudBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                        }
                                        Text(
                                            text = option,
                                            color = if (option == selectedSort) CloudBlue else NeutralDark,
                                            fontWeight = if (option == selectedSort) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = { onSortSelected(option) }
                            )
                        }
                    }
                }
            } else {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    placeholder = { Text(stringResource(R.string.my_files_search), color = NeutralWhite.copy(alpha = 0.6f), fontSize = 14.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    textStyle = TextStyle(color = NeutralWhite, fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeutralWhite.copy(alpha = 0.6f),
                        unfocusedBorderColor = NeutralWhite.copy(alpha = 0.3f),
                        cursorColor = NeutralWhite,
                        focusedContainerColor = NeutralWhite.copy(alpha = 0.15f),
                        unfocusedContainerColor = NeutralWhite.copy(alpha = 0.10f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = NeutralWhite.copy(alpha = 0.7f))
                    }
                )
                IconButton(onClick = onSearchToggle) {
                    Icon(Icons.Default.Close, contentDescription = "Close search", tint = NeutralWhite)
                }
            }
        }
    }
}

// ── Filter tabs ───────────────────────────────────────────────────────────────

@Composable
private fun FilterTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.Transparent,
        contentColor = CloudBlue,
        edgePadding = 16.dp,
        indicator = { tabPositions ->
            if (selectedIndex < tabPositions.size) {
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedIndex])
                        .height(3.dp)
                        .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                        .background(CloudBlue)
                )
            }
        },
        divider = { HorizontalDivider(color = DividerColor) }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = if (selectedIndex == index) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selectedIndex == index) CloudBlue else NeutralMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
                )
            }
        }
    }
}

// ── List view ─────────────────────────────────────────────────────────────────

@Composable
private fun FilesList(
    files: List<FileItem>,
    onFileClick: (FileItem) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(files, key = { it.id }) { file ->
            FileListRow(file = file, onClick = { onFileClick(file) })
        }
    }
}

@Composable
private fun FileListRow(file: FileItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(fileIconBg(file.type)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = fileIcon(file.type),
                    contentDescription = null,
                    tint = fileIconTint(file.type),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeutralDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (file.isFolder) "${file.itemCount} items  ·  ${file.date}"
                           else "${file.size}  ·  ${file.date}",
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

// ── Grid view ─────────────────────────────────────────────────────────────────

@Composable
private fun FilesGrid(
    files: List<FileItem>,
    onFileClick: (FileItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(files, key = { it.id }) { file ->
            FileGridCard(file = file, onClick = { onFileClick(file) })
        }
    }
}

@Composable
private fun FileGridCard(file: FileItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(fileIconBg(file.type)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = fileIcon(file.type),
                    contentDescription = null,
                    tint = fileIconTint(file.type),
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = file.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = NeutralDark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = if (file.isFolder) "${file.itemCount} items" else file.size,
                fontSize = 11.sp,
                color = NeutralMedium
            )
        }
    }
}
