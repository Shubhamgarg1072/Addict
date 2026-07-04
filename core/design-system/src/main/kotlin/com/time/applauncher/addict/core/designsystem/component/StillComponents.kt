package com.time.applauncher.addict.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.time.applauncher.addict.core.designsystem.theme.JetBrainsMono
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors

/** Monospace, tracked, usually-uppercase caption used everywhere in the prototype. */
@Composable
fun MonoLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = StillColors.TextTertiary,
    fontSize: TextUnit = 10.sp,
    letterSpacing: TextUnit = 0.16.em
) {
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontFamily = JetBrainsMono,
            fontSize = fontSize,
            letterSpacing = letterSpacing,
            color = color
        )
    )
}

/** The white pill CTA ("Grant access", "Get started"). */
@Composable
fun StillPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(StillColors.Accent, RoundedCornerShape(30.dp))
            .clickableNoRipple(onClick = onClick)
            .padding(horizontal = 30.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = StillColors.OnAccent
            )
        )
    }
}

/** Standard screen header: big title on the left, a mono action label (DONE / ← BACK) on the right. */
@Composable
fun StillScreenHeader(
    title: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String = "DONE"
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = StillColors.TextStrong,
                letterSpacing = (-0.01).em
            )
        )
        MonoLabel(
            text = actionLabel,
            modifier = Modifier.clickableNoRipple(onClick = onAction),
            color = StillColors.TextSecondary,
            fontSize = 11.sp,
            letterSpacing = 0.1.em
        )
    }
}

/** Decorative home-gesture pill (used on prototype-only surfaces). */
@Composable
fun HomeIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(StillColors.Ink(0.55f), RoundedCornerShape(3.dp))
    )
}
