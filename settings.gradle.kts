import io.github.diskria.projektor.common.licenses.MIT
import io.github.diskria.projektor.settings.extensions.configureMaven
import io.github.diskria.projektor.settings.extensions.dependencyRepositories
import io.github.diskria.projektor.settings.extensions.repositories

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

dependencyRepositories {
    maven("https://diskria.github.io/projektor")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("io.github.diskria.projektor.settings") version "3.+"
}

projekt {
    description = "Gradle plugin with reusable conventions and helpers for projects from my GitHub organizations."
    version = "3.1.1"
    license = MIT

    gradlePlugin()
    dependencyRepositories {
        configureMaven(
            name = "Minecraft",
            url = "https://libraries.minecraft.net"
        )
        configureMaven(
            name = "SpongePowered",
            url = "https://repo.spongepowered.org/repository/maven-public",
        )
        configureMaven(
            name = "Modrinth",
            url = "https://api.modrinth.com/maven",
            group = "maven.modrinth",
            includeSubgroups = false
        )
    }
    repositories {
        configureMaven(
            name = "Fabric",
            url = "https://maven.fabricmc.net"
        )
    }
}

include(":common", ":settings-plugin", ":project-plugin")
