package com.time.applauncher.addict.feature.home.presentation.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.time.applauncher.addict.core.designsystem.component.MonoLabel
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.theme.JetBrainsMono
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme

private data class FocusOption(val minutes: Int, val label: String, val sub: String)

private val FOCUS_OPTIONS = listOf(
    FocusOption(15, "15 minutes", "QUICK RESET"),
    FocusOption(30, "30 minutes", "DEEP WORK"),
    FocusOption(60, "1 hour", "FLOW"),
    FocusOption(120, "2 hours", "DEEP FOCUS"),
    FocusOption(600, "Until tomorrow", "FULL RESET")
)

@Composable
fun FocusSetupRoot(
    onBack: () -> Unit,
    onSelectDuration: (Int) -> Unit
) {
    FocusSetupScreen(onBack = onBack, onSelect = onSelectDuration)
}

@Composable
fun FocusSetupScreen(onBack: () -> Unit, onSelect: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 30.dp)
            .padding(top = 16.dp, bottom = 34.dp)
    ) {
        MonoLabel(
            "← BACK",
            modifier = Modifier.clickableNoRipple(onClick = onBack).padding(bottom = 26.dp),
            color = StillColors.TextSecondary,
            fontSize = 11.sp,
            letterSpacing = 0.1.em
        )
        Text("Focus", style = TextStyle(fontFamily = Manrope, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = StillColors.TextStrong, letterSpacing = (-0.02).em))
        Text(
            text = "Everything but the essentials disappears. Choose how long to stay present.",
            modifier = Modifier.padding(top = 12.dp),
            style = TextStyle(fontFamily = Manrope, fontSize = 15.sp, lineHeight = 23.sp, color = StillColors.TextSecondary)
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 34.dp)) {
            FOCUS_OPTIONS.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StillColors.Surface, RoundedCornerShape(18.dp))
                        .border(1.dp, StillColors.BorderStrong, RoundedCornerShape(18.dp))
                        .clickableNoRipple { onSelect(option.minutes) }
                        .padding(horizontal = 22.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(option.label, style = TextStyle(fontFamily = Manrope, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = StillColors.TextStrong))
                    Text(option.sub, style = TextStyle(fontFamily = JetBrainsMono, fontSize = 11.sp, color = StillColors.TextTertiary))
                }
            }
        }
        Box(Modifier.weight(1f))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            MonoLabel("ALLOWED · PHONE · MESSAGES · CAMERA · CALENDAR", color = StillColors.TextTertiary, fontSize = 10.sp, letterSpacing = 0.14.em)
        }
    }
}

@Preview
@Composable
private fun FocusSetupPreview() {
    StillTheme { FocusSetupScreen(onBack = {}, onSelect = {}) }
}
