package com.example.cloudstorage

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloudstorage.ui.theme.CloudBlue
import com.example.cloudstorage.ui.theme.CloudBlueLight
import com.example.cloudstorage.ui.theme.CloudBlueSplash
import com.example.cloudstorage.ui.theme.NeutralDark
import com.example.cloudstorage.ui.theme.NeutralLight
import com.example.cloudstorage.ui.theme.NeutralMedium
import com.example.cloudstorage.ui.theme.NeutralWhite
import kotlinx.coroutines.delay

// ── Data models ───────────────────────────────────────────────────────────────

enum class UploadStatus { UPLOADING, PAUSED, COMPLETED, FAILED }

data class UploadItem(
    val id: Int,
    val name: String,
    val size: String,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val progress: Float,          // 0f..1f
    val status: UploadStatus,
    val speed: String = "",       // e.g. "1.2 MB/s"
    val remaining: String = ""    // e.g. "~12s left"
)

private val sampleUploads = emptyList<UploadItem>()

private val uploadTabs = listOf("Active", "Completed", "Failed")

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun UploadManagerScreen(onBack: () -> Unit = {}) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val uploads by remember { mutableStateOf(sampleUploads) }

    val active    = uploads.filter { it.status == UploadStatus.UPLOADING || it.status == UploadStatus.PAUSED }
    val completed = uploads.filter { it.status == UploadStatus.COMPLETED }
    val failed    = uploads.filter { it.status == UploadStatus.FAILED }

    val tabLists = listOf(active, completed, failed)
    val uploadTabs = listOf(
        stringResource(R.string.upload_active),
        stringResource(R.string.upload_completed),
        stringResource(R.string.upload_failed)
    )

    Scaffold(containerColor = NeutralLight) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Top bar ────────────────────────────────────────────────────
            UploadTopBar(
                activeCount = active.size,
                onBack = onBack,
                onClearAll = { /* clear completed */ }
            )

            // ── Summary banner ─────────────────────────────────────────────
            if (active.isNotEmpty()) {
                UploadSummaryBanner(activeCount = active.size)
            }

            // ── Tabs ───────────────────────────────────────────────────────
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = NeutralWhite,
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
                divider = { HorizontalDivider(color = Color(0xFFE2E8F0)) }
            ) {
                uploadTabs.forEachIndexed { index, title ->
                    val count = tabLists[index].size
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selectedTab == index) CloudBlue else NeutralMedium
                            )
                            if (count > 0) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 2.dp)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (selectedTab == index) CloudBlue
                                             else NeutralMedium.copy(alpha = 0.3f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = count.toString(),
                                        fontSize = 10.sp,
                                        color = NeutralWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Upload list ────────────────────────────────────────────────
            val currentList = tabLists[selectedTab]

            if (currentList.isEmpty()) {
                EmptyUploadState(tabIndex = selectedTab)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(currentList, key = { it.id }) { upload ->
                        UploadItemCard(item = upload)
                    }
                }
            }
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun UploadTopBar(
    activeCount: Int,
    onBack: () -> Unit,
    onClearAll: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CloudBlueSplash)
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NeutralWhite)
            }
            Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                Text(
                    text = stringResource(R.string.upload_manager_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeutralWhite
                )
                if (activeCount > 0) {
                    Text(
                        text = "$activeCount upload${if (activeCount > 1) "s" else ""} in progress",
                        fontSize = 12.sp,
                        color = NeutralWhite.copy(alpha = 0.75f)
                    )
                }
            }
            TextButton(onClick = onClearAll) {
                Text(stringResource(R.string.upload_clear_all), color = NeutralWhite.copy(alpha = 0.85f), fontSize = 13.sp)
            }
        }
    }
}

// ── Summary banner ────────────────────────────────────────────────────────────

