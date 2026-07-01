package com.time.applauncher.addict.feature.home.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.time.applauncher.addict.feature.home.presentation.focus.FocusActiveRoot
import com.time.applauncher.addict.feature.home.presentation.focus.FocusActiveViewModel
import com.time.applauncher.addict.feature.home.presentation.focus.FocusSetupRoot
import com.time.applauncher.addict.feature.home.presentation.gate.GateRoot
import com.time.applauncher.addict.feature.home.presentation.gate.GateViewModel
import com.time.applauncher.addict.feature.home.presentation.home.HomeRoot
import com.time.applauncher.addict.feature.home.presentation.home.HomeViewModel
import com.time.applauncher.addict.feature.home.presentation.search.SearchRoot
import com.time.applauncher.addict.feature.home.presentation.search.SearchViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@Serializable
data object HomeRoute

@Serializable
data object SearchRoute

@Serializable
data class GateRoute(val packageName: String, val appLabel: String)

@Serializable
data object FocusSetupRoute

@Serializable
data class FocusActiveRoute(val minutes: Int)

/**
 * The launcher surface. Cross-feature destinations are provided as callbacks by :app.
 */
fun NavGraphBuilder.homeGraph(
    navController: NavController,
    onNavigateToDashboard: () -> Unit,
    onNavigateToAgenda: () -> Unit,
    onNavigateToGoal: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    composable<HomeRoute> {
        HomeRoot(
            onNavigateToDashboard = onNavigateToDashboard,
            onNavigateToAgenda = onNavigateToAgenda,
            onNavigateToGoal = onNavigateToGoal,
            onNavigateToSearch = { navController.navigate(SearchRoute) },
            onNavigateToGate = { pkg, label -> navController.navigate(GateRoute(pkg, label)) }
        )
    }
    composable<SearchRoute> {
        SearchRoot(
            onBack = { navController.navigateUp() },
            onNavigateToGate = { pkg, label -> navController.navigate(GateRoute(pkg, label)) },
            onNavigateToNotes = onNavigateToNotes,
            onNavigateToAgenda = onNavigateToAgenda,
            onNavigateToDashboard = onNavigateToDashboard,
            onNavigateToSettings = onNavigateToSettings
        )
    }
    composable<GateRoute> { entry ->
        val route = entry.toRoute<GateRoute>()
        GateRoot(
            packageName = route.packageName,
            appLabel = route.appLabel,
            onGoHome = { navController.popBackStack(HomeRoute, inclusive = false) }
        )
    }
    composable<FocusSetupRoute> {
        FocusSetupRoot(
            onBack = { navController.navigateUp() },
            onSelectDuration = { minutes -> navController.navigate(FocusActiveRoute(minutes)) }
        )
    }
    composable<FocusActiveRoute> { entry ->
        val route = entry.toRoute<FocusActiveRoute>()
        FocusActiveRoot(
            minutes = route.minutes,
            onGoHome = { navController.popBackStack(HomeRoute, inclusive = false) }
        )
    }
}

val homePresentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchViewModel)
    viewModel { (packageName: String, appLabel: String) ->
        GateViewModel(packageName, appLabel, get(), get(), get())
    }
    viewModel { (minutes: Int) ->
        FocusActiveViewModel(minutes)
    }
}
