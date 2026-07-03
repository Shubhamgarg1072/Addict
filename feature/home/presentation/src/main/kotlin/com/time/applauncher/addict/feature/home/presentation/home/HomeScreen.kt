package com.time.applauncher.addict.feature.home.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.time.applauncher.addict.core.designsystem.component.MonoLabel
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme
import com.time.applauncher.addict.core.presentation.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoot(
    onNavigateToDashboard: () -> Unit,
    onNavigateToAgenda: () -> Unit,
    onNavigateToGoal: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToGate: (String, String) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            HomeEvent.NavigateToDashboard -> onNavigateToDashboard()
            HomeEvent.NavigateToAgenda -> onNavigateToAgenda()
            HomeEvent.NavigateToGoal -> onNavigateToGoal()
            HomeEvent.NavigateToSearch -> onNavigateToSearch()
            is HomeEvent.NavigateToGate -> onNavigateToGate(event.packageName, event.label)
        }
    }
    HomeScreen(state = state, onAction = viewModel::onAction)
}

@Composable
fun HomeScreen(state: HomeState, onAction: (HomeAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 30.dp)
            .padding(top = 20.dp, bottom = 20.dp)
    ) {
        // Clock + date
        Text(
            text = state.clock,
            modifier = Modifier.clickableNoRipple { onAction(HomeAction.OnClickStats) },
            style = TextStyle(fontSize = 82.sp, fontWeight = FontWeight.Light, color = Color.White, letterSpacing = (-0.04).em)
        )
        Text(
            text = state.date,
            modifier = Modifier
                .padding(top = 14.dp)
                .clickableNoRipple { onAction(HomeAction.OnClickDate) },
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = StillColors.TextSecondary)
        )

        // Stats
        Row(modifier = Modifier.padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(22.dp)) {
            StatColumn("SCREEN TIME", state.screenTime)
            Box(Modifier.width(1.dp).height(40.dp).background(StillColors.Divider))
            StatColumn("UNLOCKS", state.unlocks.toString())
        }

        // Daily goal
        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .clickableNoRipple { onAction(HomeAction.OnClickGoal) }
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MonoLabel("DAILY GOAL", color = StillColors.TextTertiary, letterSpacing = 0.14.em)
                MonoLabel(state.goalRemaining, color = StillColors.TextSecondary, letterSpacing = 0.14.em)
            }
            Box(
                modifier = Modifier
                    .padding(top = 9.dp)
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color.White.copy(alpha = 0.10f), RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(state.goalFraction)
                        .height(4.dp)
                        .background(
                            if (state.goalOver) Color.White.copy(alpha = 0.4f) else Color.White,
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }

        // Favorites — scrolls only when the list doesn't fit the small screens
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            state.favorites.forEach { fav ->
                Text(
                    text = fav.label,
                    modifier = Modifier
                        .clickableNoRipple { onAction(HomeAction.OnClickFavorite(fav)) }
                        .padding(vertical = 9.dp),
                    style = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.Medium, color = StillColors.TextPrimary, letterSpacing = (-0.01).em)
                )
            }
        }

        // Quote
        if (state.showQuote) {
            Text(
                text = state.quote,
                modifier = Modifier.padding(bottom = 18.dp),
                style = TextStyle(fontSize = 14.sp, fontStyle = FontStyle.Italic, lineHeight = 21.sp, color = StillColors.TextTertiary)
            )
        }

        // Swipe up · search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickableNoRipple { onAction(HomeAction.OnClickSearch) }
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.width(26.dp).height(3.dp).background(StillColors.TextTertiary, RoundedCornerShape(2.dp)))
            MonoLabel(
                "  SWIPE UP · SEARCH",
                color = StillColors.TextSecondary,
                fontSize = 11.sp,
                letterSpacing = 0.18.em
            )
        }
    }
}

@Composable
private fun StatColumn(label: String, value: String) {
    Column {
        MonoLabel(label, color = StillColors.TextTertiary, letterSpacing = 0.14.em)
        Text(
            text = value,
            modifier = Modifier.padding(top = 5.dp),
            style = TextStyle(fontSize = 15.sp, color = StillColors.TextPrimary, fontFamily = com.time.applauncher.addict.core.designsystem.theme.JetBrainsMono)
        )
    }
}

@Preview
@Composable
private fun HomePreview() {
    StillTheme {
        HomeScreen(
            state = HomeState(
                clock = "9:41",
                date = "Sunday, January 5",
                screenTime = "2h 18m",
                unlocks = 47,
                goalFraction = 0.6f,
                goalRemaining = "1h 12m left",
                favorites = listOf(
                    FavoriteUi("a", "Messages", false),
                    FavoriteUi("b", "Camera", false),
                    FavoriteUi("c", "Maps", false)
                )
            ),
            onAction = {}
        )
    }
}
