package com.time.applauncher.addict.feature.onboarding.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.time.applauncher.addict.feature.onboarding.presentation.onboarding.OnboardingRoot
import com.time.applauncher.addict.feature.onboarding.presentation.permission.PermissionRoot
import com.time.applauncher.addict.feature.onboarding.presentation.splash.SplashRoot

/**
 * @param onFinished cross-feature callback to enter the launcher (Home), provided by :app.
 */
fun NavGraphBuilder.onboardingGraph(
    navController: NavController,
    onFinished: () -> Unit
) {
    composable<SplashRoute> {
        SplashRoot(
            onGoHome = onFinished,
            onGoOnboarding = { navController.navigate(OnboardingRoute) }
        )
    }
    composable<OnboardingRoute> {
        OnboardingRoot(
            onGoPermission = { navController.navigate(PermissionRoute) }
        )
    }
    composable<PermissionRoute> {
        PermissionRoot(onDone = onFinished)
    }
}
