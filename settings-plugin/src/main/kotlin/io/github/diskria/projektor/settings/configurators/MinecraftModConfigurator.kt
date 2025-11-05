package io.github.diskria.projektor.settings.configurators

import io.github.diskria.gradle.utils.extensions.common.buildGradleProjectPath
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.extensions.listDirectories
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.ModSide
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.utils.MinecraftConstants
import io.github.diskria.projektor.settings.configurations.MinecraftModConfiguration
import io.github.diskria.projektor.settings.configurators.common.SettingsConfigurator
import io.github.diskria.projektor.settings.extensions.configureMaven
import io.github.diskria.projektor.settings.extensions.dependencyRepositories
import io.github.diskria.projektor.settings.extensions.repositories
import io.ktor.http.*
import org.gradle.api.initialization.Settings

open class MinecraftModConfigurator(
    val config: MinecraftModConfiguration = MinecraftModConfiguration()
) : SettingsConfigurator() {

    override fun configureRepositories(settings: Settings) {
        applyRepositories(settings)
    }

    override fun configureProjects(settings: Settings) = with(settings) {
        ModLoaderType.entries.forEach { loader ->
            val loaderName = loader.getName()
            rootDir.resolve(loaderName).listDirectories().toNullIfEmpty()?.forEach { minSupportedVersionDirectory ->
                include(buildGradleProjectPath(loaderName, minSupportedVersionDirectory.name))

                ModSide.entries.forEach {
                    include(buildGradleProjectPath(loaderName, minSupportedVersionDirectory.name, it.getName()))
                }
            }
        }
    }

    companion object {
        fun applyRepositories(settings: Settings) = with(settings) {
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
            repositories {
                configureMaven(
                    name = "Fabric",
                    url = buildUrl("maven.fabricmc.net")
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
                    name = "Forge",
                    url = buildUrl("maven.minecraftforge.net")
                )
                configureMaven(
                    name = "NeoForge",
                    url = buildUrl("maven.neoforged.net") {
                        path("releases")
                    }
                )
                configureMaven(
                    name = "Quilt",
                    url = buildUrl("maven.quiltmc.org") {
                        path("repository", "release")
                    }
                )
            }
        }
    }
}
