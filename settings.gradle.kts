import io.github.diskria.projektor.common.licenses.MIT
import io.github.diskria.projektor.settings.configurators.MinecraftModConfigurator
import io.github.diskria.projektor.settings.extensions.dependencyRepositories

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://diskria.github.io/projektor")
    }

    val shouldIncludeTestProjects = false
    if (shouldIncludeTestProjects && rootDir.resolve("build/localMaven").exists()) {
        val testProjectsRoot = rootDir.resolve("test")
        testProjectsRoot.listFiles()?.filter { it.isDirectory }?.forEach { testProjectDirectory ->
            includeBuild(testProjectsRoot.resolve(testProjectDirectory.name))
        }
    }
}

dependencyRepositories {
    maven("https://diskria.github.io/projektor")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("io.github.diskria.projektor.settings") version "3.+"
}

projekt {
    description = "Gradle plugin with reusable conventions and helpers for projects from my GitHub organizations."
    version = "3.2.0"
    license = MIT
    tags = setOf("configuration")

    gradlePlugin()
    MinecraftModConfigurator.applyRepositories(settings)
}

include(":common", ":settings-plugin", ":project-plugin")
