package io.github.diskria.projektor.settings.configurators

import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.ModLoader
import io.github.diskria.projektor.settings.extensions.configureMaven
import io.github.diskria.projektor.settings.extensions.dependencyRepositories
import io.github.diskria.projektor.settings.extensions.repositories
import org.gradle.api.initialization.Settings

open class MinecraftModConfigurator : Configurator() {

    override fun configureProjects(settings: Settings) = with(settings) {
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

    override fun configureRepositories(settings: Settings) = with(settings) {
        super.configureRepositories(settings)
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
    }
}
