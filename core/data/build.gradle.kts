import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    alias(libs.plugins.addict.android.library)
}

android {
    namespace = "com.time.applauncher.addict.core.data"
}

// NOTE: type-safe `libs.*` accessors are unavailable in the body of scripts that
// apply an included-build convention plugin, so look libraries up at runtime.
val catalog = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
fun lib(alias: String) = catalog.findLibrary(alias).get()

dependencies {
    implementation(project(":core:domain"))

    implementation(platform(lib("koin-bom")))
    implementation(lib("koin-android"))

    implementation(lib("androidx-datastore-preferences"))
    implementation(lib("kermit"))
}
