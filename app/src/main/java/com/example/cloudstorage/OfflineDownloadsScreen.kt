package com.example.cloudstorage

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

// ── Models ────────────────────────────────────────────────────────────────────

data class OfflineFile(
    val id: Int,
    val name: String,
    val size: String,
    val savedDate: String,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val isAvailableOffline: Boolean = true,
    val syncStatus: OfflineSyncStatus = OfflineSyncStatus.SYNCED
)

enum class OfflineSyncStatus { SYNCED, SYNCING, OUT_OF_DATE }

// ── Sample data ───────────────────────────────────────────────────────────────

private val sampleOfflineFiles = emptyList<OfflineFile>()

// ── Storage constants ─────────────────────────────────────────────────────────

private const val TOTAL_OFFLINE_GB = 0f
private const val USED_OFFLINE_GB  = 0f

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun OfflineDownloadsScreen(onBack: () -> Unit = {}) {
    val files = remember { mutableStateListOf(*sampleOfflineFiles.toTypedArray()) }
    var deleteTarget by remember { mutableStateOf<OfflineFile?>(null) }

    Scaffold(containerColor = NeutralLight) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Top bar ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(CloudBlueSplash, CloudBlue)))
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NeutralWhite)
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                        Text("Offline Downloads", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeutralWhite)
                        Text("${files.count { it.isAvailableOffline }} files available offline", fontSize = 12.sp, color = NeutralWhite.copy(alpha = 0.75f))
                    }
                    Icon(
                        Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = NeutralWhite.copy(alpha = 0.8f),
                        modifier = Modifier.padding(end = 12.dp).size(22.dp)
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Storage usage card
                item { OfflineStorageCard(usedGb = USED_OFFLINE_GB, totalGb = TOTAL_OFFLINE_GB) }

                // Info banner
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CloudBlueLight)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.OfflinePin, contentDescription = null, tint = CloudBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Offline files are available without internet. Toggle to remove them from this device.",
                            fontSize = 12.sp,
                            color = CloudBlueSplash,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Section: Available offline
                item {
                    SectionHeaderOffline(
                        title = "Available Offline",
                        count = files.count { it.isAvailableOffline }
                    )
                }

                items(
                    items = files.filter { it.isAvailableOffline },
                    key = { it.id }
                ) { file ->
                    OfflineFileCard(
                        file = file,
                        onToggleOffline = { enabled ->
                            val idx = files.indexOfFirst { it.id == file.id }
                            if (idx >= 0) files[idx] = file.copy(isAvailableOffline = enabled)
                        },
                        onDelete = { deleteTarget = file }
                    )
                }

                // Section: Not offline
                val notOffline = files.filter { !it.isAvailableOffline }
                if (notOffline.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        SectionHeaderOffline(title = "Not Available Offline", count = notOffline.size)
                    }
                    items(items = notOffline, key = { it.id }) { file ->
                        OfflineFileCard(
                            file = file,
                            onToggleOffline = { enabled ->
                                val idx = files.indexOfFirst { it.id == file.id }
                                if (idx >= 0) files[idx] = file.copy(isAvailableOffline = enabled)
                            },
                            onDelete = { deleteTarget = file }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }

    // ── Delete confirmation dialog ─────────────────────────────────────────
    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = NeutralWhite,
            title = {
                Text("Remove Offline File", fontWeight = FontWeight.Bold, color = NeutralDark)
            },
            text = {
                Text(
                    "\"${target.name}\" will be removed from this device. It will still be available in the cloud.",
                    color = NeutralMedium,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val idx = files.indexOfFirst { it.id == target.id }
                    if (idx >= 0) files.removeAt(idx)
                    deleteTarget = null
                }) {
                    Text("Remove", color = Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) {
                    Text("Cancel", color = NeutralMedium)
                }
            }
        )
    }
}

// ── Storage card ──────────────────────────────────────────────────────────────

@Composable
private fun OfflineStorageCard(usedGb: Float, totalGb: Float) {
    val fraction = (usedGb / totalGb).coerceIn(0f, 1f)
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(CloudBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = CloudBlue, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Offline Storage", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NeutralDark)
                        Text("${usedGb} GB used of ${totalGb.toInt()} GB", fontSize = 12.sp, color = NeutralMedium)
                    }
                }
                Text(
                    text = "${(fraction * 100).toInt()}%",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CloudBlue
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                color = CloudBlue,
                trackColor = CloudBlueLight,
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StorageLegend(color = CloudBlue,         label = "Used",      value = "${usedGb} GB")
                StorageLegend(color = Color(0xFFE2E8F0), label = "Free",      value = "${String.format("%.1f", totalGb - usedGb)} GB")
                StorageLegend(color = Color(0xFF10B981), label = "Total",     value = "${totalGb.toInt()} GB")
            }
        }
    }
}

@Composable
private fun StorageLegend(color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(5.dp))
        Column {
            Text(text = label, fontSize = 10.sp, color = NeutralMedium)
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = NeutralDark)
        }
    }
}

// ── Offline file card ─────────────────────────────────────────────────────────

@Composable
private fun OfflineFileCard(
    file: OfflineFile,
    onToggleOffline: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val syncColor = when (file.syncStatus) {
        OfflineSyncStatus.SYNCED      -> Color(0xFF10B981)
        OfflineSyncStatus.SYNCING     -> CloudBlue
        OfflineSyncStatus.OUT_OF_DATE -> Color(0xFFF59E0B)
    }
    val syncLabel = when (file.syncStatus) {
        OfflineSyncStatus.SYNCED      -> "Synced"
        OfflineSyncStatus.SYNCING     -> "Syncing..."
        OfflineSyncStatus.OUT_OF_DATE -> "Out of date"
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable {},
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NeutralWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (file.isAvailableOffline) file.iconBg else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = file.icon,
                        contentDescription = null,
                        tint = if (file.isAvailableOffline) file.iconTint else NeutralMedium,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = file.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (file.isAvailableOffline) NeutralDark else NeutralMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${file.size}  ·  ${file.savedDate}",
                            fontSize = 12.sp,
                            color = NeutralMedium
                        )
                        if (file.isAvailableOffline) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(syncColor)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = syncLabel, fontSize = 11.sp, color = syncColor)
                        }
                    }
                }

                // Delete
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color(0xFFEF4444).copy(alpha = 0.7f),
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            // Syncing progress bar
            if (file.syncStatus == OfflineSyncStatus.SYNCING) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(3.dp),
                    color = CloudBlue,
                    trackColor = CloudBlueLight
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 14.dp),
                color = Color(0xFFF1F5F9)
            )

            // Offline toggle row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.OfflinePin,
                        contentDescription = null,
                        tint = if (file.isAvailableOffline) CloudBlue else NeutralMedium,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (file.isAvailableOffline) "Available offline" else "Save for offline",
                        fontSize = 12.sp,
                        color = if (file.isAvailableOffline) CloudBlue else NeutralMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Switch(
                    checked = file.isAvailableOffline,
                    onCheckedChange = onToggleOffline,
                    modifier = Modifier.size(height = 24.dp, width = 44.dp),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeutralWhite,
                        checkedTrackColor = CloudBlue,
                        uncheckedThumbColor = NeutralMedium,
                        uncheckedTrackColor = Color(0xFFE2E8F0)
                    )
                )
            }
        }
    }
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeaderOffline(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NeutralMedium)
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(CloudBlueLight)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = count.toString(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CloudBlue
            )
        }
    }
}
