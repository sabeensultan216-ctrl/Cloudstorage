package com.example.cloudstorage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

data class BackupFolder(
    val label: String,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val size: String,
    var enabled: Boolean
)

data class BackupScheduleOption(val label: String, val sub: String)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun AutomaticBackupScreen(onBack: () -> Unit = {}) {
    var backupEnabled   by remember { mutableStateOf(true) }
    var wifiOnlyEnabled by remember { mutableStateOf(true) }
    var notifyEnabled   by remember { mutableStateOf(false) }
    var selectedSchedule by remember { mutableStateOf("Daily") }

    val folders = remember {
        mutableStateOf(
            listOf(
                BackupFolder("Photos",   Icons.Default.Image,       CloudBlue,         CloudBlueLight,      "6.4 GB",  true),
                BackupFolder("Videos",   Icons.Default.Videocam,    Color(0xFF8B5CF6), Color(0xFFEDE9FE),   "2.1 GB",  true),
                BackupFolder("Music",    Icons.Default.MusicNote,   Color(0xFFEC4899), Color(0xFFFCE7F3),   "540 MB",  false),
                BackupFolder("Downloads",Icons.Default.PhoneAndroid, Color(0xFFF59E0B), Color(0xFFFEF3C7),  "890 MB",  false),
            )
        )
    }

    val scheduleOptions = listOf(
        BackupScheduleOption("Every hour",  "Backs up every 60 minutes"),
        BackupScheduleOption("Daily",       "Backs up once a day"),
        BackupScheduleOption("Weekly",      "Backs up every Sunday"),
        BackupScheduleOption("Manual only", "Only when you tap Back Up Now"),
    )

    // Simulated overall backup progress
    val backupProgress by animateFloatAsState(
        targetValue = if (backupEnabled) 0.82f else 0f,
        animationSpec = tween(durationMillis = 800, easing = LinearEasing),
        label = "backupProgress"
    )

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
                        Text("Automatic Backup", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeutralWhite)
                        Text("Secure your files automatically", fontSize = 12.sp, color = NeutralWhite.copy(alpha = 0.75f))
                    }
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = null,
                        tint = NeutralWhite.copy(alpha = 0.8f),
                        modifier = Modifier.padding(end = 12.dp).size(24.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Master backup toggle card ──────────────────────────────
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (backupEnabled) CloudBlueSplash else NeutralWhite
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Automatic Backup",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (backupEnabled) NeutralWhite else NeutralDark
                                )
                                Text(
                                    text = if (backupEnabled) "Backup is active" else "Backup is paused",
                                    fontSize = 13.sp,
                                    color = if (backupEnabled) NeutralWhite.copy(alpha = 0.78f) else NeutralMedium
                                )
                            }
                            Switch(
                                checked = backupEnabled,
                                onCheckedChange = { backupEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeutralWhite,
                                    checkedTrackColor = NeutralWhite.copy(alpha = 0.35f),
                                    uncheckedThumbColor = NeutralMedium,
                                    uncheckedTrackColor = Color(0xFFE2E8F0)
                                )
                            )
                        }

                        AnimatedVisibility(visible = backupEnabled) {
                            Column {
                                Spacer(modifier = Modifier.height(16.dp))
                                // Last backup status
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(NeutralWhite.copy(alpha = 0.15f))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF6EE7B7),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Last backup: Today at 9:45 AM",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = NeutralWhite
                                        )
                                        Text(
                                            text = "9.9 GB backed up · 2.1 GB remaining",
                                            fontSize = 11.sp,
                                            color = NeutralWhite.copy(alpha = 0.72f)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Overall backup", fontSize = 12.sp, color = NeutralWhite.copy(alpha = 0.8f))
                                    Text("${(backupProgress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeutralWhite)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { backupProgress },
                                    modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                                    color = NeutralWhite,
                                    trackColor = NeutralWhite.copy(alpha = 0.25f),
                                    strokeCap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }

                // ── Backup folders ─────────────────────────────────────────
                SectionHeader(title = "Backup Folders")
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NeutralWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        folders.value.forEachIndexed { index, folder ->
                            FolderToggleRow(
                                folder = folder,
                                onToggle = { enabled ->
                                    folders.value = folders.value.toMutableList().also { it[index] = folder.copy(enabled = enabled) }
                                }
                            )
                            if (index < folders.value.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                            }
                        }
                    }
                }

                // ── Schedule ───────────────────────────────────────────────
                SectionHeader(title = "Backup Schedule")
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NeutralWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        scheduleOptions.forEachIndexed { index, option ->
                            ScheduleRow(
                                option = option,
                                isSelected = selectedSchedule == option.label,
                                onSelect = { selectedSchedule = option.label }
                            )
                            if (index < scheduleOptions.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                            }
                        }
                    }
                }

                // ── Preferences ────────────────────────────────────────────
                SectionHeader(title = "Preferences")
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NeutralWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        PreferenceToggleRow(
                            icon = Icons.Default.Wifi,
                            iconTint = CloudBlue,
                            iconBg = CloudBlueLight,
                            title = "Wi-Fi Only",
                            subtitle = "Don't use mobile data for backup",
                            checked = wifiOnlyEnabled,
                            onToggle = { wifiOnlyEnabled = it }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F5F9))
                        PreferenceToggleRow(
                            icon = Icons.Default.Notifications,
                            iconTint = Color(0xFFF59E0B),
                            iconBg = Color(0xFFFEF3C7),
                            title = "Backup Notifications",
                            subtitle = "Get notified when backup completes",
                            checked = notifyEnabled,
                            onToggle = { notifyEnabled = it }
                        )
                    }
                }

                // ── Back Up Now button ─────────────────────────────────────
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.horizontalGradient(listOf(CloudBlueSplash, CloudBlue)))
                        .clickable { /* trigger manual backup */ }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = NeutralWhite, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Back Up Now", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeutralWhite)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ── Folder toggle row ─────────────────────────────────────────────────────────

@Composable
private fun FolderToggleRow(
    folder: BackupFolder,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(if (folder.enabled) folder.iconBg else Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = folder.icon,
                contentDescription = null,
                tint = if (folder.enabled) folder.iconTint else NeutralMedium,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = folder.label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = NeutralDark)
            Text(text = folder.size, fontSize = 12.sp, color = NeutralMedium)
        }
        Switch(
            checked = folder.enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeutralWhite,
                checkedTrackColor = CloudBlue,
                uncheckedThumbColor = NeutralMedium,
                uncheckedTrackColor = Color(0xFFE2E8F0)
            )
        )
    }
}

// ── Schedule row ──────────────────────────────────────────────────────────────

@Composable
private fun ScheduleRow(
    option: BackupScheduleOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(if (isSelected) CloudBlueLight else Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Schedule,
                contentDescription = null,
                tint = if (isSelected) CloudBlue else NeutralMedium,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.label,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) CloudBlue else NeutralDark
            )
            Text(text = option.sub, fontSize = 11.sp, color = NeutralMedium)
        }
        if (isSelected) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = CloudBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ── Preference toggle row ─────────────────────────────────────────────────────

@Composable
private fun PreferenceToggleRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(if (checked) iconBg else Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) iconTint else NeutralMedium,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = NeutralDark)
            Text(text = subtitle, fontSize = 11.sp, color = NeutralMedium)
        }
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeutralWhite,
                checkedTrackColor = CloudBlue,
                uncheckedThumbColor = NeutralMedium,
                uncheckedTrackColor = Color(0xFFE2E8F0)
            )
        )
    }
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = NeutralMedium,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}
