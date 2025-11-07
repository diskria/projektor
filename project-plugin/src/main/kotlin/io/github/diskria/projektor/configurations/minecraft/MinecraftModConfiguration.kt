package io.github.diskria.projektor.configurations.minecraft

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.log
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sync.loaders.fabric.FabricLoaderSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.loaders.fabric.FabricYarnMappingsSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.loaders.neoforge.NeoforgeLoaderSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.loaders.ornithe.OrnitheFeatherMappingsSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.parchment.ParchmentSynchronizer
import io.github.diskria.projektor.common.minecraft.versions.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.common.minecraft.versions.getMappingsEra
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.loaders.common.ModLoader
import org.gradle.api.Project

open class MinecraftModConfiguration {

    internal val fabric: FabricModConfiguration
        get() = fabricConfig ?: gradleError("Fabric loader not configured")

    internal val ornithe: OrnitheModConfiguration
        get() = ornitheConfig ?: gradleError("Ornithe loader not configured")

    internal val neoforge: NeoforgeModConfiguration
        get() = neoforgeConfig ?: gradleError("NeoForge loader not configured")

    var environment: ModEnvironment = ModEnvironment.CLIENT_SERVER
    var maxSupportedVersion: MinecraftVersion? = null

    private var fabricConfig: FabricModConfiguration? = null
    private var ornitheConfig: OrnitheModConfiguration? = null
    private var neoforgeConfig: NeoforgeModConfiguration? = null

    fun fabric(configuration: FabricModConfiguration.() -> Unit) {
        fabricConfig = FabricModConfiguration().apply(configuration)
    }

    fun ornithe(configuration: OrnitheModConfiguration.() -> Unit) {
        ornitheConfig = OrnitheModConfiguration().apply(configuration)
    }

    fun neoforge(configuration: NeoforgeModConfiguration.() -> Unit) {
        neoforgeConfig = NeoforgeModConfiguration().apply(configuration)
    }

    internal fun resolveConfig(modLoader: ModLoader, project: Project, minecraftVersion: MinecraftVersion) {
        when (modLoader.mapToEnum()) {
            ModLoaderType.FABRIC -> {
                val userConfig = fabricConfig ?: FabricModConfiguration()
                fabricConfig = FabricModConfiguration().apply {
                    loader = userConfig.loader.ifEmpty {
                        FabricLoaderSynchronizer.getLatestVersion(project, minecraftVersion)
                    }
                    yarn = userConfig.yarn.ifEmpty {
                        FabricYarnMappingsSynchronizer.getLatestVersion(project, minecraftVersion)
                    }
                    project.log("[Crafter] Fabric Loader: $loader")
                    project.log("[Crafter] Yarn Mappings: $yarn")
                }
            }

            ModLoaderType.LEGACY_FABRIC -> TODO()

            ModLoaderType.ORNITHE -> {
                val userConfig = ornitheConfig ?: OrnitheModConfiguration()
                ornitheConfig = OrnitheModConfiguration().apply {
                    loader = userConfig.loader.ifEmpty {
                        FabricLoaderSynchronizer.getLatestVersion(project, minecraftVersion)
                    }
                    feather = userConfig.feather.ifEmpty {
                        val mappingsSynchronizer = OrnitheFeatherMappingsSynchronizer(minecraftVersion.getMappingsEra())
                        mappingsSynchronizer.getLatestVersion(project, minecraftVersion)
                    }
                    project.log("[Crafter] Ornithe Loader: $loader")
                    project.log("[Crafter] Feather Mappings: $feather")
                }
            }

            ModLoaderType.BABRIC -> TODO()

            ModLoaderType.NEOFORGE -> {
                val userConfig = neoforgeConfig ?: NeoforgeModConfiguration()
                neoforgeConfig = NeoforgeModConfiguration().apply {
                    loader = userConfig.loader.ifEmpty {
                        NeoforgeLoaderSynchronizer.getLatestVersion(project, minecraftVersion)
                    }
                    parchmentMinecraft = userConfig.parchmentMinecraft.ifEmpty {
                        ParchmentSynchronizer.getLatestComponent(project, minecraftVersion).minecraftVersion.asString()
                    }
                    parchmentMappings = userConfig.parchmentMappings.ifEmpty {
                        ParchmentSynchronizer.getLatestVersion(project, minecraftVersion)
                    }
                    project.log("[Crafter] NeoForge Loader: $loader")
                    project.log("[Crafter] Parchment Mappings: $parchmentMappings")
                }
            }

            ModLoaderType.FORGE -> {

            }
        }
    }
}
