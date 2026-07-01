import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    alias(libs.plugins.addict.kotlin.library)
}

val catalog = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
fun lib(alias: String) = catalog.findLibrary(alias).get()

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":feature:agenda:domain"))
    implementation(platform(lib("koin-bom")))
    implementation(lib("koin-core"))
}
