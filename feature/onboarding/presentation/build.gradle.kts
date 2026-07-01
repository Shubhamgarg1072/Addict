plugins {
    alias(libs.plugins.addict.android.feature)
}

android {
    namespace = "com.time.applauncher.addict.feature.onboarding.presentation"
}

dependencies {
    implementation(project(":core:data"))
}
