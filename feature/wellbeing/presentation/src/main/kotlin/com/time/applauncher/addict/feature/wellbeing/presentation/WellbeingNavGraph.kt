package com.time.applauncher.addict.feature.wellbeing.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.time.applauncher.addict.feature.wellbeing.presentation.dashboard.DashboardRoot
import com.time.applauncher.addict.feature.wellbeing.presentation.dashboard.DashboardViewModel
import com.time.applauncher.addict.feature.wellbeing.presentation.goal.GoalRoot
import com.time.applauncher.addict.feature.wellbeing.presentation.goal.GoalViewModel
import kotlinx.serialization.Serializable
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@Serializable
data object DashboardRoute

@Serializable
data object GoalRoute

fun NavGraphBuilder.wellbeingGraph(onBack: () -> Unit) {
    composable<DashboardRoute> {
        DashboardRoot(onBack = onBack)
    }
    composable<GoalRoute> {
        GoalRoot(onBack = onBack)
    }
}

val wellbeingPresentationModule = module {
    viewModelOf(::DashboardViewModel)
    viewModelOf(::GoalViewModel)
}
