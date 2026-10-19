package com.example.cloudstorage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloudstorage.ui.theme.CloudBlue
import com.example.cloudstorage.ui.theme.CloudBlueLight
import com.example.cloudstorage.ui.theme.CloudBlueSplash
import com.example.cloudstorage.ui.theme.NeutralDark
import com.example.cloudstorage.ui.theme.NeutralMedium
import com.example.cloudstorage.ui.theme.NeutralWhite

// ── Preview type ──────────────────────────────────────────────────────────────

enum class PreviewType { IMAGE, VIDEO, DOCUMENT, OTHER }

data class PreviewFile(
    val name: String,
    val size: String,
    val date: String,
    val owner: String = "You",
    val type: PreviewType,
    val path: String = "My Files / Vacation Photos"
)

// Sample file passed to the screen (in real app pass via nav arg)
val samplePreviewFile = PreviewFile(
    name  = "Beach_sunset.jpg",
    size  = "4.2 MB",
    date  = "Sep 8, 2026",
    type  = PreviewType.IMAGE,
    path  = "My Files / Vacation Photos"
)

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilePreviewScreen(
    file: PreviewFile = samplePreviewFile,
    onBack: () -> Unit = {}
) {
    var showBars     by remember { mutableStateOf(true) }
    var isStarred    by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showInfoSheet by remember { mutableStateOf(false) }
    val sheetState   = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Pinch-zoom state (image only)
    var scale       by remember { mutableFloatStateOf(1f) }
    var offsetX     by remember { mutableFloatStateOf(0f) }
    var offsetY     by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ── Content area ───────────────────────────────────────────────────
        when (file.type) {
            PreviewType.IMAGE    -> ImagePreview(scale, offsetX, offsetY,
                onTap    = { showBars = !showBars },
                onZoom   = { s, x, y -> scale = s; offsetX = x; offsetY = y }
            )
            PreviewType.VIDEO    -> VideoPreview(onTap = { showBars = !showBars })
            PreviewType.DOCUMENT -> DocumentPreview(file = file)
            PreviewType.OTHER    -> GenericPreview(file = file)
        }

        // ── Top bar ────────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = showBars,
            enter = fadeIn() + slideInVertically { -it },
            exit  = fadeOut() + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            PreviewTopBar(
                file         = file,
                isStarred    = isStarred,
                showMoreMenu = showMoreMenu,
                onBack       = onBack,
                onStar       = { isStarred = !isStarred },
                onMoreToggle = { showMoreMenu = !showMoreMenu },
                onInfo       = { showInfoSheet = true; showMoreMenu = false },
                onZoomIn     = { scale = (scale + 0.25f).coerceAtMost(4f) },
                onZoomOut    = { scale = (scale - 0.25f).coerceAtLeast(1f) },
                showZoom     = file.type == PreviewType.IMAGE
            )
        }

        // ── Bottom action bar ──────────────────────────────────────────────
        AnimatedVisibility(
            visible = showBars,
            enter = fadeIn() + slideInVertically { it },
            exit  = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            PreviewBottomBar()
        }
    }

    // ── File info bottom sheet ─────────────────────────────────────────────
    if (showInfoSheet) {
        ModalBottomSheet(
            onDismissRequest = { showInfoSheet = false },
            sheetState = sheetState,
            containerColor = NeutralWhite,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            FileInfoSheet(file = file, onDismiss = { showInfoSheet = false })
        }
    }
}

// ── Image preview ─────────────────────────────────────────────────────────────

@Composable
private fun ImagePreview(
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    onTap: () -> Unit,
    onZoom: (Float, Float, Float) -> Unit
) {
    var currentScale  by remember { mutableFloatStateOf(scale) }
    var currentOffsetX by remember { mutableFloatStateOf(offsetX) }
    var currentOffsetY by remember { mutableFloatStateOf(offsetY) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    currentScale  = (currentScale * zoom).coerceIn(1f, 4f)
                    val maxX = (size.width  * (currentScale - 1)) / 2
                    val maxY = (size.height * (currentScale - 1)) / 2
                    currentOffsetX = (currentOffsetX + pan.x).coerceIn(-maxX, maxX)
                    currentOffsetY = (currentOffsetY + pan.y).coerceIn(-maxY, maxY)
                    onZoom(currentScale, currentOffsetX, currentOffsetY)
                }
            }
            .clickable { onTap() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_vacation_thumb),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX       = currentScale,
                    scaleY       = currentScale,
                    translationX = currentOffsetX,
                    translationY = currentOffsetY
                )
        )
    }
}

// ── Video preview ─────────────────────────────────────────────────────────────

@Composable
private fun VideoPreview(onTap: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .clickable { onTap() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(NeutralWhite.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = "Play",
                    tint = NeutralWhite,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Tap to play video", color = NeutralWhite.copy(alpha = 0.7f), fontSize = 14.sp)
        }
    }
}

// ── Document preview ──────────────────────────────────────────────────────────

