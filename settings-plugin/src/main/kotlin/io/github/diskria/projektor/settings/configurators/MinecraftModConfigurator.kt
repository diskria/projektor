package io.github.diskria.projektor.settings.configurators

import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.settings.configurations.MinecraftModConfiguration
import io.github.diskria.projektor.settings.extensions.configureMaven
import io.github.diskria.projektor.settings.extensions.configureRepositories
import io.github.diskria.projektor.settings.minecraft.ModLoader
import io.github.diskria.projektor.settings.projekt.MinecraftMod
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import io.github.diskria.projektor.settings.repositories.DependencyRepositories
import org.gradle.api.initialization.Settings

open class MinecraftModConfigurator(
    val config: MinecraftModConfiguration
) : Configurator<MinecraftMod>() {

    override fun configure(settings: Settings, projekt: IProjekt): MinecraftMod = with(settings) {
        val minecraftMod = MinecraftMod(projekt, config)
        applyCommonConfiguration(settings, minecraftMod)
        configureRepositories(DependencyRepositories) {
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
        return minecraftMod
    }
}
