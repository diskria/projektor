package io.github.diskria.projektor.configurations.minecraft

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.projektor.common.minecraft.sync.loaders.fabric.FabricApiSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.loaders.fabric.FabricLoaderSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.loaders.fabric.FabricYarnMappingsSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.loaders.neoforge.NeoforgeLoaderSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.loaders.ornithe.OrnitheFeatherMappingsSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.loaders.ornithe.OrnitheFeatherSplitMappingsSynchronizer
import io.github.diskria.projektor.common.minecraft.sync.parchment.ParchmentSynchronizer
import io.github.diskria.projektor.common.minecraft.versions.common.MinecraftVersion
import io.github.diskria.projektor.common.minecraft.versions.common.areSplitMixins
import io.github.diskria.projektor.common.minecraft.versions.common.asString
import io.github.diskria.projektor.minecraft.ModEnvironment
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.minecraft.loaders.fabric.Fabric
import io.github.diskria.projektor.minecraft.loaders.fabric.ornithe.Ornithe
import io.github.diskria.projektor.minecraft.loaders.fabric.quilt.Quilt
import io.github.diskria.projektor.minecraft.loaders.forge.Forge
import io.github.diskria.projektor.minecraft.loaders.forge.neoforge.NeoForge
import org.gradle.api.Project

open class MinecraftModConfiguration {

    internal val fabric: FabricModConfiguration
        get() = fabricConfig ?: gradleError("Fabric loader not configured")

    internal val ornithe: OrnitheModConfiguration
        get() = ornitheConfig ?: gradleError("Ornithe loader not configured")

    internal val neoforge: NeoforgeModConfiguration
        get() = neoforgeConfig ?: gradleError("Neoforge loader not configured")

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
        project.logger.lifecycle("[Crafter] Components:")
        when (modLoader) {
            Fabric -> {
                val userConfig = fabricConfig ?: FabricModConfiguration()
                fabricConfig = FabricModConfiguration().apply {
                    loader = userConfig.loader.ifEmpty {
                        FabricLoaderSynchronizer.getLatestVersion(project, minecraftVersion)
                    }
                    yarn = userConfig.yarn.ifEmpty {
                        FabricYarnMappingsSynchronizer.getLatestVersion(project, minecraftVersion)
                    }
                    api = userConfig.api.ifEmpty {
                        FabricApiSynchronizer.getLatestVersion(project, minecraftVersion)
                    }
                    println("Fabric Loader: $loader")
                    println("Fabric API: $api")
                    println("Fabric Yarn: $yarn")
                }
            }

            Ornithe -> {
                val userConfig = ornitheConfig ?: OrnitheModConfiguration()
                ornitheConfig = OrnitheModConfiguration().apply {
                    loader = userConfig.loader.ifEmpty {
                        FabricLoaderSynchronizer.getLatestVersion(project, minecraftVersion)
                    }
                    feather = userConfig.feather.ifEmpty {
                        if (minecraftVersion.areSplitMixins()) {
                            OrnitheFeatherSplitMappingsSynchronizer.getLatestVersion(project, minecraftVersion)
                        } else {
                            OrnitheFeatherMappingsSynchronizer.getLatestVersion(project, minecraftVersion)
                        }
                    }
                    println("Fabric Loader: $loader")
                    println("Ornithe Feather: $feather")
                }
            }

            NeoForge -> {
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
                    println("NeoForge: $loader")
                    println("Parchment: $parchmentMappings for $parchmentMinecraft")
                }
            }

            Forge -> {

            }

            Quilt -> {

            }
        }
    }
}
