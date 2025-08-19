// Top-level build file where you can add configuration options common to all sub-projects/modules.
allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

// Basic tasks that would be available without Android SDK
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