package com.time.applauncher.addict.feature.settings.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.time.applauncher.addict.feature.settings.presentation.backup.BackupService
import com.time.applauncher.addict.feature.settings.presentation.backup.SafBackupService
import kotlinx.serialization.Serializable
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@Serializable
data object SettingsRoute

@Serializable
data object FavoritesRoute

@Serializable
data object DistractingAppsRoute

@Serializable
data object PrivacyRoute

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
            onNavigateFavorites = { navController.navigate(FavoritesRoute) },
            onNavigateDistracting = { navController.navigate(DistractingAppsRoute) },
            onNavigatePrivacy = { navController.navigate(PrivacyRoute) },
            onNavigateAbout = { navController.navigate(AboutRoute) }
        )
    }
    composable<FavoritesRoute> {
        FavoritesRoot(onBack = { navController.navigateUp() })
    }
    composable<DistractingAppsRoute> {
        DistractingAppsRoot(onBack = { navController.navigateUp() })
    }
    composable<PrivacyRoute> {
        PrivacyRoot(onBack = { navController.navigateUp() })
    }
    composable<AboutRoute> {
        AboutRoot(onBack = { navController.navigateUp() })
    }
}

val settingsPresentationModule = module {
    single<BackupService> { SafBackupService(androidContext(), get(), get(), get()) }
    viewModelOf(::SettingsViewModel)
    viewModelOf(::FavoritesViewModel)
    viewModelOf(::DistractingAppsViewModel)
}
