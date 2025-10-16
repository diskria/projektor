import io.github.diskria.projektor.common.licenses.MIT
import io.github.diskria.projektor.settings.configurators.MinecraftModConfigurator

pluginManagement {
    repositories {
        maven("https://diskria.github.io/projektor")
        gradlePluginPortal()
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "3.+"
}

projekt {
    description = "Gradle plugin with reusable conventions and helpers for projects from my GitHub organizations."
    version = "3.4.7"
    license = MIT
    tags = setOf("configuration")

    gradlePlugin()
    MinecraftModConfigurator.applyRepositories(settings)
}

include(":common", ":settings-plugin", ":project-plugin")
