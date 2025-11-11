package io.github.diskria.projektor.configurators

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.generics.addIfNotNull
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.common.ProjectDirectories
import io.github.diskria.projektor.common.minecraft.era.common.MappingsType
import io.github.diskria.projektor.common.minecraft.sides.ModEnvironment
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.mappingsType
import io.github.diskria.projektor.configurations.minecraft.MinecraftModConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.extensions.ensureKotlinPluginsApplied
import io.github.diskria.projektor.extensions.kotlinApply
import io.github.diskria.projektor.extensions.toProjekt
import io.github.diskria.projektor.helpers.AccessorConfigHelper
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModEntryPointsTask
import io.github.diskria.projektor.tasks.minecraft.test.TestClientModTask
import io.github.diskria.projektor.tasks.minecraft.test.TestServerModTask
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke

open class MinecraftModConfigurator(
    val config: MinecraftModConfiguration = MinecraftModConfiguration()
) : ProjectConfigurator<MinecraftMod>() {

    override fun buildProjekt(project: Project): MinecraftMod =
        project.toProjekt().toMinecraftMod(project, config)

    override fun configureProject(project: Project, projekt: MinecraftMod) {
        val versionProject = project
        val mod = projekt
        val loader = mod.loader
        val environment = mod.config.environment
        val sides = environment.sides
        val isMergedMappings = mod.minecraftVersion.mappingsType == MappingsType.MERGED
        val sideProjects = sides.associateWith {
            versionProject.project(it.getName()).kotlinApply {
                ensureKotlinPluginsApplied()
            }
        }
        val modProject = project.rootProject
        if (environment == ModEnvironment.CLIENT_SERVER) {
            val clientProject = sideProjects.getValue(ModSide.CLIENT)
            val serverProject = sideProjects.getValue(ModSide.SERVER)
            with(clientProject) {
                dependencies {
                    compileOnly(serverProject)
                }
            }
        }
        val craftedResources = versionProject.getGeneratedResourcesDirectory().resolve("crafter")
        val craftedSources = versionProject.getGeneratedSourcesDirectory().resolve("crafter")
        val sideAccessorConfigs = sideProjects.mapValues { (_, sideProject) ->
            sideProject.sourceSets.main.resources.srcDirs.first().resolve(mod.accessorConfigFileName).ensureFileExists {
                writeText(loader.getAccessorConfigTemplate())
            }
        }
        if (isMergedMappings) {
            val mergedAccessorConfigFile = craftedResources.resolve(mod.accessorConfigPath).ensureFileExists().apply {
                writeText(AccessorConfigHelper.mergeConfigs(sideAccessorConfigs.values))
            }
            loader.configure(mod, modProject, versionProject, sideProjects, mergedAccessorConfigFile)
        } else {
            sideProjects.forEach { (side, sideProject) ->
                loader.configure(
                    mod,
                    modProject,
                    sideProject,
                    mapOf(side to sideProject),
                    sideAccessorConfigs.getValue(side)
                )
            }
        }
        sideProjects.forEach { (side, sideProject) ->
            val pluginProject = if (isMergedMappings) versionProject else sideProjects.getValue(side)
            val pluginClasspath = pluginProject.sourceSets.main
            sideProject.sourceSets.main.addToClasspath(pluginClasspath)
            val mixins = sideProject.sourceSets.create(ProjectDirectories.MINECRAFT_MIXINS).apply {
                addToClasspath(pluginClasspath)
            }
            with(sideProject) {
                tasks {
                    jar {
                        from(mixins.output)
                    }
                }
            }
            when (side) {
                ModSide.CLIENT -> versionProject.registerTask<TestClientModTask>()
                ModSide.SERVER -> versionProject.registerTask<TestServerModTask>()
            } {
                dependsSequentiallyOn(
                    buildList {
                        add(versionProject.tasks.build.get())
                        addAll(loader.getPrepareRunTasks(pluginProject, side))
                        addIfNotNull(pluginProject.tasks.findByName("run" + side.getName(PascalCase)))
                    }
                )
            }
        }
        val generateModEntryPointsTask = versionProject.registerTask<GenerateModEntryPointsTask> {
            minecraftMod = mod
            modSides = sides
            outputDirectory = craftedSources
        }
        versionProject.sourceSets.main.apply {
            val mergedSourceSetsDirectory = versionProject.getBuildDirectory("sources+resources")
            val sideSourceSets = sideProjects.flatMap { it.value.sourceSets }
            java {
                srcDirs(
                    generateModEntryPointsTask.map { it.outputDirectory },
                    sideSourceSets.flatMap { it.java.srcDirs },
                )
                destinationDirectory = mergedSourceSetsDirectory
            }
            resources {
                exclude(mod.accessorConfigFileName)
                srcDirs(
                    craftedResources,
                    sideSourceSets.flatMap { it.resources.srcDirs },
                )
            }
            output.setResourcesDir(mergedSourceSetsDirectory)
        }
    }
}
