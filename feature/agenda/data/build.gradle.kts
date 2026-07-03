import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    alias(libs.plugins.addict.android.library)
    alias(libs.plugins.addict.room)
}

android {
    namespace = "com.time.applauncher.addict.feature.agenda.data"
}

val catalog = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
fun lib(alias: String) = catalog.findLibrary(alias).get()

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":feature:agenda:domain"))

    implementation(platform(lib("koin-bom")))
    implementation(lib("koin-android"))
}
