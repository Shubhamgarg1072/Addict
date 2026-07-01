package com.time.applauncher.addict.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.time.applauncher.addict.core.designsystem.component.MonoLabel
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme

@Composable
fun AboutRoot(onBack: () -> Unit) {
    AboutScreen(onBack = onBack)
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 34.dp)
            .padding(top = 12.dp, bottom = 40.dp)
    ) {
        MonoLabel(
            text = "← BACK",
            modifier = Modifier.clickableNoRipple(onClick = onBack).padding(vertical = 8.dp),
            color = StillColors.TextSecondary,
            fontSize = 11.sp,
            letterSpacing = 0.1.em
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
            Box(Modifier.size(15.dp).background(Color.White, CircleShape))
            Box(Modifier.height(24.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White)) { append("Still") }
                    withStyle(SpanStyle(color = Color.White.copy(alpha = 0.35f))) { append(".") }
                },
                style = TextStyle(fontFamily = Manrope, fontSize = 38.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.02).em)
            )
            Box(Modifier.height(16.dp))
            Text(
                text = "A launcher built on a single belief — the best app is the one you never open. No feeds, no badges, no bait.",
                style = TextStyle(fontFamily = Manrope, fontSize = 16.sp, lineHeight = 26.sp, color = StillColors.TextSecondary)
            )
            Box(Modifier.height(30.dp))
            MonoLabel(
                text = "VERSION 1.0.0\nMADE FOR ANDROID · MATERIAL 3\nYOUR DATA NEVER LEAVES THIS PHONE",
                color = StillColors.TextTertiary,
                fontSize = 11.sp,
                letterSpacing = 0.12.em
            )
        }
        Text(
            text = "\"Less scrolling. More living.\"",
            style = TextStyle(fontFamily = Manrope, fontSize = 13.sp, fontStyle = FontStyle.Italic, color = StillColors.TextTertiary)
        )
    }
}

@Preview
@Composable
private fun AboutPreview() {
    StillTheme { AboutScreen(onBack = {}) }
}
