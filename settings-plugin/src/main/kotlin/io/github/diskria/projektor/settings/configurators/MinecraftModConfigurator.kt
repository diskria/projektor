package io.github.diskria.projektor.settings.configurators

import io.github.diskria.gradle.utils.extensions.common.buildGradleProjectPath
import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.rootDirectory
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.listDirectories
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.common.minecraft.MinecraftConstants
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.common.minecraft.versions.mappingsType
import io.github.diskria.projektor.settings.configurations.MinecraftModConfiguration
import io.github.diskria.projektor.settings.configurators.common.SettingsConfigurator
import io.github.diskria.projektor.settings.configurators.common.dependencyRepositories
import io.github.diskria.projektor.settings.configurators.common.repositories
import io.github.diskria.projektor.settings.extensions.configureMaven
import io.ktor.http.*
import org.gradle.api.initialization.Settings

open class MinecraftModConfigurator(
    val config: MinecraftModConfiguration = MinecraftModConfiguration()
) : SettingsConfigurator() {

    override fun configureRepositories(settings: Settings) {
        applyMainRepositories(settings)
        applyExternalRepositories(settings)
    }

    override fun configureProjects(settings: Settings) = with(settings) {
        ModLoaderType.values().forEach { loader ->
            val loaderDirectoryName = loader.getName(`kebab-case`)
            val loaderDirectory = rootDirectory.resolve(loaderDirectoryName)
            val minecraftVersions = loaderDirectory.listDirectories().map {
                MinecraftVersion.parseOrNull(it.name)
                    ?: gradleError("Unknown Minecraft version directory: ${it.relativeTo(rootDirectory)}")
            }
            minecraftVersions.forEach { minecraftVersion ->
                val modProjectDirectoryName = minecraftVersion.asString()
                val modProjectDirectory = loaderDirectory.resolve(modProjectDirectoryName)
                val modProjectPath = buildGradleProjectPath(loaderDirectoryName, modProjectDirectoryName)
                include(modProjectPath)

                minecraftVersion.mappingsType.sides.forEach { side ->
                    val sideProjectDirectoryName = side.getName()
                    val sideProjectDirectory = modProjectDirectory.resolve(sideProjectDirectoryName)
                    if (sideProjectDirectory.exists()) {
                        val sideProjectPath = buildGradleProjectPath(modProjectPath, sideProjectDirectoryName)
                        include(sideProjectPath)
                    }
                }
            }
        }
    }

    companion object {
        fun applyMainRepositories(settings: Settings) = with(settings) {
            dependencyRepositories {
                configureMaven(
                    name = MinecraftConstants.FULL_GAME_NAME,
                    url = buildUrl("libraries.minecraft.net")
                )
                configureMaven(
                    name = "SpongePowered",
                    url = buildUrl("repo.spongepowered.org") {
                        path("repository", "maven-public")
                    }
                )
                configureMaven(
                    name = "Modrinth",
                    url = buildUrl("api.modrinth.com") {
                        path("maven")
                    },
                    group = "maven.modrinth",
                    includeSubgroups = false
                )
            }
        }

        fun applyExternalRepositories(settings: Settings) = with(settings) {
            repositories {
                configureMaven(
                    name = "Parchment",
                    url = buildUrl("maven.parchmentmc.org")
                )
                configureMaven(
                    name = ModLoaderType.FABRIC.displayName,
                    url = buildUrl("maven.fabricmc.net")
                )
                configureMaven(
                    name = ModLoaderType.LEGACY_FABRIC.displayName,
                    url = buildUrl("maven.legacyfabric.net")
                )
                configureMaven(
                    name = ModLoaderType.ORNITHE.displayName + "Releases",
                    url = buildUrl("maven.ornithemc.net") {
                        path("releases")
                    }
                )
                configureMaven(
                    name = ModLoaderType.ORNITHE.displayName + "Snapshots",
                    url = buildUrl("maven.ornithemc.net") {
                        path("snapshots")
                    }
                )
                configureMaven(
                    name = ModLoaderType.FORGE.displayName,
                    url = buildUrl("maven.minecraftforge.net")
                )
                configureMaven(
                    name = ModLoaderType.NEOFORGE.displayName,
                    url = buildUrl("maven.neoforged.net") {
                        path("releases")
                    }
                )
            }
        }
    }
}
