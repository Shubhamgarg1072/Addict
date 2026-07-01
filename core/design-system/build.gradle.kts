plugins {
    alias(libs.plugins.addict.android.library)
    alias(libs.plugins.addict.android.compose)
}

android {
    namespace = "com.time.applauncher.addict.core.designsystem"
}

dependencies {
    implementation(project(":core:domain"))
    // Compose + Google Fonts are provided by the addict.android.compose convention plugin.
}
