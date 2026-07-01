plugins {
    alias(libs.plugins.addict.android.feature)
}

android {
    namespace = "com.time.applauncher.addict.feature.notes.presentation"
}

dependencies {
    implementation(project(":feature:notes:domain"))
}
