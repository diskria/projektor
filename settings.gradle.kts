pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://diskria.github.io/projektor")
    }
    includeBuild("test/gradle-plugin")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("io.github.diskria.projektor.settings") version "2.+"
}

projekt {
    name = "Projektor"
    description = "Gradle plugin with reusable conventions and helpers for projects from my GitHub organizations."
    version = "2.0.5"

    gradlePlugin()
    minecraftMod()
}

include(":settings-plugin", ":project-plugin")
