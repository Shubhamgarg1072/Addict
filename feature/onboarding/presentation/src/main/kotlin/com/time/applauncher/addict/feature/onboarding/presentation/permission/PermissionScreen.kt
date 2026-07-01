package com.time.applauncher.addict.feature.onboarding.presentation.permission

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.time.applauncher.addict.core.data.usage.UsagePermission
import com.time.applauncher.addict.core.designsystem.component.StillPrimaryButton
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme
import com.time.applauncher.addict.core.presentation.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun PermissionRoot(
    onDone: () -> Unit,
    viewModel: PermissionViewModel = koinViewModel()
) {
    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            PermissionEvent.Done -> onDone()
        }
    }
    PermissionScreen(
        onGrant = {
            UsagePermission.launchSettings(context)
            viewModel.onAction(PermissionAction.OnComplete)
        },
        onSkip = { viewModel.onAction(PermissionAction.OnComplete) }
    )
}

@Composable
fun PermissionScreen(onGrant: () -> Unit, onSkip: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 34.dp, vertical = 44.dp)
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .border(1.dp, StillColors.BorderStrong, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .border(2.dp, StillColors.TextSecondary, CircleShape)
                )
            }
            Box(Modifier.height(28.dp))
            Text(
                text = "Usage access",
                style = TextStyle(fontFamily = Manrope, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = (-0.01).em)
            )
            Box(Modifier.height(16.dp))
            Text(
                text = "To show your screen time and help you build focus, Still needs Usage Access.",
                style = TextStyle(fontFamily = Manrope, fontSize = 15.sp, lineHeight = 24.sp, color = StillColors.TextSecondary)
            )
            Box(Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StillColors.Surface, RoundedCornerShape(14.dp))
                    .border(1.dp, StillColors.Border, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.size(6.dp).background(StillColors.TextTertiary, CircleShape))
                Text(
                    text = "Your data stays on your device. Nothing is ever uploaded.",
                    style = TextStyle(fontFamily = Manrope, fontSize = 13.sp, lineHeight = 20.sp, color = StillColors.TextSecondary)
                )
            }
        }
        StillPrimaryButton(
            text = "Grant access",
            onClick = onGrant,
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickableNoRipple(onClick = onSkip)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Not now",
                style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, color = StillColors.TextSecondary)
            )
        }
    }
}

@Preview
@Composable
private fun PermissionPreview() {
    StillTheme { PermissionScreen(onGrant = {}, onSkip = {}) }
}
