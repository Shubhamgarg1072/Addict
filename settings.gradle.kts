pluginManagement {
    includeBuild("gradle/build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Addict"

include(":app")

include(":core:domain")
include(":core:data")
include(":core:presentation")
include(":core:design-system")

include(":feature:onboarding:presentation")
include(":feature:home:presentation")
include(":feature:wellbeing:presentation")

include(":feature:agenda:domain")
include(":feature:agenda:data")
include(":feature:agenda:presentation")

include(":feature:notes:domain")
include(":feature:notes:data")
include(":feature:notes:presentation")

include(":feature:settings:presentation")
