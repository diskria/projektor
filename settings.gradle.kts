import io.github.diskria.projektor.RepositoriesFilterType
import io.github.diskria.projektor.extensions.kotlin.configureMaven
import io.github.diskria.projektor.extensions.kotlin.configureRepositories

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://diskria.github.io/projektor")
    }
}

plugins {
    id("io.github.diskria.projektor.settings") version "2.+"
}

projekt {
    name = "Projektor"
    description = "Gradle plugin with reusable conventions and helpers for projects from my GitHub organizations."
    version = "2.0.1"

    gradlePlugin()

    configureRepositories(RepositoriesFilterType.DEPENDENCIES) {
        configureMaven(
            name = "Minecraft",
            url = "https://libraries.minecraft.net",
            group = "com.mojang"
        )
        configureMaven(
            name = "SpongePowered",
            url = "https://repo.spongepowered.org/repository/maven-public",
            group = "org.spongepowered"
        )
        configureMaven(
            name = "Modrinth",
            url = "https://api.modrinth.com/maven",
            group = "maven.modrinth",
            includeSubgroups = false
        )
    }
    configureRepositories {
        configureMaven(
            name = "Fabric",
            url = "https://maven.fabricmc.net"
        )
    }
}

include(":settings-plugin", ":project-plugin")
