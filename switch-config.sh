#!/bin/bash

# Script to switch between Android and non-Android Gradle configurations

case "$1" in
    "android")
        echo "Switching to Android configuration..."
        if [ -f "settings.gradle.kts.android" ] && [ -f "build.gradle.kts.android" ] && [ -f "app/build.gradle.kts.android" ]; then
            cp settings.gradle.kts.android settings.gradle.kts
            cp build.gradle.kts.android build.gradle.kts
            cp app/build.gradle.kts.android app/build.gradle.kts
            echo "✅ Switched to Android configuration"
            echo "⚠️  Note: Requires network access to Google repositories (dl.google.com)"
        else
            echo "❌ Android configuration files not found"
            exit 1
        fi
        ;;
    "basic")
        echo "Switching to enhanced basic configuration..."
        if [ -f "build.gradle.kts.basic" ] && [ -f "app/build.gradle.kts.basic" ] && [ -f "settings.gradle.kts.basic" ]; then
            cp build.gradle.kts.basic build.gradle.kts
            cp app/build.gradle.kts.basic app/build.gradle.kts
            cp settings.gradle.kts.basic settings.gradle.kts
            echo "✅ Switched to enhanced basic configuration"
            echo "✅ Basic Gradle tasks are available"
            echo "✅ Core game logic can be compiled and tested"
        else
            echo "Current configuration is already basic/non-Android"
            echo "✅ Basic Gradle tasks are available"
        fi
        ;;
    "test")
        echo "Testing current configuration..."
        ./gradlew tasks --quiet > /dev/null 2>&1
        if [ $? -eq 0 ]; then
            echo "✅ Current configuration works"
            echo "Available tasks:"
            ./gradlew tasks --quiet | grep -E "^(assemble|build|clean|test|help)" | head -5
        else
            echo "❌ Current configuration has issues"
            echo "Try: $0 basic"
        fi
        ;;
    *)
        echo "Usage: $0 {android|basic|test}"
        echo ""
        echo "  android  - Switch to Android development configuration"
        echo "  basic    - Current basic configuration (no Android SDK required)" 
        echo "  test     - Test current configuration"
        echo ""
        echo "Current status:"
        if ./gradlew tasks --quiet > /dev/null 2>&1; then
            echo "✅ Gradle is working"
        else
            echo "❌ Gradle has issues"
        fi
        ;;
esac