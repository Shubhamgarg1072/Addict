package com.time.applauncher.addict.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.time.applauncher.addict.core.designsystem.component.MonoLabel
import com.time.applauncher.addict.core.designsystem.component.StillScreenHeader
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesRoot(
    onBack: () -> Unit,
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FavoritesScreen(state = state, onBack = onBack, onAction = viewModel::onAction)
}

@Composable
fun FavoritesScreen(
    state: FavoritesState,
    onBack: () -> Unit,
    onAction: (FavoritesAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 30.dp)
            .padding(top = 12.dp, bottom = 20.dp)
    ) {
        StillScreenHeader(title = "Favorites", onAction = onBack)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MonoLabel("SHOWN ON YOUR HOME SCREEN", color = StillColors.TextTertiary, letterSpacing = 0.14.em)
            MonoLabel(
                "${state.selectedCount} OF $MAX_FAVORITES",
                color = if (state.atLimit) StillColors.TextSecondary else StillColors.TextTertiary,
                letterSpacing = 0.14.em
            )
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(state.apps, key = { it.packageName }) { app ->
                AppCheckRow(
                    label = app.label,
                    selected = app.selected,
                    enabled = app.selected || !state.atLimit,
                    onClick = { onAction(FavoritesAction.OnToggleApp(app.packageName)) }
                )
            }
        }
    }
}

@Composable
private fun AppCheckRow(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableNoRipple(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 16.sp,
                color = if (enabled) StillColors.TextPrimary else StillColors.TextTertiary
            )
        )
        Box(
            modifier = Modifier
                .size(17.dp)
                .background(
                    if (selected) StillColors.Accent else Color.Transparent,
                    RoundedCornerShape(5.dp)
                )
                .border(
                    1.5.dp,
                    if (selected) StillColors.Accent else StillColors.BorderStrong,
                    RoundedCornerShape(5.dp)
                )
        )
    }
}

@Preview
@Composable
private fun FavoritesPreview() {
    StillTheme {
        FavoritesScreen(
            state = FavoritesState(
                apps = listOf(
                    FavoriteAppUi("a", "Messages", true),
                    FavoriteAppUi("b", "Camera", true),
                    FavoriteAppUi("c", "Chrome", false)
                ),
                selectedCount = 2
            ),
            onBack = {},
            onAction = {}
        )
    }
}
