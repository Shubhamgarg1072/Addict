package com.time.applauncher.addict.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.time.applauncher.addict.core.designsystem.component.MonoLabel
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme

private val PRIVACY_POINTS = listOf(
    "Everything stays on this phone" to
        "Your settings, notes, agenda and usage insights are stored locally. Still has no servers, no accounts and no analytics.",
    "Usage access is read-only" to
        "The Usage Access permission is used solely to show your own screen time and unlock counts. It is never transmitted anywhere.",
    "Backups are yours" to
        "A backup is a plain file saved where you choose. Only you can read it, share it or delete it."
)

@Composable
fun PrivacyRoot(onBack: () -> Unit) {
    PrivacyScreen(onBack = onBack)
}

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
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
        Box(Modifier.height(26.dp))
        Text(
            text = "Privacy",
            style = TextStyle(fontFamily = Manrope, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = StillColors.TextStrong, letterSpacing = (-0.02).em)
        )
        Column(
            modifier = Modifier.weight(1f).padding(top = 30.dp),
            verticalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            PRIVACY_POINTS.forEach { (title, body) ->
                Column {
                    Text(
                        text = title,
                        style = TextStyle(fontFamily = Manrope, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = StillColors.TextPrimary)
                    )
                    Text(
                        text = body,
                        modifier = Modifier.padding(top = 6.dp),
                        style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, lineHeight = 22.sp, color = StillColors.TextSecondary)
                    )
                }
            }
        }
        MonoLabel(
            text = "NO SERVERS · NO ACCOUNTS · NO TRACKING",
            color = StillColors.TextTertiary,
            fontSize = 10.sp,
            letterSpacing = 0.14.em
        )
    }
}

@Preview
@Composable
private fun PrivacyPreview() {
    StillTheme { PrivacyScreen(onBack = {}) }
}
