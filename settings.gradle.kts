pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SmartPantry"

include(
    ":app",
    ":core:model",
    ":core:domain",
    ":core:database",
    ":core:data",
    ":core:ui",
    ":feature:auth",
    ":feature:inventory",
    ":feature:assistant",
    ":feature:analytics",
    ":feature:profile",
)