@Composable
private fun DocumentPreview(file: PreviewFile) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1E)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(vertical = 32.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NeutralWhite),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(38.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = file.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeutralDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = file.size, fontSize = 13.sp, color = NeutralMedium)
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CloudBlueLight)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Document preview not available.\nDownload to open in a compatible app.",
                        fontSize = 13.sp,
                        color = CloudBlueSplash,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

// ── Generic preview ───────────────────────────────────────────────────────────

@Composable
private fun GenericPreview(file: PreviewFile) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = NeutralWhite.copy(alpha = 0.3f),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(file.name, color = NeutralWhite, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Preview not available for this file type", color = NeutralWhite.copy(alpha = 0.5f), fontSize = 13.sp)
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun PreviewTopBar(
    file: PreviewFile,
    isStarred: Boolean,
    showMoreMenu: Boolean,
    onBack: () -> Unit,
    onStar: () -> Unit,
    onMoreToggle: () -> Unit,
    onInfo: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    showZoom: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.65f), Color.Transparent)
                )
            )
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NeutralWhite)
            }

            // Title + path
            Column(modifier = Modifier.weight(1f).padding(start = 2.dp)) {
                Text(
                    text = file.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeutralWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = file.path,
                    fontSize = 11.sp,
                    color = NeutralWhite.copy(alpha = 0.65f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Zoom controls (image only)
            if (showZoom) {
                IconButton(onClick = onZoomOut) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom out", tint = NeutralWhite)
                }
                IconButton(onClick = onZoomIn) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom in", tint = NeutralWhite)
                }
            }

            // Star
            IconButton(onClick = onStar) {
                Icon(
                    imageVector = if (isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "Star",
                    tint = if (isStarred) Color(0xFFFBBF24) else NeutralWhite
                )
            }

            // More options
            Box {
                IconButton(onClick = onMoreToggle) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = NeutralWhite)
                }
                DropdownMenu(
                    expanded = showMoreMenu,
                    onDismissRequest = onMoreToggle
                ) {
                    DropdownMenuItem(
                        text = { Text("File Info") },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = NeutralMedium) },
                        onClick = onInfo
                    )
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        leadingIcon = { Icon(Icons.Default.DriveFileRenameOutline, contentDescription = null, tint = NeutralMedium) },
                        onClick = onMoreToggle
                    )
                    DropdownMenuItem(
                        text = { Text("Move to folder") },
                        leadingIcon = { Icon(Icons.Default.Folder, contentDescription = null, tint = NeutralMedium) },
                        onClick = onMoreToggle
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Delete", color = Color(0xFFEF4444)) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) },
                        onClick = onMoreToggle
                    )
                }
            }
        }
    }
}

// ── Bottom action bar ─────────────────────────────────────────────────────────

@Composable
private fun PreviewBottomBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                )
            )
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomAction(icon = Icons.Default.Share,                    label = "Share",    tint = NeutralWhite, onClick = {})
            BottomAction(icon = Icons.Default.Download,                 label = "Download", tint = NeutralWhite, onClick = {})
            BottomAction(icon = Icons.Default.DriveFileRenameOutline,   label = "Rename",   tint = NeutralWhite, onClick = {})
            BottomAction(icon = Icons.Default.Delete,                   label = "Delete",   tint = Color(0xFFFC8181), onClick = {})
        }
    }
}

@Composable
private fun BottomAction(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(26.dp))
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = label, fontSize = 11.sp, color = tint)
    }
}

// ── File info bottom sheet ────────────────────────────────────────────────────

@Composable
private fun FileInfoSheet(file: PreviewFile, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 36.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (file.type) {
                            PreviewType.IMAGE    -> CloudBlueLight
                            PreviewType.VIDEO    -> Color(0xFFEDE9FE)
                            PreviewType.DOCUMENT -> Color(0xFFFEE2E2)
                            PreviewType.OTHER    -> Color(0xFFF1F5F9)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (file.type) {
                        PreviewType.IMAGE    -> Icons.Default.Description
                        PreviewType.VIDEO    -> Icons.Default.VideoLibrary
                        PreviewType.DOCUMENT -> Icons.Default.Description
                        PreviewType.OTHER    -> Icons.Default.Description
                    },
                    contentDescription = null,
                    tint = when (file.type) {
                        PreviewType.IMAGE    -> CloudBlue
                        PreviewType.VIDEO    -> Color(0xFF8B5CF6)
                        PreviewType.DOCUMENT -> Color(0xFFEF4444)
                        PreviewType.OTHER    -> NeutralMedium
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(file.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NeutralDark)
                Text(file.path, fontSize = 12.sp, color = NeutralMedium)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFE2E8F0))
        Spacer(modifier = Modifier.height(16.dp))

        // Details
        InfoRow(label = "Size",      value = file.size)
        InfoRow(label = "Modified",  value = file.date)
        InfoRow(label = "Owner",     value = file.owner)
        InfoRow(label = "Type",      value = file.type.name.lowercase().replaceFirstChar { it.uppercase() })
        InfoRow(label = "Location",  value = file.path)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = NeutralMedium, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 13.sp, color = NeutralDark, fontWeight = FontWeight.SemiBold)
    }
}
