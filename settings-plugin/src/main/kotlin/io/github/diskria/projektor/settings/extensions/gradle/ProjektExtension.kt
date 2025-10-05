package io.github.diskria.projektor.settings.extensions.gradle

import io.github.diskria.gradle.utils.extensions.gradle.SettingsExtension
import io.github.diskria.gradle.utils.extensions.kotlin.common.gradleError
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPrefix
import io.github.diskria.kotlin.utils.extensions.asDirectory
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.common.modifyIf
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.setCase
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
                val modLoaderName = modLoader.getName()
                val modLoaderDirectory = rootDir.resolve(modLoaderName)
                if (modLoaderDirectory.isDirectory) {
                    modLoaderDirectory.listFiles()?.filter { it.isDirectory }?.forEach { versionDirectory ->
                        include(":$modLoaderName:${versionDirectory.name}")
                    }
                }
            }
        }
    }

    private fun applyCommonConfiguration() = script {
        if (isCommonConfigurationApplied) {
            return@script
        }
        val (owner, repo) = if (providers.environmentVariable("CI").isPresent) {
            val githubOwner = providers.environmentVariable("GITHUB_OWNER").orNull ?: gradleError(
                "Environment variable GITHUB_OWNER must be set"
            )
            val githubRepo = providers.environmentVariable("GITHUB_REPO").orNull ?: gradleError(
                "Environment variable GITHUB_REPO must be set"
            )
            githubOwner to githubRepo
        } else {
            rootDir.parentFile.asDirectory().name to rootDir.name
        }
        rootProject.name = repo
            .setCase(`kebab-case`, `Title Case`)
            .modifyIf(owner.first().isUpperCase()) {
                it.appendPrefix(owner + Constants.Char.SPACE)
            }
        gradle.rootProject {
            description = requireProperty(this@ProjektExtension.description, this@ProjektExtension::description.name)
            version = requireProperty(this@ProjektExtension.version, this@ProjektExtension::version.name)
        }
        repositories {
            mavenCentral()
        }
        pluginRepositories {
            gradlePluginPortal()
        }
        versionCatalog.orNull?.let { files ->
            dependencyResolutionManagement {
                versionCatalogs {
                    create("libs") {
                        from(files)
                    }
                }
            }
        }
        isCommonConfigurationApplied = true
    }

    private fun isProjektor(): Boolean = script {
        true // TODO replace with root project name check after 2.1 release
    }
}
