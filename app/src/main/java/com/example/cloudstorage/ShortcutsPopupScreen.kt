package com.example.cloudstorage

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class ShortcutItem(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val onClick: () -> Unit = {}
)

@Composable
fun ShortcutsPopupScreen(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onUploadFile: () -> Unit = {},
    onUploadFolder: () -> Unit = {},
    onCreateFolder: () -> Unit = {},
    onTakePhoto: () -> Unit = {},
    onScanDocument: () -> Unit = {},
    onCreateDocument: () -> Unit = {}
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                // Main Popup Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .wrapContentHeight()
                        .clickable { }, // Prevent dismiss when clicking on card
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header with title and close button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.quick_actions_title),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.cd_close),
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Shortcuts Grid (2x3)
                        val shortcuts = listOf(
                            ShortcutItem(
                                icon = Icons.Outlined.Home, // Upload File
                                title = stringResource(R.string.quick_action_upload_file),
                                description = stringResource(R.string.quick_action_upload_file_desc),
                                onClick = onUploadFile
                            ),
                            ShortcutItem(
                                icon = Icons.Outlined.Folder,
                                title = stringResource(R.string.quick_action_upload_folder), 
                                description = stringResource(R.string.quick_action_upload_folder_desc),
                                onClick = onUploadFolder
                            ),
                            ShortcutItem(
                                icon = Icons.Outlined.Folder,
                                title = stringResource(R.string.quick_action_create_folder),
                                description = stringResource(R.string.quick_action_create_folder_desc),
                                onClick = onCreateFolder
                            ),
                            ShortcutItem(
                                icon = Icons.Outlined.Home, // Camera
                                title = stringResource(R.string.quick_action_take_photo),
                                description = stringResource(R.string.quick_action_take_photo_desc),
                                onClick = onTakePhoto
                            ),
                            ShortcutItem(
                                icon = Icons.Outlined.Settings, // Scanner
                                title = stringResource(R.string.quick_action_scan_document), 
                                description = stringResource(R.string.quick_action_scan_document_desc),
                                onClick = onScanDocument
                            ),
                            ShortcutItem(
                                icon = Icons.Outlined.Info, // Document
                                title = stringResource(R.string.quick_action_create_document),
                                description = stringResource(R.string.quick_action_create_document_desc),
                                onClick = onCreateDocument
                            )
                        )
                        
                        // Grid Layout - 2 rows, 3 columns
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // First Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                shortcuts.take(3).forEach { shortcut ->
                                    ShortcutButton(
                                        item = shortcut,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            
                            // Second Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                shortcuts.drop(3).forEach { shortcut ->
                                    ShortcutButton(
                                        item = shortcut,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutButton(
    item: ShortcutItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { 
                item.onClick() 
            }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Color(0xFF3B82F6).copy(alpha = 0.1f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color(0xFF3B82F6),
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Title
        Text(
            text = item.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Description
        Text(
            text = item.description,
            fontSize = 11.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

// Floating Action Button with Animation
@Composable
fun AnimatedFloatingActionButton(
    onClick: () -> Unit,
    isExpanded: Boolean = false,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = tween(300),
        label = "FAB Rotation"
    )
    
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(56.dp),
        containerColor = Color(0xFF3B82F6),
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Quick Actions",
            modifier = Modifier
                .size(24.dp)
                .rotate(rotation)
        )
    }
}