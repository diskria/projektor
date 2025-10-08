import io.github.diskria.projektor.settings.licenses.MIT
import io.github.diskria.projektor.settings.projekt.MinecraftMod

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://diskria.github.io/projektor")
    }
    val isTestsForceDisabled = true
    if (!isTestsForceDisabled && rootDir.resolve("build/localMaven").exists()) {
        rootDir.resolve("test").listFiles()?.filter { it.isDirectory }?.forEach { testProjectDirectory ->
            includeBuild("test/${testProjectDirectory.name}")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("io.github.diskria.projektor.settings") version "2.+"
}

projekt {
    description = "Gradle plugin with reusable conventions and helpers for projects from my GitHub organizations."
    version = "3.0.0"
    license = MIT

    gradlePlugin()
}

MinecraftMod.applyRepositories(settings)

include(":project-plugin", ":settings-plugin")
