package com.time.applauncher.addict.core.domain.model

enum class ThemeMode { AMOLED, DARK, LIGHT }

data class UserSettings(
    val theme: ThemeMode = ThemeMode.AMOLED,
    val use24h: Boolean = false,
    val showQuote: Boolean = true,
    val colorfulIcons: Boolean = false,
    val highContrast: Boolean = false,
    val goalMinutes: Int = 150,
    val launchDelaySeconds: Int = 5,
    val onboardingComplete: Boolean = false,
    val favorites: List<String> = emptyList(),
    val distractingPackages: Set<String> = emptySet()
)
