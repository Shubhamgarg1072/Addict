plugins {
    alias(libs.plugins.addict.kotlin.library)
}

dependencies {
    implementation(project(":core:domain"))
}
