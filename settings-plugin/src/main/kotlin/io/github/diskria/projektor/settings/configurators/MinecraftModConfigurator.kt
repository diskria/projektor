package io.github.diskria.projektor.settings.configurators

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.ModLoaderType
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
        include(":common")
        ModLoaderType.entries.forEach { loader ->
            val loaderName = loader.getName()
            rootDir.resolve(loaderName).listFiles()?.filter { it.isDirectory }?.forEach { versionDirectory ->
                include(":$loaderName:${versionDirectory.name}")
            }
        }
    }

    companion object {
        fun applyRepositories(settings: Settings) = with(settings) {
            dependencyRepositories {
                configureMaven(
                    name = "Minecraft",
                    url = buildUrl("libraries.minecraft.net")
                )
                configureMaven(
                    name = "SpongePowered",
                    url = buildUrl("repo.spongepowered.org") {
                        path("repository", "maven-public")
                    },
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
            }
        }
    }
}
