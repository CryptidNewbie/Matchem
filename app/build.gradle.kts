plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    // NEW: apply the Kotlin Compose plugin
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.cryptidnewbie.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cryptidnewbie.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Debug config if needed
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xjdk-release=17")
    }

    buildFeatures {
        compose = true
    }
    // REMOVED: composeOptions { kotlinCompilerExtensionVersion = "..." }
    // The Compose compiler is provided by the Kotlin Compose plugin above.

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM controls versions of Compose artifacts (UI/runtime). Keep this current.
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))

    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")

    debugImplementation("androidx.compose.ui:ui-tooling")

    // KotlinX
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Unit tests
    testImplementation("junit:junit:4.13.2")

    // Android tests
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}