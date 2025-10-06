pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://diskria.github.io/projektor")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("io.github.diskria.projektor.settings") version "2.+"
}

projekt {
    description = "Gradle plugin with reusable conventions and helpers for projects from my GitHub organizations."
    version = "2.2.1"

    gradlePlugin()
    minecraftMod()
}

include(":project-plugin", ":settings-plugin")
