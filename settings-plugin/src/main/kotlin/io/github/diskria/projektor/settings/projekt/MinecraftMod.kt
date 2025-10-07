package io.github.diskria.projektor.settings.projekt

import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.settings.RepositoriesFilterType
import io.github.diskria.projektor.settings.extensions.configureMaven
import io.github.diskria.projektor.settings.extensions.configureRepositories
import io.github.diskria.projektor.settings.minecraft.ModLoader
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

data class MinecraftMod(private val projekt: IProjekt, private val settings: Settings) : IProjekt by projekt {

    override val configureRepositories: Settings.() -> Unit = applyRepositories

    override val configureProjects: Settings.() -> Unit = {
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

    companion object {
        val applyRepositories: Settings.() -> Unit = {
            configureRepositories(RepositoriesFilterType.DEPENDENCIES) {
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
            configureRepositories {
                configureMaven(
                    name = "Fabric",
                    url = "https://maven.fabricmc.net"
                )
            }
        }
    }
}
