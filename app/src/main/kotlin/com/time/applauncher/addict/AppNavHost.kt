package com.time.applauncher.addict

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.time.applauncher.addict.feature.agenda.presentation.AgendaRoute
import com.time.applauncher.addict.feature.agenda.presentation.agendaGraph
import com.time.applauncher.addict.feature.home.presentation.FocusSetupRoute
import com.time.applauncher.addict.feature.home.presentation.HomeRoute
import com.time.applauncher.addict.feature.home.presentation.homeGraph
import com.time.applauncher.addict.feature.notes.presentation.NotesRoute
import com.time.applauncher.addict.feature.notes.presentation.notesGraph
import com.time.applauncher.addict.feature.onboarding.presentation.SplashRoute
import com.time.applauncher.addict.feature.onboarding.presentation.onboardingGraph
import com.time.applauncher.addict.feature.settings.presentation.SettingsRoute
import com.time.applauncher.addict.feature.settings.presentation.settingsGraph
import com.time.applauncher.addict.feature.wellbeing.presentation.DashboardRoute
import com.time.applauncher.addict.feature.wellbeing.presentation.GoalRoute
import com.time.applauncher.addict.feature.wellbeing.presentation.wellbeingGraph

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = SplashRoute) {
        onboardingGraph(
            navController = navController,
            onFinished = {
                navController.navigate(HomeRoute) {
                    popUpTo(SplashRoute) { inclusive = true }
                }
            }
        )
        homeGraph(
            navController = navController,
            onNavigateToDashboard = { navController.navigate(DashboardRoute) },
            onNavigateToAgenda = { navController.navigate(AgendaRoute) },
            onNavigateToGoal = { navController.navigate(GoalRoute) },
            onNavigateToNotes = { navController.navigate(NotesRoute) },
            onNavigateToSettings = { navController.navigate(SettingsRoute) }
        )
        wellbeingGraph(onBack = { navController.navigateUp() })
        agendaGraph(onBack = { navController.navigateUp() })
        notesGraph(onBack = { navController.navigateUp() })
        settingsGraph(
            navController = navController,
            onBack = { navController.navigateUp() },
            onNavigateGoal = { navController.navigate(GoalRoute) },
            onNavigateFocus = { navController.navigate(FocusSetupRoute) }
        )
    }
}
