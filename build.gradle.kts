// Top-level build file for basic Kotlin library configuration
allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

// Basic tasks that work without Android SDK
task("hello") {
    doLast {
        println("Hello from Match'em project!")
        println("Available modules: ${subprojects.map { it.name }}")
    }
}

task("checkProject") {
    doLast {
        println("Project structure check:")
        println("Root project: ${project.name}")
        println("Build file exists: ${file("build.gradle.kts").exists()}")
        println("App module exists: ${file("app").exists()}")
        println("Source directory exists: ${file("app/src/main/java").exists()}")
    }
}

task("switchToAndroid") {
    doLast {
        println("To enable full Android development:")
        println("1. Ensure network access to Google repositories (dl.google.com)")
        println("2. Run: ./switch-config.sh android")
        println("3. Then: ./gradlew assembleDebug")
    }
}