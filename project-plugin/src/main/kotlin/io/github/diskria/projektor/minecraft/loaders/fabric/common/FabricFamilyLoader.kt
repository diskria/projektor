package io.github.diskria.projektor.minecraft.loaders.fabric.common

import io.github.diskria.gradle.utils.extensions.common.buildArtifactCoordinates
import io.github.diskria.gradle.utils.extensions.getDirectory
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
import io.github.diskria.projektor.helpers.AccessorConfigHelper
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import java.io.File

abstract class FabricFamilyLoader(val loader: ModLoaderType) : ModLoader() {

    override fun getAccessorConfigTemplate(): String = AccessorConfigHelper.ACCESS_WIDENER_TEMPLATE

    override fun configure(
        mod: MinecraftMod,
        modProject: Project,
        project: Project,
        sideProjects: Map<ModSide, Project>,
        accessorConfigFile: File,
    ) = with(project) {
        super.configure(mod, modProject, project, sideProjects, accessorConfigFile)
        val isMergedMappings = mod.minecraftVersion.mappingsType == MappingsType.MERGED
        fabric {
            runs {
                sideProjects.forEach { (side, sideProject) ->
                    named(side.getName()) {
                        name = side.getName(`Title Case`)
                        runDir = sideProject.getDirectory(mod.config.runDirectory).asFile
                            .relativeTo(project.projectDirectory).path
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
                            JvmArguments.property("mixin.debug.export", true.toString()),
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
            mixin {
                defaultRefmapName = mod.refmapFileName
            }
            accessWidenerPath = accessorConfigFile
        }
        if (!isMergedMappings) {
            val side = sideProjects.keys.single()
            fabric {
                when (side) {
                    ModSide.CLIENT -> clientOnlyMinecraftJar()
                    ModSide.SERVER -> serverOnlyMinecraftJar()
                }
            }
            if (loader == ModLoaderType.ORNITHE) {
                ornithe {
                    @Suppress("DEPRECATION")
                    when (side) {
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
            mappings(resolveMappings(project, mod))
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
