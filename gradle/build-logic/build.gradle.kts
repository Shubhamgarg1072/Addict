plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    // Hardcoded coordinates: build-logic does not declare its own "libs" catalog,
    // so the main build's auto-generated `libs` body accessors are not shadowed.
    compileOnly("com.android.tools.build:gradle:9.2.1")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.10")
    compileOnly("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.2.10-2.0.2")
    compileOnly("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.2.10")
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "addict.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "addict.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "addict.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "addict.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "addict.kotlin.library"
            implementationClass = "KotlinLibraryConventionPlugin"
        }
        register("room") {
            id = "addict.room"
            implementationClass = "RoomConventionPlugin"
        }
    }
}
