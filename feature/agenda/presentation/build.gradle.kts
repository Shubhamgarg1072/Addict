plugins {
    alias(libs.plugins.addict.android.feature)
}

android {
    namespace = "com.time.applauncher.addict.feature.agenda.presentation"
}

dependencies {
    implementation(project(":feature:agenda:domain"))
}
