package com.genzx.keuangan.ui.theme

import androidx.compose.ui.graphics.Color

// ── Primary: Deep Ocean Blue/Teal (dari pixel analisis #023048 → #0a8692) ──
val OceanDeep = Color(0xFF012D44)
val OceanMid = Color(0xFF034866)
val OceanLight = Color(0xFF056A85)
val OceanBright = Color(0xFF0A8A9C)
val OceanContainer = Color(0xFFB2EBF2)
val OnOceanContainer = Color(0xFF001F28)

// ── Gradient colors for header ──
val GradientStart = Color(0xFF012D44)
val GradientMid = Color(0xFF034866)
val GradientEnd = Color(0xFF0A8A9C)

// ── Accent: Purple/Violet (dari pixel #8056e8, #d8c3fb) ──
val VioletAccent = Color(0xFF7C4DDB)
val VioletLight = Color(0xFF9B72EF)
val VioletContainer = Color(0xFFEDE7F6)
val OnVioletContainer = Color(0xFF21005D)

// ── Accent: Pink/Rose ──
val RoseAccent = Color(0xFFE91E8C)
val RoseSoft = Color(0xFFF48FB1)
val RoseContainer = Color(0xFFFCE4EC)

// ── Accent: Mint/Green ──
val MintAccent = Color(0xFF00C896)
val MintSoft = Color(0xFF80CBC4)
val MintContainer = Color(0xFFE0F2F1)

// ── Accent: Amber/Orange ──
val AmberAccent = Color(0xFFFFAB00)
val AmberSoft = Color(0xFFFFCC02)
val AmberContainer = Color(0xFFFFF8E1)

// ── Status Colors ──
val IncomeGreen = Color(0xFF00C853)
val IncomeGreenLight = Color(0xFFE8F5E9)
val IncomeGreenDark = Color(0xFF00953D)
val ExpenseRed = Color(0xFFFF1744)
val ExpenseRedLight = Color(0xFFFFEBEE)
val ExpenseRedDark = Color(0xFFD50000)
val WarningAmber = Color(0xFFFF8F00)
val WarningAmberLight = Color(0xFFFFF8E1)

// ── Neutral: Light Theme ──
val BackgroundLight = Color(0xFFF4F6FA)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF0F2F8)
val OutlineLight = Color(0xFFDDE1ED)
val TextPrimary = Color(0xFF0D1B2A)
val TextSecondary = Color(0xFF526070)
val TextHint = Color(0xFFABB8C3)

// ── Neutral: Dark Theme ──
val BackgroundDark = Color(0xFF0A0F14)
val SurfaceDark = Color(0xFF121A22)
val SurfaceVariantDark = Color(0xFF1E2A35)
val SurfaceDark2 = Color(0xFF1A2535)
val OutlineDark = Color(0xFF2A3A4A)
val TextPrimaryDark = Color(0xFFE8F4F8)
val TextSecondaryDark = Color(0xFF90AAB8)
val TextHintDark = Color(0xFF4A6070)

// ── Card gradient colors ──
val CardGreen1 = Color(0xFF00C896)
val CardGreen2 = Color(0xFF00897B)
val CardPurple1 = Color(0xFF7C4DDB)
val CardPurple2 = Color(0xFF512DA8)
val CardBlue1 = Color(0xFF0288D1)
val CardBlue2 = Color(0xFF01579B)
val CardRose1 = Color(0xFFE91E8C)
val CardRose2 = Color(0xFFC2185B)
val CardAmber1 = Color(0xFFFFAB00)
val CardAmber2 = Color(0xFFFF6F00)

// ── Category Colors (vibrant for GenZ) ──
val CategoryColors = listOf(
    Color(0xFF0A8A9C), // Teal
    Color(0xFF7C4DDB), // Violet
    Color(0xFF00C896), // Mint
    Color(0xFFE91E8C), // Rose
    Color(0xFFFFAB00), // Amber
    Color(0xFF3B82F6), // Blue
    Color(0xFFFF5252), // Red
    Color(0xFF00BCD4), // Cyan
    Color(0xFF8BC34A), // Green
    Color(0xFFFF9800), // Orange
    Color(0xFF9C27B0), // Purple
    Color(0xFFEC407A), // Pink
    Color(0xFF26C6DA), // Cyan light
    Color(0xFFFFEE58), // Yellow
)

// ── Backward compat aliases ──
val Teal = OceanLight
val TealDark = OceanDeep
val TealDarkTheme = OceanBright
val TealLight = OceanBright
val TealContainer = OceanContainer
val OnTealContainer = OnOceanContainer
val LilacSoft = VioletLight
val LilacContainer = VioletContainer
val SurfaceVariant = SurfaceVariantLight
