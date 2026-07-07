package com.time.applauncher.addict.feature.home.presentation.search

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.time.applauncher.addict.core.designsystem.component.MonoLabel
import com.time.applauncher.addict.core.designsystem.component.clickableNoRipple
import com.time.applauncher.addict.core.designsystem.theme.JetBrainsMono
import com.time.applauncher.addict.core.designsystem.theme.Manrope
import com.time.applauncher.addict.core.designsystem.theme.StillColors
import com.time.applauncher.addict.core.designsystem.theme.StillTheme
import com.time.applauncher.addict.core.presentation.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

private val KEY_ROWS = listOf("qwertyuiop", "asdfghjkl", "zxcvbnm")

@Composable
fun SearchRoot(
    onBack: () -> Unit,
    onNavigateToGate: (String, String) -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToAgenda: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is SearchEvent.NavigateToGate -> onNavigateToGate(event.packageName, event.label)
            SearchEvent.NavigateToNotes -> onNavigateToNotes()
            SearchEvent.NavigateToAgenda -> onNavigateToAgenda()
            SearchEvent.NavigateToDashboard -> onNavigateToDashboard()
            SearchEvent.NavigateToSettings -> onNavigateToSettings()
            SearchEvent.Close -> onBack()
        }
    }
    SearchScreen(state = state, onAction = viewModel::onAction)
}

@Composable
fun SearchScreen(state: SearchState, onAction: (SearchAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StillColors.Background)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Query row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 26.dp, end = 26.dp, top = 8.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Bottom) {
                Text(
                    text = state.query,
                    style = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.SemiBold, color = StillColors.TextStrong, letterSpacing = (-0.01).em)
                )
                BlinkingCaret()
            }
            MonoLabel(
                "CLOSE",
                modifier = Modifier.clickableNoRipple { onAction(SearchAction.OnClose) },
                color = StillColors.TextSecondary,
                fontSize = 11.sp,
                letterSpacing = 0.1.em
            )
        }

        // Results
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp)
        ) {
            if (state.showRecents && state.recents.isNotEmpty()) {
                MonoLabel("RECENT", modifier = Modifier.padding(top = 4.dp, bottom = 10.dp), color = StillColors.TextTertiary)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    state.recents.forEach { app ->
                        Box(
                            modifier = Modifier
                                .padding(bottom = 8.dp)
                                .border(1.dp, StillColors.BorderStrong, RoundedCornerShape(20.dp))
                                .clickableNoRipple { onAction(SearchAction.OnSelectApp(app.packageName, app.name, app.isDistracting)) }
                                .padding(horizontal = 15.dp, vertical = 8.dp)
                        ) {
                            Text(app.name, style = TextStyle(fontFamily = Manrope, fontSize = 14.sp, color = StillColors.TextPrimary))
                        }
                    }
                }
                Box(Modifier.height(14.dp))
            }

            state.builtins.forEach { builtin ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableNoRipple { onAction(SearchAction.OnSelectBuiltin(builtin.target)) }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(builtin.name, style = TextStyle(fontFamily = Manrope, fontSize = 19.sp, fontWeight = FontWeight.Medium, color = StillColors.TextPrimary))
                    MonoLabel("STILL", color = StillColors.TextTertiary, fontSize = 9.sp, letterSpacing = 0.12.em)
                }
            }

            state.results.forEach { app ->
                if (app.showLetter) {
                    MonoLabel(app.letter, modifier = Modifier.padding(top = 14.dp, bottom = 4.dp), color = StillColors.TextFaint)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableNoRipple { onAction(SearchAction.OnSelectApp(app.packageName, app.name, app.isDistracting)) }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(app.name, style = TextStyle(fontSize = 19.sp, fontWeight = FontWeight.Medium, color = StillColors.TextPrimary))
                    if (app.isDistracting) {
                        Box(
                            modifier = Modifier
                                .border(1.dp, StillColors.BorderStrong, RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            MonoLabel("MINDFUL", color = StillColors.TextTertiary, fontSize = 9.sp, letterSpacing = 0.12.em)
                        }
                    }
                }
            }
            Box(Modifier.height(12.dp))
        }

        // Keyboard
        Keyboard(onAction)
    }
}

@Composable
private fun Keyboard(onAction: (SearchAction) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StillColors.PanelDark)
            .padding(horizontal = 5.dp, vertical = 10.dp)
    ) {
        KEY_ROWS.forEachIndexed { index, row ->
            val hPad = when (index) {
                1 -> 14.dp
                2 -> 30.dp
                else -> 0.dp
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = hPad, vertical = 3.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { ch ->
                    Key(ch.toString(), Modifier.weight(1f)) { onAction(SearchAction.OnKey(ch)) }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .background(StillColors.Ink(0.06f), RoundedCornerShape(8.dp))
                    .clickableNoRipple { onAction(SearchAction.OnSpace) },
                contentAlignment = Alignment.Center
            ) {
                Text("space", style = TextStyle(fontFamily = Manrope, fontSize = 13.sp, color = StillColors.TextStrong))
            }
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(38.dp)
                    .background(StillColors.Ink(0.06f), RoundedCornerShape(8.dp))
                    .clickableNoRipple { onAction(SearchAction.OnBackspace) },
                contentAlignment = Alignment.Center
            ) {
                Text("⌫", style = TextStyle(fontFamily = JetBrainsMono, fontSize = 15.sp, color = StillColors.TextStrong))
            }
        }
    }
}

@Composable
private fun Key(label: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(42.dp)
            .background(StillColors.Ink(0.08f), RoundedCornerShape(7.dp))
            .clickableNoRipple(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, style = TextStyle(fontFamily = Manrope, fontSize = 17.sp, fontWeight = FontWeight.Medium, color = StillColors.TextStrong))
    }
}

@Composable
private fun BlinkingCaret() {
    val transition = rememberInfiniteTransition(label = "caret")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 1100
                1f at 0
                1f at 549
                0f at 550
                0f at 1099
            }
        ),
        label = "caretAlpha"
    )
    Box(
        modifier = Modifier
            .padding(start = 2.dp, bottom = 3.dp)
            .alpha(alpha)
            .width(2.dp)
            .height(26.dp)
            .background(StillColors.TextStrong)
    )
}

@Preview
@Composable
private fun SearchPreview() {
    StillTheme {
        SearchScreen(
            state = SearchState(
                query = "",
                showRecents = true,
                recents = listOf(RecentItemUi("a", "Messages", false)),
                builtins = listOf(BuiltinItemUi("Notes", BuiltinTarget.NOTES)),
                results = listOf(
                    ResultItemUi("c", "Calculator", false, true, "C"),
                    ResultItemUi("i", "Instagram", true, true, "I")
                )
            ),
            onAction = {}
        )
    }
}
