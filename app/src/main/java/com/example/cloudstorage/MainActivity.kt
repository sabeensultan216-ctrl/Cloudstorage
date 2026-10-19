package com.example.cloudstorage

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import com.example.cloudstorage.ui.theme.CloudStorageTheme
import com.example.cloudstorage.ui.theme.NeutralWhite

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocalizationManager.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalizationManager.init(this)

        // Render content edge-to-edge (behind status & nav bars)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val currentLang by LocalizationManager.currentLanguage
            val baseContext = LocalContext.current
            val locale = remember(currentLang) { LocalizationManager.getLocale(currentLang) }
            val configuration = remember(currentLang) {
                Configuration(baseContext.resources.configuration).apply {
                    setLocale(locale)
                    setLayoutDirection(locale)
                }
            }
            val localizedContext = remember(currentLang) {
                baseContext.createConfigurationContext(configuration)
            }
            val layoutDir = remember(currentLang) {
                if (LocalizationManager.isRtl(currentLang)) LayoutDirection.Rtl else LayoutDirection.Ltr
            }

            CompositionLocalProvider(
                LocalConfiguration provides configuration,
                LocalContext provides localizedContext,
                LocalLayoutDirection provides layoutDir
            ) {
                CloudStorageTheme(dynamicColor = false) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = NeutralWhite
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}