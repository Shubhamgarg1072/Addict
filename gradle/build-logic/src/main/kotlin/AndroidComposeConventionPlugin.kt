import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Enables Jetpack Compose on whichever Android extension (application or library)
 * is already applied to the module.
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

        extensions.findByType(ApplicationExtension::class.java)?.apply {
            buildFeatures.compose = true
        }
        extensions.findByType(LibraryExtension::class.java)?.apply {
            buildFeatures.compose = true
        }

        addComposeDependencies()

        dependencies {
            add("implementation", libs.findLibrary("androidx-compose-ui-text-google-fonts").get())
        }
    }
}
