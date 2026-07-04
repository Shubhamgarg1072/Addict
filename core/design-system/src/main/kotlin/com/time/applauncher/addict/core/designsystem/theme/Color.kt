package com.time.applauncher.addict.core.designsystem.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.time.applauncher.addict.core.domain.model.ThemeMode

/**
 * Resolved color values for one theme. `ink` is the base color text and hairline
 * fills are derived from (white on dark themes, near-black on light).
 */
data class StillPalette(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val panelDark: Color,
    val accent: Color,
    val onAccent: Color,
    val ink: Color,
    val isLight: Boolean,
    val highContrast: Boolean
)

private val AmoledPalette = StillPalette(
    background = Color(0xFF000000),
    surface = Color(0xFF0D0D0F),
    surfaceElevated = Color(0xFF141416),
    panelDark = Color(0xFF0B0B0D),
    accent = Color(0xFFFFFFFF),
    onAccent = Color(0xFF000000),
    ink = Color.White,
    isLight = false,
    highContrast = false
)

private val DarkPalette = AmoledPalette.copy(
    background = Color(0xFF121214),
    surface = Color(0xFF1A1A1D),
    surfaceElevated = Color(0xFF202024),
    panelDark = Color(0xFF17171A)
)

private val LightPalette = StillPalette(
    background = Color(0xFFFAFAF8),
    surface = Color(0xFFF0F0EE),
    surfaceElevated = Color(0xFFE9E9E7),
    panelDark = Color(0xFFF2F2F0),
    accent = Color(0xFF111111),
    onAccent = Color(0xFFFFFFFF),
    ink = Color(0xFF111111),
    isLight = true,
    highContrast = false
)

fun stillPaletteFor(theme: ThemeMode, highContrast: Boolean): StillPalette {
    val base = when (theme) {
        ThemeMode.AMOLED -> AmoledPalette
        ThemeMode.DARK -> DarkPalette
        ThemeMode.LIGHT -> LightPalette
    }
    return base.copy(highContrast = highContrast)
}

/**
 * Theme tokens, resolved from the palette [StillTheme] installs. Backed by snapshot
 * state so every composable reading a token recomposes when the theme changes.
 */
object StillColors {
    internal var palette: StillPalette by mutableStateOf(AmoledPalette)

    val Background get() = palette.background
    val Surface get() = palette.surface
    val SurfaceElevated get() = palette.surfaceElevated
    val PanelDark get() = palette.panelDark

    val Accent get() = palette.accent
    val OnAccent get() = palette.onAccent

    /** Full-strength text (titles, the clock). */
    val TextStrong get() = palette.ink

    val TextPrimary get() = Ink(0.90f)
    val TextSecondary get() = Ink(0.55f)
    val TextTertiary get() = Ink(0.35f)
    val TextFaint get() = Ink(0.28f)

    val Border get() = Ink(0.06f)
    val BorderStrong get() = Ink(0.12f)
    val BorderSelected get() = Ink(0.50f)

    val TrackOff get() = Ink(0.14f)
    val Divider get() = Ink(0.10f)

    /** The theme's ink at [alpha] — replaces hardcoded `Color.White.copy(alpha)`. */
    fun Ink(alpha: Float): Color {
        val boosted = if (palette.highContrast) (alpha * 1.45f).coerceAtMost(1f) else alpha
        return palette.ink.copy(alpha = boosted)
    }
}
