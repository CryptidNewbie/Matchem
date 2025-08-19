// Root build.gradle.kts can be minimal with Kotlin DSL.
// All plugin versions are managed in settings.gradle.kts.
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}