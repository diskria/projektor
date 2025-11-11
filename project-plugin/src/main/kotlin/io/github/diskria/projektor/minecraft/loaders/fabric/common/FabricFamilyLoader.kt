package io.github.diskria.projektor.minecraft.loaders.fabric.common

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.gradle.utils.extensions.common.buildArtifactCoordinates
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.gradle.utils.helpers.jvm.Size
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.failWithUnsupportedType
import io.github.diskria.kotlin.utils.extensions.ensureDirectoryExists
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.common.ProjectDirectories
import io.github.diskria.projektor.common.minecraft.era.common.MappingsEra
import io.github.diskria.projektor.common.minecraft.era.common.MinecraftEra
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderType
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.common.minecraft.versions.mappingsEra
import io.github.diskria.projektor.common.minecraft.versions.minJavaVersion
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.helpers.AccessWidenerHelper
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.minecraft.loaders.SideSourceSets
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateMergedAccessorConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModEntryPointsTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModMixinsConfigTask
import io.github.diskria.projektor.tasks.minecraft.test.TestClientModTask
import io.github.diskria.projektor.tasks.minecraft.test.TestServerModTask
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType

abstract class FabricFamilyLoader(val loader: ModLoaderType) : ModLoader() {

    override fun configure(project: Project, sideProjects: Map<ModSide, Project>, mod: MinecraftMod) = with(project) {
        val sides = sideProjects.keys
        val modMain = sourceSets.main
        val generateModEntryPointsTask = registerTask<GenerateModEntryPointsTask> {
            minecraftMod = mod
            modSides = sides
            outputDirectory = getBuildDirectory("generated/sources/crafter")
        }
        val sourceSetBySides = sideProjects.mapValues { (side, sideProject) ->
            val sideMain = sideProject.sourceSets.main.apply { addToClasspath(modMain) }
            val sideMixins = sideProject.sourceSets.create(SideSourceSets.MIXINS_NAME).apply { addToClasspath(modMain) }
            SideSourceSets(side, sideMain, sideMixins)
        }
        val generateMixinsConfigTask = registerTask<GenerateModMixinsConfigTask> {
            minecraftMod = mod
            sideMixinSourceSetDirectories = sourceSetBySides.mapValues { it.value.mixins.javaSourcesDirectory }
            outputFile = getTempFile(mod.mixinsConfigFileName)
        }
        val generateModConfigTask = registerTask<GenerateModConfigTask> {
            minecraftMod = mod
            outputFile = getTempFile(mod.configFileName)
        }
        val accessorConfigFiles = sourceSetBySides.map {
            it.value.main.resourcesDirectory.resolve(mod.accessorConfigFileName).ensureFileExists {
                writeText(AccessWidenerHelper.TEMPLATE)
            }
        }
        val generatedResourcesDirectory = getBuildDirectory("generated/resources/crafter").get().asFile
            .ensureDirectoryExists()
        val mergedAccessorConfigFile = generatedResourcesDirectory.resolve(mod.accessorConfigPath)
            .ensureFileExists()
            .apply { writeText(GenerateMergedAccessorConfigTask.buildFileText(accessorConfigFiles)) }
        fabric {
            runs {
                sides.forEach { side ->
                    named(side.getName()) {
                        name = side.getName(`Title Case`)
                        runDir = sideProjects.getValue(side)
                            .projectDirectory
                            .resolve(ProjectDirectories.MINECRAFT_RUN)
                            .relativeTo(project.projectDirectory)
                            .path
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
            accessWidenerPath = mergedAccessorConfigFile
        }
        restoreDependencyResolutionRepositories()
        dependencies {
            minecraft("com.mojang", "minecraft", mod.minecraftVersion.asString())
            val loaderVersion = when (loader) {
                ModLoaderType.LEGACY_FABRIC -> mod.config.legacyFabric.loader
                ModLoaderType.ORNITHE -> mod.config.ornithe.loader
                else -> mod.config.fabric.loader
            }
            modImplementation("net.fabricmc", "fabric-loader", loaderVersion)
            mappings(resolveMappings(this@with, mod))
        }
        tasks {
            withType<AbstractCopyTask> {
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
            processResources {
                copyTaskOutput(generateMixinsConfigTask, mod.assetsPath)
                copyTaskOutput(generateModConfigTask, mod.configFileParentPath)

//                copyFile(mergedAccessorConfigFile, mod.accessorConfigParentPath)
                copyFile(rootProject.getFile(mod.iconFileName).asFile, mod.assetsPath)
            }
        }
        sideProjects.forEach { (side, sideProject) ->
            tasks.lazyConfigure<JavaExec>("run" + side.getName(PascalCase)) {
                javaLauncher = this@with.getExtension<JavaToolchainService>().launcherFor {
                    val javaVersion = mod.minecraftVersion.minJavaVersion
                    configureJavaVendor(javaVersion, JvmVendorSpec.ADOPTIUM, JvmVendorSpec.AZUL)
                }
            }
            sideProject.tasks {
                jar {
                    from(project.sourceSets.mixins.output)
                }
            }
            when (side) {
                ModSide.CLIENT -> registerTask<TestClientModTask>()
                ModSide.SERVER -> registerTask<TestServerModTask>()
            } {
                dependsOn(tasks.findByName("run" + side.getName(PascalCase)))
            }
        }
        modMain.apply {
            val mergedSourceSetsDirectory = getBuildDirectory("sourcesSets")
            val sideSourceSets = sideProjects.flatMap { it.value.sourceSets }
            java {
                srcDirs(generateModEntryPointsTask.map { it.outputDirectory })
                srcDirs(sideSourceSets.map { it.javaSourcesDirectory })
                destinationDirectory = mergedSourceSetsDirectory
            }
            resources {
                exclude(mod.accessorConfigFileName)
                srcDirs(generatedResourcesDirectory)
                srcDirs(sideSourceSets.map { it.resourcesDirectory })
            }
            output.setResourcesDir(mergedSourceSetsDirectory)
        }
        if (mod.minecraftVersion.mappingsEra != MappingsEra.MERGED) {
            val side = sides.single()
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
