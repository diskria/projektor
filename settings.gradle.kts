import io.github.diskria.projektor.settings.configurators.MinecraftModConfigurator
import io.github.diskria.projektor.settings.extensions.configureMaven
import io.github.diskria.projektor.settings.extensions.configureRepositories
import io.github.diskria.projektor.settings.licenses.MIT
import io.github.diskria.projektor.settings.repositories.DependencyRepositories

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://diskria.github.io/projektor")
    }
    val isTestsForceDisabled = false
    if (!isTestsForceDisabled && rootDir.resolve("build/localMaven").exists()) {
        rootDir.resolve("test").listFiles()?.filter { it.isDirectory }?.forEach { testProjectDirectory ->
            includeBuild("test/${testProjectDirectory.name}")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("io.github.diskria.projektor.settings") version "3.+"
}

projekt {
    description = "Gradle plugin with reusable conventions and helpers for projects from my GitHub organizations."
    version = "3.0.3"
    license = MIT

    gradlePlugin()
    MinecraftModConfigurator.applyRepositories(settings)
}

include(":project-plugin", ":settings-plugin")
