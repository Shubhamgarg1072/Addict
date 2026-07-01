package com.time.applauncher.addict.feature.onboarding.presentation.di

import com.time.applauncher.addict.feature.onboarding.presentation.onboarding.OnboardingViewModel
import com.time.applauncher.addict.feature.onboarding.presentation.permission.PermissionViewModel
import com.time.applauncher.addict.feature.onboarding.presentation.splash.SplashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val onboardingPresentationModule = module {
    viewModelOf(::SplashViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::PermissionViewModel)
}
