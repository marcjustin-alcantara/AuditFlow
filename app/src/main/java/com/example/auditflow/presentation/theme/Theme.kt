package com.example.auditflow.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Force Cyberpunk dark mode globally
private val CyberpunkColorScheme = darkColorScheme(
    primary = DefaultPrimary,
    secondary = DefaultSecondary,
    background = BackgroundDark,
    surface = SurfaceDark,
    error = ErrorRed,
    onPrimary = Color.Black, // Black text inside neon buttons for high contrast
    onBackground = Color(0xFFE0E0E0), // Off-white terminal text
    onSurface = Color(0xFFE0E0E0)
)

@Composable
fun AuditFlowTheme(
    dynamicPrimaryColor: Color = DefaultPrimary,
    content: @Composable () -> Unit
) {
    // Dynamically inject the department's neon color
    val colorScheme = CyberpunkColorScheme.copy(
        primary = dynamicPrimaryColor
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Pitch black status bar
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}