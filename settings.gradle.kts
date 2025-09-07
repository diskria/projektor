plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

apply(from = "gradle/settings/common.settings.gradle.kts")

fun RepositoryHandler.mavenGradlePluginPortal() {
    maven("https://plugins.gradle.org/m2")
}

fun RepositoryHandler.mavenFabric() {
    maven("https://maven.fabricmc.net")
}

fun RepositoryHandler.mavenMinecraftLibraries() {
    maven("https://libraries.minecraft.net")
}

@Suppress("UnstableApiUsage")
fun RepositoryHandler.mavenSpongePowered() {
    exclusiveContent {
        forRepository {
            maven("https://repo.spongepowered.org/repository/maven-public")
        }
        filter {
            includeGroupAndSubgroups("org.spongepowered")
        }
    }
}

fun RepositoryHandler.commonRepositories() {
    mavenCentral()
    mavenGradlePluginPortal()
    mavenFabric()
    mavenMinecraftLibraries()
    mavenSpongePowered()
}

fun RepositoryHandler.pluginRepositories() {
    gradlePluginPortal()
}

@Suppress("UnstableApiUsage")
fun setupRepositories() {
    dependencyResolutionManagement.repositories {
        commonRepositories()
    }

    pluginManagement.repositories {
        commonRepositories()
        pluginRepositories()
    }
}

setupRepositories()

include(":build-plugin")
