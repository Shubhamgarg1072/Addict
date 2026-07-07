package com.time.applauncher.addict.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors

/** Flat single-line input matching the prototype's dark cards. */
@Composable
fun StillTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        textStyle = TextStyle(
            fontFamily = Manrope,
            fontSize = 15.sp,
            color = StillColors.TextPrimary
        ),
        cursorBrush = SolidColor(StillColors.Accent),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .background(StillColors.PanelDark, RoundedCornerShape(12.dp))
                    .border(1.dp, StillColors.BorderStrong, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(
                            fontFamily = Manrope,
                            fontSize = 15.sp,
                            color = StillColors.TextTertiary
                        )
                    )
                }
                innerTextField()
            }
        }
    )
}
