package io.github.diskria.projektor.configurations.minecraft

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.log
import io.github.diskria.projektor.common.ProjectDirectories
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType.*
import io.github.diskria.projektor.common.minecraft.sides.ModEnvironment
import io.github.diskria.projektor.common.minecraft.sync.loaders.fabric.FabricLoaderSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.loaders.fabric.FabricYarnMappingsSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.loaders.forge.ForgeLoaderSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.loaders.legacy_fabric.LegacyFabricYarnMappingsSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.loaders.neoforge.NeoForgeLoaderSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.loaders.ornithe.OrnitheFeatherMappingsSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.parchment.ParchmentSynchronizer
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.common.minecraft.versions.mappingsType
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.minecraft.loaders.AbstractModLoader
import org.gradle.api.Project

open class MinecraftModConfiguration {

    internal val fabric: MinecraftVersionsConfig
        get() = fabricConfig ?: gradleError("Fabric loader not configured")

    internal val legacyFabric: MinecraftVersionsConfig
        get() = legacyFabricConfig ?: gradleError("Legacy Fabric loader not configured")

    internal val ornithe: MinecraftVersionsConfig
        get() = ornitheConfig ?: gradleError("Ornithe loader not configured")

    internal val forge: MinecraftVersionsConfig
        get() = forgeConfig ?: gradleError("Forge loader not configured")

    internal val neoforge: MinecraftVersionsConfig
        get() = neoforgeConfig ?: gradleError("NeoForge loader not configured")

    var environment: ModEnvironment = ModEnvironment.CLIENT_SERVER
    var maxSupportedVersion: MinecraftVersion? = null
    var javaVersion: Int? = null
    var runDirectoryName: String = ProjectDirectories.MINECRAFT_RUN

    private var fabricConfig: MinecraftVersionsConfig? = null
    private var legacyFabricConfig: MinecraftVersionsConfig? = null
    private var ornitheConfig: MinecraftVersionsConfig? = null
    private var forgeConfig: MinecraftVersionsConfig? = null
    private var neoforgeConfig: MinecraftVersionsConfig? = null

    fun fabric(configure: MinecraftVersionsConfig.() -> Unit) {
        fabricConfig = MinecraftVersionsConfig().apply(configure)
    }

    fun legacyFabric(configure: MinecraftVersionsConfig.() -> Unit) {
        legacyFabricConfig = MinecraftVersionsConfig().apply(configure)
    }

    fun ornithe(configure: MinecraftVersionsConfig.() -> Unit) {
        ornitheConfig = MinecraftVersionsConfig().apply(configure)
    }

    fun forge(configure: MinecraftVersionsConfig.() -> Unit) {
        forgeConfig = MinecraftVersionsConfig().apply(configure)
    }

    fun neoforge(configure: MinecraftVersionsConfig.() -> Unit) {
        neoforgeConfig = MinecraftVersionsConfig().apply(configure)
    }

    internal fun getVersions(loader: ModLoaderType): MinecraftVersionsConfig =
        when (loader) {
            FABRIC -> fabric
            LEGACY_FABRIC -> legacyFabric
            ORNITHE -> ornithe
            FORGE -> forge
            NEOFORGE -> neoforge
        }

    internal fun resolveConfig(modLoader: AbstractModLoader, project: Project, minSupportedVersion: MinecraftVersion) {
        when (modLoader.mapToEnum()) {
            FABRIC -> {
                val userConfig = fabricConfig ?: MinecraftVersionsConfig()
                fabricConfig = MinecraftVersionsConfig().apply {
                    loader = userConfig.loader.ifEmpty {
                        FabricLoaderSynchronizer.getLatestVersion(project, minSupportedVersion)
                    }
                    mappings = userConfig.mappings.ifEmpty {
                        FabricYarnMappingsSynchronizer.getLatestVersion(project, minSupportedVersion)
                    }
                    minecraft = minSupportedVersion.asString()
                    project.log("[Crafter] Fabric Loader: $loader")
                    project.log("[Crafter] Yarn Mappings: $mappings")
                }
            }

            LEGACY_FABRIC -> {
                val userConfig = legacyFabricConfig ?: MinecraftVersionsConfig()
                legacyFabricConfig = MinecraftVersionsConfig().apply {
                    loader = userConfig.loader.ifEmpty {
                        FabricLoaderSynchronizer.getLatestVersion(project, minSupportedVersion)
                    }
                    mappings = userConfig.mappings.ifEmpty {
                        LegacyFabricYarnMappingsSynchronizer.getLatestVersion(project, minSupportedVersion)
                    }
                    minecraft = userConfig.minecraft
                        ?: LegacyFabricYarnMappingsSynchronizer.getLatestComponent(project, minSupportedVersion)
                            .minecraftVersion.asString()

                    project.log("[Crafter] Legacy Fabric Loader: $loader")
                    project.log("[Crafter] Legacy Fabric Yarn Mappings: $mappings")
                }
            }

            ORNITHE -> {
                val userConfig = ornitheConfig ?: MinecraftVersionsConfig()
                ornitheConfig = MinecraftVersionsConfig().apply {
                    loader = userConfig.loader.ifEmpty {
                        FabricLoaderSynchronizer.getLatestVersion(project, minSupportedVersion)
                    }
                    mappings = userConfig.mappings.ifEmpty {
                        val synchronizer = OrnitheFeatherMappingsSynchronizer(minSupportedVersion.mappingsType)
                        synchronizer.getLatestVersion(project, minSupportedVersion)
                    }
                    project.log("[Crafter] Ornithe Loader: $loader")
                    project.log("[Crafter] Feather Mappings: $mappings")
                }
            }

            FORGE -> {
                val userConfig = forgeConfig ?: MinecraftVersionsConfig()
                forgeConfig = MinecraftVersionsConfig().apply {
                    loader = userConfig.loader.ifEmpty {
                        ForgeLoaderSynchronizer.getLatestVersion(project, minSupportedVersion)
                    }
                    mappings = userConfig.mappings.ifEmpty {
                        ParchmentSynchronizer.getLatestVersion(project, minSupportedVersion)
                    }
                    minecraft = userConfig.minecraft
                        ?: ParchmentSynchronizer.getLatestComponent(project, minSupportedVersion)
                            .minecraftVersion.asString()
                    project.log("[Crafter] Forge Loader: $loader")
                    project.log("[Crafter] Parchment Mappings: $mappings")
                }
            }

            NEOFORGE -> {
                val userConfig = neoforgeConfig ?: MinecraftVersionsConfig()
                neoforgeConfig = MinecraftVersionsConfig().apply {
                    loader = userConfig.loader.ifEmpty {
                        NeoForgeLoaderSynchronizer.getLatestVersion(project, minSupportedVersion)
                    }
                    mappings = userConfig.mappings.ifEmpty {
                        ParchmentSynchronizer.getLatestVersion(project, minSupportedVersion)
                    }
                    minecraft = userConfig.minecraft
                        ?: ParchmentSynchronizer.getLatestComponent(project, minSupportedVersion)
                            .minecraftVersion.asString()
                    project.log("[Crafter] NeoForge Loader: $loader")
                    project.log("[Crafter] Parchment Mappings: $mappings")
                }
            }
        }
    }
}
