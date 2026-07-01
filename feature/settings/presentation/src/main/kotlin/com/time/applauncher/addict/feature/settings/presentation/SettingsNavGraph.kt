package com.time.applauncher.addict.feature.settings.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@Serializable
data object SettingsRoute

@Serializable
data object AboutRoute

/**
 * @param onNavigateGoal / onNavigateFocus cross-feature callbacks provided by :app.
 */
fun NavGraphBuilder.settingsGraph(
    navController: NavController,
    onBack: () -> Unit,
    onNavigateGoal: () -> Unit,
    onNavigateFocus: () -> Unit
) {
    composable<SettingsRoute> {
        SettingsRoot(
            onBack = onBack,
            onNavigateGoal = onNavigateGoal,
            onNavigateFocus = onNavigateFocus,
            onNavigateAbout = { navController.navigate(AboutRoute) }
        )
    }
    composable<AboutRoute> {
        AboutRoot(onBack = { navController.navigateUp() })
    }
}

val settingsPresentationModule = module {
    viewModelOf(::SettingsViewModel)
}
