package com.time.applauncher.addict.feature.settings.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.time.applauncher.addict.core.designsystem.component.MonoLabel
import com.time.applauncher.addict.core.designsystem.component.StillScreenHeader
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.theme.JetBrainsMono
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme
import com.time.applauncher.addict.core.domain.model.ThemeMode
import com.time.applauncher.addict.core.domain.model.UserSettings
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsRoot(
    onBack: () -> Unit,
    onNavigateGoal: () -> Unit,
    onNavigateFocus: () -> Unit,
    onNavigateFavorites: () -> Unit,
    onNavigateDistracting: () -> Unit,
    onNavigatePrivacy: () -> Unit,
    onNavigateAbout: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val backupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let { viewModel.onAction(SettingsAction.OnBackupTarget(it)) } }
    val restoreLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { viewModel.onAction(SettingsAction.OnRestoreSource(it)) } }

    SettingsScreen(
        state = state,
        onBack = onBack,
        onAction = viewModel::onAction,
        onNavigateGoal = onNavigateGoal,
        onNavigateFocus = onNavigateFocus,
        onNavigateFavorites = onNavigateFavorites,
        onNavigateDistracting = onNavigateDistracting,
        onNavigatePrivacy = onNavigatePrivacy,
        onNavigateAbout = onNavigateAbout,
        onBackupClick = { backupLauncher.launch("still-backup.json") },
        onRestoreClick = { restoreLauncher.launch(arrayOf("application/json")) }
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onBack: () -> Unit,
    onAction: (SettingsAction) -> Unit,
    onNavigateGoal: () -> Unit,
    onNavigateFocus: () -> Unit,
    onNavigateFavorites: () -> Unit,
    onNavigateDistracting: () -> Unit,
    onNavigatePrivacy: () -> Unit,
    onNavigateAbout: () -> Unit,
    onBackupClick: () -> Unit = {},
    onRestoreClick: () -> Unit = {}
) {
    val s = state.settings
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 30.dp)
            .padding(top = 12.dp, bottom = 30.dp)
    ) {
        StillScreenHeader(title = "Settings", onAction = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Group("APPEARANCE") {
                LinkRow("Theme", themeLabel(s.theme), onClick = { onAction(SettingsAction.CycleTheme) })
                LinkRow("Font", "Manrope")
                ToggleRow("Colorful app icons", s.colorfulIcons, last = true) {
                    onAction(SettingsAction.ToggleColorfulIcons)
                }
            }
            Group("FOCUS & WELLBEING") {
                LinkRow("Daily usage goal", goalLabel(s.goalMinutes), onClick = onNavigateGoal)
                LinkRow("Focus settings", "", onClick = onNavigateFocus)
                LinkRow("Distracting apps", "", onClick = onNavigateDistracting)
                LinkRow(
                    "Intentional launch delay", "${s.launchDelaySeconds}s", last = true,
                    onClick = { onAction(SettingsAction.CycleLaunchDelay) }
                )
            }
            Group("HOME") {
                LinkRow("Favorites", "", onClick = onNavigateFavorites)
                ToggleRow("24-hour clock", s.use24h) { onAction(SettingsAction.Toggle24h) }
                ToggleRow("Daily quote", s.showQuote, last = true) { onAction(SettingsAction.ToggleQuote) }
            }
            Group("ACCESSIBILITY") {
                ToggleRow("High contrast", s.highContrast) { onAction(SettingsAction.ToggleHighContrast) }
                LinkRow("Text size", "Default", last = true)
            }
            Group("DATA") {
                LinkRow("Backup", "", onClick = onBackupClick)
                LinkRow("Restore", "", onClick = onRestoreClick)
                LinkRow("Privacy", "", last = true, onClick = onNavigatePrivacy)
            }
            if (state.dataMessage != null) {
                MonoLabel(
                    text = state.dataMessage,
                    modifier = Modifier.padding(top = 10.dp),
                    color = StillColors.TextSecondary,
                    letterSpacing = 0.14.em
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableNoRipple(onClick = onNavigateAbout)
                    .padding(vertical = 26.dp),
                contentAlignment = Alignment.Center
            ) {
                MonoLabel("STILL · VERSION 1.0 · ABOUT", color = StillColors.TextSecondary, fontSize = 11.sp, letterSpacing = 0.14.em)
            }
        }
    }
}

@Composable
private fun Group(title: String, content: @Composable () -> Unit) {
    MonoLabel(title, modifier = Modifier.padding(top = 22.dp, bottom = 8.dp), color = StillColors.TextTertiary)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, StillColors.Border, RoundedCornerShape(16.dp))
    ) {
        content()
    }
}

@Composable
private fun rowModifier(last: Boolean, onClick: (() -> Unit)?): Modifier {
    var m = Modifier
        .fillMaxWidth()
        .background(StillColors.Surface)
    if (!last) m = m.drawBehind {
        val h = 1.dp.toPx()
        drawLine(
            color = StillColors.Border,
            start = Offset(0f, size.height - h),
            end = Offset(size.width, size.height - h),
            strokeWidth = h
        )
    }
    if (onClick != null) m = m.clickableNoRipple(onClick = onClick)
    return m.padding(horizontal = 18.dp, vertical = 15.dp)
}

@Composable
private fun LinkRow(label: String, value: String, last: Boolean = false, onClick: (() -> Unit)? = null) {
    Row(
        modifier = rowModifier(last, onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = TextStyle(fontFamily = Manrope, fontSize = 15.sp, color = StillColors.TextPrimary))
        if (value.isNotEmpty()) {
            Text(value, style = TextStyle(fontFamily = JetBrainsMono, fontSize = 13.sp, color = StillColors.TextTertiary))
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, last: Boolean = false, onToggle: () -> Unit) {
    Row(
        modifier = rowModifier(last, onToggle),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = TextStyle(fontFamily = Manrope, fontSize = 15.sp, color = StillColors.TextPrimary))
        ToggleSwitch(checked)
    }
}

@Composable
private fun ToggleSwitch(checked: Boolean) {
    val knobOffset by animateDpAsState(if (checked) 21.dp else 3.dp, label = "knob")
    Box(
        modifier = Modifier
            .width(42.dp)
            .height(24.dp)
            .background(
                if (checked) StillColors.Accent else StillColors.TrackOff,
                RoundedCornerShape(14.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset, y = 3.dp)
                .size(18.dp)
                .background(if (checked) StillColors.OnAccent else StillColors.TextStrong, CircleShape)
        )
    }
}

private fun themeLabel(theme: ThemeMode) = when (theme) {
    ThemeMode.AMOLED -> "AMOLED"
    ThemeMode.DARK -> "Dark"
    ThemeMode.LIGHT -> "Light"
}

private fun goalLabel(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return if (m == 0) "${h}h" else "${h}h ${m}m"
}

@Preview
@Composable
private fun SettingsPreview() {
    StillTheme {
        SettingsScreen(
            state = SettingsState(UserSettings()),
            onBack = {}, onAction = {}, onNavigateGoal = {}, onNavigateFocus = {},
            onNavigateFavorites = {}, onNavigateDistracting = {}, onNavigatePrivacy = {}, onNavigateAbout = {}
        )
    }
}
