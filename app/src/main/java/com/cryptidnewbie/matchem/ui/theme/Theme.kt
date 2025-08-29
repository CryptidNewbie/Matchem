package com.cryptidnewbie.matchem.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Modern Material-inspired color palette
private val ModernLightColorScheme = lightColorScheme(
    primary = Color(0xFF006494),       // Blue accent for buttons
    secondary = Color(0xFF39A0ED),     // Lighter blue accent
    tertiary = Color(0xFFF7C873),      // Warm accent
    background = Color(0xFFF3F6FB),    // Soft off-white background
    surface = Color(0xFFE9EEF6),       // Light gray for cards
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFF222222),
    onSurface = Color(0xFF222222),
)

private val ModernDarkColorScheme = darkColorScheme(
    primary = Color(0xFF62B6CB),       // Lighter blue for buttons
    secondary = Color(0xFF1B3A4B),     // Deep blue for accents
    tertiary = Color(0xFFF7C873),
    background = Color(0xFF121A23),    // Soft dark background
    surface = Color(0xFF1B263B),       // Slightly lighter for cards
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun MatchEmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ModernDarkColorScheme else ModernLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}