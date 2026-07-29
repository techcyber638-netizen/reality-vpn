package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003640),
    onPrimaryContainer = CyberCyan,
    secondary = CyberPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2E0066),
    onSecondaryContainer = Color(0xFFD0BFFF),
    tertiary = CyberGreen,
    onTertiary = Color.Black,
    background = CyberDarkBg,
    onBackground = CyberTextPrimary,
    surface = CyberCardBg,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberCardBorder,
    onSurfaceVariant = CyberTextSecondary,
    error = CyberMagenta,
    onError = Color.White,
    outline = CyberCyan.copy(alpha = 0.4f)
)

@Composable
fun CyberTechTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
