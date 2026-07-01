package com.time.applauncher.addict.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/** AMOLED palette lifted from the Still prototype. */
object StillColors {
    val Background = Color(0xFF000000)
    val Surface = Color(0xFF0D0D0F)
    val SurfaceElevated = Color(0xFF141416)
    val PanelDark = Color(0xFF0B0B0D)

    val Accent = Color(0xFFFFFFFF)
    val OnAccent = Color(0xFF000000)

    val TextPrimary = Color.White.copy(alpha = 0.90f)
    val TextSecondary = Color.White.copy(alpha = 0.55f)
    val TextTertiary = Color.White.copy(alpha = 0.35f)
    val TextFaint = Color.White.copy(alpha = 0.28f)

    val Border = Color.White.copy(alpha = 0.06f)
    val BorderStrong = Color.White.copy(alpha = 0.12f)
    val BorderSelected = Color.White.copy(alpha = 0.50f)

    val TrackOff = Color.White.copy(alpha = 0.14f)
    val Divider = Color.White.copy(alpha = 0.10f)
}
