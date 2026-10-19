package com.example.cloudstorage.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CloudColorScheme = lightColorScheme(
    primary            = CloudBlue,
    onPrimary          = NeutralWhite,
    primaryContainer   = CloudBlueLight,
    onPrimaryContainer = CloudBlueDark,
    secondary          = CloudSky,
    onSecondary        = NeutralWhite,
    background         = NeutralWhite,
    onBackground       = NeutralDark,
    surface            = NeutralWhite,
    onSurface          = NeutralDark,
    error              = ErrorRed,
    onError            = NeutralWhite,
)

@Composable
fun CloudStorageTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,   // keep brand colors consistent
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = CloudBlueSplash.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = CloudColorScheme,
        typography = Typography,
        content = content
    )
}