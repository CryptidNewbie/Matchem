pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
        gradlePluginPortal()
        // Try to use a mirror or alternative
        maven { url = uri("https://repo1.maven.org/maven2") }
        maven { url = uri("https://jcenter.bintray.com") }
    }
}

rootProject.name = "Match'em "
include(":app")
 