plugins {
    alias(libs.plugins.addict.android.library)
    alias(libs.plugins.addict.android.compose)
}

android {
    namespace = "com.time.applauncher.addict.core.presentation"
}

dependencies {
    implementation(project(":core:domain"))
    // Compose + coroutines are provided by the convention plugins.
}
