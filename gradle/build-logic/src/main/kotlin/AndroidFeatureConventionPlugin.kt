import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.project

/**
 * Convention for a feature `presentation` module: an Android library with Compose,
 * Koin, type-safe navigation + serialization, and the shared core modules wired in.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("addict.android.library")
        pluginManager.apply("addict.android.compose")
        pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

        dependencies {
            add("implementation", project(":core:domain"))
            add("implementation", project(":core:presentation"))
            add("implementation", project(":core:design-system"))

            add("implementation", platform(libs.findLibrary("koin-bom").get()))
            add("implementation", libs.findLibrary("koin-android").get())
            add("implementation", libs.findLibrary("koin-androidx-compose").get())

            add("implementation", libs.findLibrary("androidx-navigation-compose").get())
            add("implementation", libs.findLibrary("kotlinx-serialization-json").get())
            add("implementation", libs.findLibrary("kotlinx-coroutines-core").get())

            add("testImplementation", libs.findLibrary("junit-jupiter").get())
            add("testRuntimeOnly", libs.findLibrary("junit-platform-launcher").get())
            add("testImplementation", libs.findLibrary("turbine").get())
            add("testImplementation", libs.findLibrary("assertk").get())
            add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())
        }

        tasks.withType(org.gradle.api.tasks.testing.Test::class.java).configureEach {
            useJUnitPlatform()
        }
    }
}
