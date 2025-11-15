package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.projektor.common.minecraft.era.common.MappingsType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderFamily
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.getResourcePackFormat
import io.github.diskria.projektor.common.minecraft.versions.mappingsType
import io.github.diskria.projektor.common.minecraft.versions.minJavaVersion
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.extensions.mappers.mapToEnum
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModEntryPointsTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModMixinsConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateResourcePackConfigTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.JavaExec
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import java.io.File

abstract class AbstractModLoader {

    val family: ModLoaderFamily
        get() = ModLoaderFamily.of(mapToEnum())

    open fun configure(
        mod: MinecraftMod,
        modProject: Project,
        pluginProject: Project,
        sideProjects: Map<ModSide, Project>,
        accessorConfigFile: File,
    ): Any = with(pluginProject) {
        val isMergedMappings = mod.minecraftVersion.mappingsType == MappingsType.MERGED
        modProject.findCommonProject()?.let { commonProject ->
            dependencies {
                add("compileOnly", commonProject)
            }
            tasks {
                jar {
                    from(commonProject.sourceSets.main.output)
                }
            }
        }
        tasks {
            withType<AbstractCopyTask> {
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
            sideProjects.keys.forEach { side ->
                lazyConfigure<JavaExec>(side.getRunTaskName()) {
                    addToClasspath(jar.get().archiveFile)
                    if (mod.minecraftVersion.mappingsType == MappingsType.MERGED) {
                        javaLauncher = pluginProject.getExtension<JavaToolchainService>().launcherFor {
                            val javaVersion = mod.minecraftVersion.minJavaVersion
                            configureJavaVendor(javaVersion, JvmVendorSpec.ADOPTIUM, JvmVendorSpec.AZUL)
                        }
                    }
                }
            }
            if (isResourcePackConfigRequired()) {
                val generateResourcePackConfigTask = registerTask<GenerateResourcePackConfigTask> {
                    minecraftMod = mod
                    outputFile = getTempFile(mod.resourcePackConfigFileName)
                    format = mod.minecraftVersion.getResourcePackFormat(pluginProject)
                }
                processResources {
                    copyTaskOutput(generateResourcePackConfigTask)
                }
            }
            val generateMixinsConfigTask = registerTask<GenerateModMixinsConfigTask> {
                minecraftMod = mod
                sideSourceSetDirectories = sideProjects.mapValues { it.value.sourceSets.mixins.java.srcDirs.first() }
                outputFile = getTempFile(mod.mixinsConfigFileName)
            }
            val generateModConfigTask = registerTask<GenerateModConfigTask> {
                minecraftMod = mod
                singleSide = sideProjects.keys.singleOrNull()
                outputFile = getTempFile(mod.configFileName)
            }
            processResources {
                copyTaskOutput(generateMixinsConfigTask, mod.assetsPath)
                copyTaskOutput(generateModConfigTask, mod.configFileParentPath)
                copyFile(modProject.getFile(mod.iconFileName).asFile, mod.assetsPath)
                if (!isMergedMappings) {
                    moveFile(mod.accessorConfigFileName, mod.accessorConfigPath)
                }
            }
        }
        val generateModEntryPointsTask = registerTask<GenerateModEntryPointsTask> {
            minecraftMod = mod
            sides = sideProjects.keys
            outputDirectory = craftedSourcesDirectory
        }
        sourceSets.main.apply {
            val mergedSourceSetsDirectory = getBuildDirectory("sources+resources")
            val sideSourceSets = sideProjects.flatMap { it.value.sourceSets }
            java {
                srcDirs(
                    generateModEntryPointsTask.map { it.outputDirectory },
                    sideSourceSets.flatMap { it.java.srcDirs },
                )
                destinationDirectory = mergedSourceSetsDirectory
            }
            resources {
                srcDirs(sideSourceSets.flatMap { it.resources.srcDirs })
                if (isMergedMappings) {
                    exclude(mod.accessorConfigFileName)
                    srcDirs(craftedResourcesDirectory)
                }
            }
            output.setResourcesDir(mergedSourceSetsDirectory)
        }
    }

    open fun getPrepareRunTasks(pluginProject: Project, side: ModSide): List<Task> = emptyList()

    open fun getAccessorConfigPreset(): String = Constants.Char.EMPTY

    open fun isResourcePackConfigRequired(): Boolean = false
}
