package io.github.diskria.projektor.settings.extensions.gradle

import io.github.diskria.gradle.utils.extensions.gradle.SettingsExtension
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.settings.RepositoriesFilterType
import io.github.diskria.projektor.settings.extensions.kotlin.configureMaven
import io.github.diskria.projektor.settings.extensions.kotlin.configureRepositories
import io.github.diskria.projektor.settings.minecraft.ModLoader
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class ProjektExtension @Inject constructor(objects: ObjectFactory) : SettingsExtension() {

    val name: Property<String> = objects.property(String::class.java)
    val description: Property<String> = objects.property(String::class.java)
    val version: Property<String> = objects.property(String::class.java)

    private var isCommonConfigurationApplied: Boolean = false

    fun gradlePlugin() = script {
        applyCommonConfiguration()
        configureRepositories(RepositoriesFilterType.DEPENDENCIES) {
            gradlePluginPortal()
        }
    }

    fun kotlinLibrary() = script {
        applyCommonConfiguration()
    }

    fun minecraftMod() = script {
        applyCommonConfiguration()
        configureRepositories(RepositoriesFilterType.DEPENDENCIES) {
            configureMaven(
                name = "Minecraft",
                url = "https://libraries.minecraft.net"
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
        if (!isProjektor()) {
            include(":common")

            ModLoader.entries.forEach { modLoader ->
                val directoryName = modLoader.getName()
                val modLoaderDirectory = rootDir.resolve(directoryName)
                if (modLoaderDirectory.isDirectory) {
                    modLoaderDirectory.listFiles()?.filter { it.isDirectory }?.forEach { versionDirectory ->
                        include(":$directoryName:${versionDirectory.name}")
                    }
                }
            }
        }
    }

    fun androidLibrary() = script {
        applyCommonConfiguration()
        configureRepositories {
            google()
        }
    }

    fun androidApplication() = script {
        androidLibrary()
    }

    private fun applyCommonConfiguration() = script {
        if (isCommonConfigurationApplied) {
            return@script
        }

        val projektName = requireProperty(name, ::name.name)
        val projektDescription = requireProperty(description, ::description.name)
        val projektVersion = requireProperty(version, ::version.name)

        rootProject.name = projektName
        gradle.rootProject {
            description = projektDescription
            version = projektVersion
        }

        configureRepositories {
            mavenCentral()
        }
        configureRepositories(RepositoriesFilterType.PLUGINS) {
            gradlePluginPortal()
        }
        isCommonConfigurationApplied = true
    }

    private fun isProjektor(): Boolean = script {
        rootProject.name == "Projektor" || rootProject.name == "Projektor Settings"
    }
}
