pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // Android Gradle Plugin and Kotlin versions
        id("com.android.application") version "8.12.1"
        kotlin("android") version "2.0.0"
        kotlin("plugin.serialization") version "2.0.0"
        // NEW: Kotlin Compose compiler plugin (required with Kotlin 2.0 when Compose is enabled)
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Matchem"
include(":app")