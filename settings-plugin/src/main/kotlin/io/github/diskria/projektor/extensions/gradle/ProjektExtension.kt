package io.github.diskria.projektor.extensions.gradle

import io.github.diskria.gradle.utils.extensions.gradle.SettingsExtension
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.RepositoriesFilterType
import io.github.diskria.projektor.extensions.kotlin.configureMaven
import io.github.diskria.projektor.extensions.kotlin.configureRepositories
import io.github.diskria.projektor.minecraft.ModLoader
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class ProjektExtension @Inject constructor(objects: ObjectFactory) : SettingsExtension() {

    val name: Property<String> = objects.property(String::class.java)
    val description: Property<String> = objects.property(String::class.java)
    val version: Property<String> = objects.property(String::class.java)

    private var isProjectConfigured: Boolean = false

    fun gradlePlugin() = script {
        configureProject()
        configureRepositories(RepositoriesFilterType.DEPENDENCIES) {
            gradlePluginPortal()
        }
    }

    fun kotlinLibrary() = script {
        configureProject()
    }

    fun minecraftMod() = script {
        configureProject()
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
                url = "https://maven.fabricmc.net",
                group = "net.fabricmc"
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
        configureProject()
        configureRepositories {
            google()
        }
    }

    fun androidApplication() = script {
        androidLibrary()
    }

    private fun configureProject() = script {
        if (isProjectConfigured) {
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
        isProjectConfigured = true
    }

    private fun isProjektor(): Boolean = script {
        rootProject.name == "Projektor"
    }
}
