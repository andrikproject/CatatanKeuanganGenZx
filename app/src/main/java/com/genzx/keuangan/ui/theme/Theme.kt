package com.genzx.keuangan.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Teal,
    onPrimary = SurfaceLight,
    primaryContainer = TealContainer,
    onPrimaryContainer = OnTealContainer,
    secondary = LilacSoft,
    onSecondary = SurfaceLight,
    secondaryContainer = LilacContainer,
    onSecondaryContainer = TextPrimary,
    tertiary = MintSoft,
    onTertiary = SurfaceLight,
    tertiaryContainer = MintContainer,
    onTertiaryContainer = TextPrimary,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = OutlineLight,
    error = ExpenseRed,
    onError = SurfaceLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = TealDarkTheme,
    onPrimary = BackgroundDark,
    primaryContainer = TealDark,
    onPrimaryContainer = TealContainer,
    secondary = LilacSoft,
    onSecondary = BackgroundDark,
    secondaryContainer = Color(0xFF4A148C),
    onSecondaryContainer = LilacContainer,
    background = BackgroundDark,
    onBackground = SurfaceLight,
    surface = SurfaceDark,
    onSurface = SurfaceLight,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextHint,
    outline = Color(0xFF30363D),
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
            window.statusBarColor = colorScheme.primary.toArgb()
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