@Composable
private fun UploadSummaryBanner(activeCount: Int) {
    var totalProgress by remember { mutableFloatStateOf(0.55f) }

    // Animate progress slowly for demo
    LaunchedEffect(Unit) {
        while (totalProgress < 0.95f) {
            delay(80)
            totalProgress = (totalProgress + 0.003f).coerceAtMost(0.95f)
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = totalProgress,
        animationSpec = tween(durationMillis = 400, easing = LinearEasing),
        label = "totalProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CloudBlue)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Overall Progress",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = NeutralWhite
                )
                Text(
                    text = "${(animatedProgress * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeutralWhite
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = NeutralWhite,
                trackColor = NeutralWhite.copy(alpha = 0.30f),
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$activeCount files uploading  ·  2.1 MB/s average",
                fontSize = 11.sp,
                color = NeutralWhite.copy(alpha = 0.80f)
            )
        }
    }
}

// ── Upload item card ──────────────────────────────────────────────────────────

@Composable
private fun UploadItemCard(item: UploadItem) {
    var isPaused by remember { mutableStateOf(item.status == UploadStatus.PAUSED) }
    var progress by remember { mutableFloatStateOf(item.progress) }

    // Simulate upload progress for UPLOADING items
    LaunchedEffect(isPaused) {
        if (!isPaused && item.status == UploadStatus.UPLOADING) {
            while (progress < 1f) {
                delay(100)
                progress = (progress + 0.008f).coerceAtMost(1f)
            }
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "itemProgress_${item.id}"
    )

    val statusColor = when (item.status) {
        UploadStatus.COMPLETED -> Color(0xFF10B981)
        UploadStatus.FAILED    -> Color(0xFFEF4444)
        UploadStatus.PAUSED    -> Color(0xFFF59E0B)
        UploadStatus.UPLOADING -> CloudBlue
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // File icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(item.iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))

                // Name + meta
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeutralDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Status dot
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        val statusLabel = when {
                            item.status == UploadStatus.COMPLETED -> "Completed"
                            item.status == UploadStatus.FAILED    -> "Failed"
                            isPaused                              -> "Paused"
                            else                                  -> item.speed
                        }
                        Text(text = statusLabel, fontSize = 12.sp, color = NeutralMedium)
                        if (item.status == UploadStatus.UPLOADING && !isPaused && item.remaining.isNotEmpty()) {
                            Text(text = "  ·  ${item.remaining}", fontSize = 12.sp, color = NeutralMedium)
                        }
                    }
                }

                // Action button
                when (item.status) {
                    UploadStatus.UPLOADING, UploadStatus.PAUSED -> {
                        IconButton(onClick = { isPaused = !isPaused }) {
                            Icon(
                                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (isPaused) "Resume" else "Pause",
                                tint = CloudBlue
                            )
                        }
                        IconButton(onClick = { /* cancel */ }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel", tint = NeutralMedium)
                        }
                    }
                    UploadStatus.COMPLETED -> {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Done",
                            tint = Color(0xFF10B981),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(24.dp)
                        )
                    }
                    UploadStatus.FAILED -> {
                        IconButton(onClick = { /* retry */ }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retry", tint = Color(0xFFEF4444))
                        }
                    }
                }
            }

            // Progress bar (only for active / paused)
            if (item.status == UploadStatus.UPLOADING || item.status == UploadStatus.PAUSED) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(CircleShape),
                        color = if (isPaused) Color(0xFFF59E0B) else CloudBlue,
                        trackColor = CloudBlueLight,
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPaused) Color(0xFFF59E0B) else CloudBlue
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.size,
                    fontSize = 11.sp,
                    color = NeutralMedium
                )
            }

            // Error message for failed
            if (item.status == UploadStatus.FAILED) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFEF2F2))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Upload failed. Tap retry to try again.",
                        fontSize = 12.sp,
                        color = Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyUploadState(tabIndex: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = when (tabIndex) {
                    1 -> Icons.Default.CheckCircle
                    2 -> Icons.Default.ErrorOutline
                    else -> Icons.Default.CloudQueue
                },
                contentDescription = null,
                tint = NeutralMedium.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = when (tabIndex) {
                    1 -> stringResource(R.string.upload_empty_completed)
                    2 -> stringResource(R.string.upload_empty_failed)
                    else -> stringResource(R.string.upload_empty_active)
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = NeutralMedium
            )
        }
    }
}
