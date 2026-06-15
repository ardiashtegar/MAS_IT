package com.masit.hub.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MasItColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryBlue,
    onPrimaryContainer = TextOnPrimary,
    secondary = AccentYellow,
    onSecondary = PrimaryBlue,
    secondaryContainer = AccentYellow,
    onSecondaryContainer = PrimaryBlue,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceColor,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextSecondary,
    error = DangerRed,
    onError = TextOnPrimary,
    outline = InputBorder,
    outlineVariant = CategoryBorder
)

@Composable
fun MasITTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = MasItColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PrimaryBlue.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}