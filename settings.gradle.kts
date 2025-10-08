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
    version = "3.0.0"
    license = MIT

    gradlePlugin()
}

configureRepositories(DependencyRepositories) {
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
configureRepositories {
    configureMaven(
        name = "Fabric",
        url = "https://maven.fabricmc.net"
    )
}

include(":project-plugin", ":settings-plugin")
