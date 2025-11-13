package io.github.diskria.projektor.minecraft.loaders.fabric.common

import io.github.diskria.gradle.utils.extensions.common.buildArtifactCoordinates
import io.github.diskria.gradle.utils.extensions.projectDirectory
import io.github.diskria.gradle.utils.extensions.restoreDependencyResolutionRepositories
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.gradle.utils.helpers.jvm.Size
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.failWithUnsupportedType
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.era.common.MappingsType
import io.github.diskria.projektor.common.minecraft.era.common.MinecraftEra
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.common.minecraft.versions.mappingsType
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.minecraft.helpers.AccessorConfigHelper
import io.github.diskria.projektor.minecraft.loaders.AbstractModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import java.io.File

abstract class AbstractFabricModLoader(val loader: ModLoaderType) : AbstractModLoader() {

    override fun getAccessorConfigPreset(): String = AccessorConfigHelper.WIDENER_PRESET

    override fun configure(
        mod: MinecraftMod,
        modProject: Project,
        pluginProject: Project,
        sideProjects: Map<ModSide, Project>,
        accessorConfigFile: File,
    ) = with(pluginProject) {
        super.configure(mod, modProject, pluginProject, sideProjects, accessorConfigFile)
        fabric {
            runs {
                sideProjects.forEach { (side, project) ->
                    val runDirectory = project.projectDirectory.resolve(mod.config.runDirectoryName)
                    named(side.getName()) {
                        name = side.getName(`Title Case`)
                        runDir = runDirectory.relativeTo(pluginProject.projectDirectory).path
                        when (side) {
                            ModSide.CLIENT -> client()
                            ModSide.SERVER -> server()
                        }
                        val memoryRange = when (side) {
                            ModSide.CLIENT -> 2..4
                            ModSide.SERVER -> 4..8
                        }
                        vmArgs(
                            *JvmArguments.memory(memoryRange, Size.GIGABYTES),
                            JvmArguments.property("mixin.debug.export", true),
                        )
                        if (mod.minecraftVersion.getEra() < MinecraftEra.ALPHA) {
                            vmArgs(
                                JvmArguments.property("fabric.gameVersion", mod.minecraftVersion.asString()),
                            )
                        }
                        if (side == ModSide.CLIENT) {
                            programArgs(
                                *JvmArguments.program("username", mod.developerUsername),
                                *JvmArguments.program("userProperties", Constants.Json.EMPTY_OBJECT),
                            )
                        }
                    }
                }
            }
            @Suppress("UnstableApiUsage")
            mixin {
                defaultRefmapName = mod.refmapFileName
            }
            accessWidenerPath = accessorConfigFile
        }
        if (mod.minecraftVersion.mappingsType != MappingsType.MERGED) {
            val singleSide = sideProjects.keys.single()
            fabric {
                when (singleSide) {
                    ModSide.CLIENT -> clientOnlyMinecraftJar()
                    ModSide.SERVER -> serverOnlyMinecraftJar()
                }
            }
            if (loader == ModLoaderType.ORNITHE) {
                ornithe {
                    @Suppress("DEPRECATION")
                    when (singleSide) {
                        ModSide.CLIENT -> clientOnlyMappings()
                        ModSide.SERVER -> serverOnlyMappings()
                    }
                }
            }
        }
        restoreDependencyResolutionRepositories()
        dependencies {
            val loaderVersion = when (loader) {
                ModLoaderType.LEGACY_FABRIC -> mod.config.legacyFabric.loader
                ModLoaderType.ORNITHE -> mod.config.ornithe.loader
                else -> mod.config.fabric.loader
            }
            minecraft("com.mojang", "minecraft", mod.minecraftVersion.asString())
            modImplementation("net.fabricmc", "fabric-loader", loaderVersion)
            mappings(resolveMappings(pluginProject, mod))
        }
    }

    private fun resolveMappings(project: Project, mod: MinecraftMod): Any = with(project) {
        val versions = mod.config.getVersions(loader)
        when (loader) {
            ModLoaderType.FABRIC -> buildArtifactCoordinates("net.fabricmc", "yarn", versions.mappings, "v2")
            ModLoaderType.LEGACY_FABRIC -> legacyFabric.yarn(versions.minecraft, versions.mappings)
            ModLoaderType.ORNITHE -> ornithe.featherMappings(versions.mappings)
            else -> failWithUnsupportedType(loader::class)
        }
    }
}
