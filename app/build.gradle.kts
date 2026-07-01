import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    alias(libs.plugins.addict.android.application)
    alias(libs.plugins.addict.android.compose)
}

android {
    namespace = "com.time.applauncher.addict"

    defaultConfig {
        applicationId = "com.time.applauncher.addict"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

val catalog = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
fun lib(alias: String) = catalog.findLibrary(alias).get()

dependencies {
    // Core
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:presentation"))
    implementation(project(":core:design-system"))

    // Features
    implementation(project(":feature:onboarding:presentation"))
    implementation(project(":feature:home:presentation"))
    implementation(project(":feature:wellbeing:presentation"))
    implementation(project(":feature:agenda:domain"))
    implementation(project(":feature:agenda:data"))
    implementation(project(":feature:agenda:presentation"))
    implementation(project(":feature:notes:domain"))
    implementation(project(":feature:notes:data"))
    implementation(project(":feature:notes:presentation"))
    implementation(project(":feature:settings:presentation"))

    // DI
    implementation(platform(lib("koin-bom")))
    implementation(lib("koin-android"))
    implementation(lib("koin-androidx-compose"))

    // Navigation
    implementation(lib("androidx-navigation-compose"))
    implementation(lib("kotlinx-serialization-json"))

    implementation(lib("androidx-core-ktx"))
}
