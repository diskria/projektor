package io.github.diskria.projektor.settings.projekt

import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.settings.RepositoriesFilterType
import io.github.diskria.projektor.settings.extensions.configureMaven
import io.github.diskria.projektor.settings.extensions.configureRepositories
import io.github.diskria.projektor.settings.minecraft.ModLoader
import io.github.diskria.projektor.settings.projekt.common.AbstractProjekt
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

class MinecraftMod(
    projekt: IProjekt,
    settingsProvider: () -> Settings
) : AbstractProjekt(
    projekt,
    settingsProvider
), IProjekt by projekt {

    override fun configureRepositories() {
        script {
            applyRepositories(this)
        }
    }

    override fun configureProjects() = script {
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
        fun applyRepositories(settings: Settings) = with(settings) {
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
