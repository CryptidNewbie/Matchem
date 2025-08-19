// Enhanced Kotlin library configuration with dependencies for app functionality
plugins {
    `java-library`
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
    
    // Kotlinx libraries that work without Android
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    
    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.10")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
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

// Task to prepare for Android build (when network access is available)
task("prepareAndroid") {
    doLast {
        println("To enable full Android development:")
        println("1. Ensure network access to Google repositories (dl.google.com)")
        println("2. Run: ./switch-config.sh android")
        println("3. Then: ./gradlew assembleDebug")
        println("")
        println("Current configuration provides:")
        println("- ✅ Kotlin compilation")
        println("- ✅ Core app logic validation")
        println("- ✅ Unit testing capabilities")
        println("- ✅ Serialization support")
    }
}