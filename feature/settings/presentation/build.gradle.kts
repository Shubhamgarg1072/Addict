plugins {
    alias(libs.plugins.addict.android.feature)
}

android {
    namespace = "com.time.applauncher.addict.feature.settings.presentation"
}

dependencies {
    // Backup/restore reads and writes notes + agenda through their domain contracts.
    implementation(project(":feature:notes:domain"))
    implementation(project(":feature:agenda:domain"))
}
