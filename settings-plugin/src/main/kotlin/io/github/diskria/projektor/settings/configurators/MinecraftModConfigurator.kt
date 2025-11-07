package io.github.diskria.projektor.settings.configurators

import io.github.diskria.gradle.utils.extensions.common.buildGradleProjectPath
import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.rootDirectory
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.listDirectories
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.extensions.configureMaven
import io.github.diskria.projektor.common.minecraft.MinecraftConstants
import io.github.diskria.projektor.common.minecraft.era.common.MinecraftEra
import io.github.diskria.projektor.common.minecraft.era.common.firstVersion
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.common.minecraft.versions.compareTo
import io.github.diskria.projektor.settings.configurations.MinecraftModConfiguration
import io.github.diskria.projektor.settings.configurators.common.SettingsConfigurator
import io.github.diskria.projektor.settings.configurators.common.dependencyRepositories
import io.github.diskria.projektor.settings.configurators.common.repositories
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
        ModLoaderType.entries.forEach { loader ->
            val loaderName = loader.getName()
            val loaderDirectory = rootDirectory.resolve(loaderName)
            val minSupportedVersionDirectories = loaderDirectory.listDirectories().map {
                MinecraftVersion.parseOrNull(it.name)
                    ?: gradleError("Unknown Minecraft version directory: ${it.relativeTo(rootDirectory)}")
            }
            minSupportedVersionDirectories.forEach { minSupportedVersionDirectory ->
                val modProjectPath = buildGradleProjectPath(loaderName, minSupportedVersionDirectory.asString())
                include(modProjectPath)

                if (minSupportedVersionDirectory < MinecraftEra.BETA.firstVersion()) {
                    include(buildGradleProjectPath(modProjectPath, ModSide.CLIENT.getName()))
                } else {
                    ModSide.entries.forEach {
                        val sideProjectPath = buildGradleProjectPath(modProjectPath, it.getName())
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
                    name = "Fabric",
                    url = buildUrl("maven.fabricmc.net")
                )
                configureMaven(
                    name = "LegacyFabric",
                    url = buildUrl("maven.legacyfabric.net")
                )
                configureMaven(
                    name = "OrnitheReleases",
                    url = buildUrl("maven.ornithemc.net") {
                        path("releases")
                    }
                )
                configureMaven(
                    name = "OrnitheSnapshots",
                    url = buildUrl("maven.ornithemc.net") {
                        path("snapshots")
                    }
                )
                configureMaven(
                    name = "Babric",
                    url = buildUrl("maven.glass-launcher.net") {
                        path("babric")
                    }
                )
                configureMaven(
                    name = "Forge",
                    url = buildUrl("maven.minecraftforge.net")
                )
                configureMaven(
                    name = "NeoForge",
                    url = buildUrl("maven.neoforged.net") {
                        path("releases")
                    }
                )
            }
        }
    }
}
