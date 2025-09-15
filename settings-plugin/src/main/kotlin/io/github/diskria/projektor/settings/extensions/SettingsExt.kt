package io.github.diskria.projektor.settings.extensions

import io.github.diskria.projektor.settings.minecraft.ModLoader
import io.github.diskria.projektor.settings.minecraft.logicalName
import io.github.diskria.projektor.settings.properties.toAutoNamedGradleProperty
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.initialization.Settings
import org.gradle.kotlin.dsl.maven

fun Settings.configureProject() {
    val projectName by providers.toAutoNamedGradleProperty()
    rootProject.name = projectName
    setupRepositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://diskria.github.io/projektor") {
            name = "Projektor"
        }
    }
}

fun Settings.configureMinecraftMod() {
    setupRepositories {
        maven("https://libraries.minecraft.net") {
            name = "MinecraftLibraries"
        }
        maven("https://maven.fabricmc.net") {
            name = "Fabric"
        }
        setupExclusiveContent(
            maven("https://repo.spongepowered.org/repository/maven-public") {
                name = "SpongePowered"
            },
            "org.spongepowered",
            isSubgroupsAllowed = true
        )
        setupExclusiveContent(
            maven("https://api.modrinth.com/maven") {
                name = "Modrinth"
            },
            "maven.modrinth"
        )
    }
    if (!isProjektorProject()) {
        include(":common")

        ModLoader.entries.forEach { modLoader ->
            val modLoaderDirectory = rootDir.resolve(modLoader.logicalName())
            if (modLoaderDirectory.isDirectory) {
                modLoaderDirectory.listFiles()?.filter { it.isDirectory }?.forEach { versionDirectory ->
                    include(":$modLoader:${versionDirectory.name}")
                }
            }
        }
    }
}

fun Settings.configureAndroidApp() {
    setupRepositories {
        google()
    }
}

private fun Settings.isProjektorProject(): Boolean =
    rootProject.name == "Projektor"

private fun Settings.setupRepositories(repositories: RepositoryHandler.() -> Unit) {
    dependencyResolutionManagement {
        @Suppress("UnstableApiUsage")
        repositories {
            repositories()
        }
    }

    pluginManagement {
        repositories {
            repositories()
        }
    }
}

private fun RepositoryHandler.setupExclusiveContent(
    maven: MavenArtifactRepository,
    groupFilter: String,
    isSubgroupsAllowed: Boolean = false,
) {
    exclusiveContent {
        forRepository {
            maven
        }
        filter {
            if (isSubgroupsAllowed) {
                @Suppress("UnstableApiUsage")
                includeGroupAndSubgroups(groupFilter)
            } else {
                includeGroup(groupFilter)
            }
        }
    }
}
