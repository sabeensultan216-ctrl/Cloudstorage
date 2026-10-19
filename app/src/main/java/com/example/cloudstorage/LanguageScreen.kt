package com.example.cloudstorage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloudstorage.ui.theme.CloudStorageTheme
import com.example.cloudstorage.ui.theme.NeutralWhite

// ── Models ────────────────────────────────────────────────────────────────────

data class LanguageItem(
    val code: String,
    val name: String,
    val flagRes: Int
)

private val languages = listOf(
    LanguageItem("en", "English",    R.drawable.ic_flag_en),
    LanguageItem("hi", "हिन्दी",      R.drawable.ic_flag_hi),
    LanguageItem("ur", "اردو",       R.drawable.ic_flag_ur),
    LanguageItem("ar", "العربية",     R.drawable.ic_flag_ar),
    LanguageItem("es", "Español",    R.drawable.ic_flag_es),
    LanguageItem("fr", "Français",   R.drawable.ic_flag_fr),
    LanguageItem("de", "Deutsch",    R.drawable.ic_flag_de),
    LanguageItem("zh", "中文 (简体)", R.drawable.ic_flag_zh)
)

// ── Screen Composable ─────────────────────────────────────────────────────────

@Composable
fun LanguageScreen(
    onBack: () -> Unit = {},
    onContinue: (String) -> Unit = {}
) {
    var selectedLanguageCode by remember { mutableStateOf(LocalizationManager.currentLanguage.value) }

    val primaryGradient = Brush.horizontalGradient(
        listOf(Color(0xFF0084FF), Color(0xFF8B5CF6))
    )
    val selectedBorderGradient = Brush.horizontalGradient(
        listOf(Color(0xFF007AFF), Color(0xFF8B5CF6))
    )

    Scaffold(
        containerColor = NeutralWhite
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Ambient soft background glows
            Box(
                modifier = Modifier
                    .size(240.dp)
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
                    .size(240.dp)
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

            // ── Single-screen layout (no scrolling, fits viewport) ─────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp)
                    .padding(top = 4.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Row with Back Button and Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = stringResource(R.string.choose_your_language),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                // ── Language Options List (8 items) ──────────────────────────
                Column(
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    languages.forEach { item ->
                        val isSelected = item.code == selectedLanguageCode

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) Color(0xFFF0F7FF) else Color.White)
                                .then(
                                    if (isSelected) {
                                        Modifier.border(
                                            width = 2.dp,
                                            brush = selectedBorderGradient,
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                    } else {
                                        Modifier.border(
                                            width = 1.dp,
                                            color = Color(0xFFE2E8F0),
                                            shape = RoundedCornerShape(14.dp)
                                        )
                                    }
                                )
                                .clickable { selectedLanguageCode = item.code }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Flag icon with rounded corners and border
                                Box(
                                    modifier = Modifier
                                        .size(width = 38.dp, height = 26.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(5.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = item.flagRes),
                                        contentDescription = item.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.FillBounds
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // Language name
                                Text(
                                    text = item.name,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.weight(1f)
                                )

                                // Radio indicator
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .border(
                                            width = if (isSelected) 2.dp else 1.5.dp,
                                            color = if (isSelected) Color(0xFF007AFF) else Color(0xFFCBD5E1),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF007AFF))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Continue Button ──────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(25.dp),
                            spotColor = Color(0xFF8B5CF6).copy(alpha = 0.35f)
                        )
                        .clip(RoundedCornerShape(25.dp))
                        .background(primaryGradient)
                        .clickable { onContinue(selectedLanguageCode) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.continue_btn),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
fun LanguageScreenPreview() {
    CloudStorageTheme {
        LanguageScreen()
    }
}
