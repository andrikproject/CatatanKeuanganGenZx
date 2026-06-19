package com.genzx.keuangan.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = OceanLight,
    onPrimary = Color.White,
    primaryContainer = OceanContainer,
    onPrimaryContainer = OnOceanContainer,
    secondary = VioletAccent,
    onSecondary = Color.White,
    secondaryContainer = VioletContainer,
    onSecondaryContainer = OnVioletContainer,
    tertiary = MintAccent,
    onTertiary = Color.White,
    tertiaryContainer = MintContainer,
    onTertiaryContainer = TextPrimary,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = OutlineLight,
    error = ExpenseRed,
    onError = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary = OceanBright,
    onPrimary = BackgroundDark,
    primaryContainer = OceanMid,
    onPrimaryContainer = OceanContainer,
    secondary = VioletLight,
    onSecondary = BackgroundDark,
    secondaryContainer = Color(0xFF3D1A8C),
    onSecondaryContainer = VioletContainer,
    tertiary = MintSoft,
    onTertiary = BackgroundDark,
    tertiaryContainer = Color(0xFF00453D),
    onTertiaryContainer = MintContainer,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = OutlineDark,
    error = ExpenseRed,
    onError = BackgroundDark,
)

@Composable
fun CatatanKeuanganGenZxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GenZxTypography,
        shapes = GenZxShapes,
        content = content
    )
}
