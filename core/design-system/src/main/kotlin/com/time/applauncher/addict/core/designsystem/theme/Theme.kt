package com.time.applauncher.addict.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.time.applauncher.addict.core.domain.model.ThemeMode

@Composable
fun StillTheme(
    themeMode: ThemeMode = ThemeMode.AMOLED,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val palette = stillPaletteFor(themeMode, highContrast)
    // Unconditional write: equal values are deduped by the state's structural
    // equality policy, and not reading first avoids a backwards write.
    StillColors.palette = palette

    val colorScheme = (if (palette.isLight) lightColorScheme() else darkColorScheme()).copy(
        background = StillColors.Background,
        surface = StillColors.Surface,
        primary = StillColors.Accent,
        onPrimary = StillColors.OnAccent,
        onBackground = StillColors.TextPrimary,
        onSurface = StillColors.TextPrimary
    )
    MaterialTheme(
        colorScheme = colorScheme,
        typography = StillTypography,
        content = content
    )
}
