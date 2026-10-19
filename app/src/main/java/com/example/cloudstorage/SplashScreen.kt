package com.example.cloudstorage

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloudstorage.ui.theme.CloudBlue
import com.example.cloudstorage.ui.theme.CloudBlueSplash
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    // ── Animation values ───────────────────────────────────────────────────
    val logoScale    = remember { Animatable(0.4f) }
    val logoAlpha    = remember { Animatable(0f) }
    val textAlpha    = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {

        // 1 — Logo pop-in (scale + fade together)
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650, easing = EaseOutBack)
            )
        }
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 450)
            )
        }

        // 2 — App name fades in slightly after logo
        delay(350)
        launch {
            textAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500)
            )
        }

        // 3 — Tagline fades in after name
        delay(250)
        launch {
            taglineAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500)
            )
        }

        // 4 — Stay on screen, then navigate
        delay(800)
        onSplashFinished()
    }

    // ── Layout ─────────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to CloudBlueSplash,
                        1.0f to CloudBlue
                    )
                )
            )
    ) {

        // ── Logo + text — centred on screen ───────────────────────────────
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {

            // Logo
            Image(
                painter          = painterResource(id = R.drawable.ic_cloudbox_logo),
                contentDescription = "CloudBox",
                modifier = Modifier
                    .size(110.dp)
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // App name
            Text(
                text         = "CloudBox",
                fontSize     = 38.sp,
                fontWeight   = FontWeight.ExtraBold,
                color        = Color.White,
                letterSpacing = 1.5.sp,
                modifier     = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tagline
            Text(
                text         = "Your files, everywhere.",
                fontSize     = 15.sp,
                fontWeight   = FontWeight.Normal,
                color        = Color.White.copy(alpha = 0.78f),
                textAlign    = TextAlign.Center,
                letterSpacing = 0.4.sp,
                modifier     = Modifier.alpha(taglineAlpha.value)
            )
        }
    }
}
