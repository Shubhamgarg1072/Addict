package com.time.applauncher.addict.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

// The prototype uses Manrope + JetBrains Mono. We fall back to the platform
// sans-serif / monospace families so the app renders identically offline without
// bundling font binaries or depending on downloadable fonts at runtime.
val Manrope: FontFamily = FontFamily.SansSerif
val JetBrainsMono: FontFamily = FontFamily.Monospace

val StillTypography = Typography(
    displayLarge = TextStyle(fontFamily = Manrope),
    displayMedium = TextStyle(fontFamily = Manrope),
    displaySmall = TextStyle(fontFamily = Manrope),
    headlineLarge = TextStyle(fontFamily = Manrope),
    headlineMedium = TextStyle(fontFamily = Manrope),
    headlineSmall = TextStyle(fontFamily = Manrope),
    titleLarge = TextStyle(fontFamily = Manrope),
    titleMedium = TextStyle(fontFamily = Manrope),
    titleSmall = TextStyle(fontFamily = Manrope),
    bodyLarge = TextStyle(fontFamily = Manrope),
    bodyMedium = TextStyle(fontFamily = Manrope),
    bodySmall = TextStyle(fontFamily = Manrope),
    labelLarge = TextStyle(fontFamily = Manrope),
    labelMedium = TextStyle(fontFamily = Manrope),
    labelSmall = TextStyle(fontFamily = Manrope)
)
