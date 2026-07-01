package com.time.applauncher.addict.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.time.applauncher.addict.core.domain.model.ThemeMode

@Composable
fun StillTheme(
    themeMode: ThemeMode = ThemeMode.AMOLED,
    content: @Composable () -> Unit
) {
    val background = when (themeMode) {
        ThemeMode.AMOLED -> StillColors.Background
        ThemeMode.DARK -> StillColors.Surface
        ThemeMode.LIGHT -> StillColors.Background // launcher stays dark; light is a no-op placeholder
    }
    val colorScheme = darkColorScheme(
        background = background,
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
