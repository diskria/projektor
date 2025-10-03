package io.github.diskria.projektor.settings.extensions.gradle

import io.github.diskria.gradle.utils.extensions.gradle.SettingsExtension
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.settings.extensions.kotlin.configureMaven
import io.github.diskria.projektor.settings.extensions.kotlin.dependencyRepositories
import io.github.diskria.projektor.settings.extensions.kotlin.pluginRepositories
import io.github.diskria.projektor.settings.extensions.kotlin.repositories
import io.github.diskria.projektor.settings.minecraft.ModLoader
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

open class ProjektExtension @Inject constructor(objects: ObjectFactory) : SettingsExtension() {

    val name: Property<String> = objects.property(String::class.java)
    val description: Property<String> = objects.property(String::class.java)
    val version: Property<String> = objects.property(String::class.java)
    val versionCatalog: Property<ConfigurableFileCollection> = objects.property(ConfigurableFileCollection::class.java)

    private var isCommonConfigurationApplied: Boolean = false

    fun gradlePlugin() = script {
        applyCommonConfiguration()
        dependencyRepositories {
            gradlePluginPortal()
        }
    }

    fun kotlinLibrary() = script {
        applyCommonConfiguration()
    }

    fun androidLibrary() = script {
        applyCommonConfiguration()
        repositories {
            google()
        }
    }

    fun androidApplication() = script {
        androidLibrary()
    }

    fun minecraftMod() = script {
        applyCommonConfiguration()
        dependencyRepositories {
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
        repositories {
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

        repositories {
            mavenCentral()
        }
        pluginRepositories {
            gradlePluginPortal()
        }
        versionCatalog.orNull?.let { configurableFileCollection ->
            dependencyResolutionManagement {
                versionCatalogs {
                    create("libs") {
                        from(configurableFileCollection)
                    }
                }
            }
        }
        isCommonConfigurationApplied = true
    }

    private fun isProjektor(): Boolean = script {
        rootProject.name == "Projektor"
    }
}
