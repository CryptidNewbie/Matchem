// Temporary simplified configuration until Android SDK access is available
plugins {
    `java-library`
    kotlin("jvm") version "1.9.10"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
}

// Custom task to verify project structure
task("verify") {
    doLast {
        println("Verifying app module structure...")
        val srcDir = file("src/main/java")
        println("Source directory exists: ${srcDir.exists()}")
        if (srcDir.exists()) {
            fileTree(srcDir).forEach { file ->
                if (file.extension == "kt") {
                    println("Found Kotlin file: ${file.relativeTo(srcDir)}")
                }
            }
        }
    }
}