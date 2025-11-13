package io.github.diskria.projektor.configurators

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.ensureDirectoryExists
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.generics.addIfNotNull
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.ProjectDirectories
import io.github.diskria.projektor.common.minecraft.era.common.MappingsType
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderFamily
import io.github.diskria.projektor.common.minecraft.sides.ModEnvironment
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.mappingsType
import io.github.diskria.projektor.configurations.minecraft.MinecraftModConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.minecraft.helpers.AccessorConfigHelper
import io.github.diskria.projektor.minecraft.helpers.server.EulaHelper
import io.github.diskria.projektor.minecraft.helpers.server.ServerOperatorsHelper
import io.github.diskria.projektor.minecraft.helpers.server.ServerPropertiesHelper
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.ZipMultiSideMinecraftModTask
import io.github.diskria.projektor.tasks.minecraft.test.TestClientModTask
import io.github.diskria.projektor.tasks.minecraft.test.TestServerModTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
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
        val sideAccessorConfigFiles = sideProjects.mapValues { (_, sideProject) ->
            sideProject.sourceSets.main.resources.srcDirs.first().resolve(mod.accessorConfigFileName).ensureFileExists {
                writeText(loader.getAccessorConfigPreset())
            }
        }
        sideProjects.forEach { (side, sideProject) ->
            val runDirectory = sideProject.projectDirectory.resolve(mod.config.runDirectoryName).ensureDirectoryExists()
            if (side == ModSide.SERVER) {
                runDirectory.resolve(EulaHelper.FILE_NAME).ensureFileExists {
                    writeText(EulaHelper.buildPreset(mod))
                }
                runDirectory.resolve(ServerPropertiesHelper.FILE_NAME).ensureFileExists {
                    writeText(ServerPropertiesHelper.buildPreset(mod))
                }
                runDirectory.resolve(ServerOperatorsHelper.FILE_NAME).ensureFileExists {
                    writeText(ServerOperatorsHelper.buildPreset(mod))
                }
            }
        }
        sideProjects.values.forEach { sideProject ->
            val pluginProject = if (isMergedMappings) versionProject else sideProject
            val pluginMainSourceSet = pluginProject.sourceSets.main
            if (isMergedMappings) {
                sideProject.sourceSets.main.addToClasspath(pluginMainSourceSet, withOutput = true)
            }
            val mixins = sideProject.sourceSets.create(ProjectDirectories.MINECRAFT_MIXINS).apply {
                addToClasspath(pluginMainSourceSet, withOutput = true)
            }
            with(sideProject) {
                tasks {
                    jar {
                        from(mixins.output)
                    }
                }
            }
        }
        if (isMergedMappings) {
            val mergedAccessorConfigFile = versionProject.craftedResourcesDirectory.resolve(mod.accessorConfigPath)
                .ensureFileExists().apply {
                    writeText(AccessorConfigHelper.mergeConfigurations(sideAccessorConfigFiles.values))
                }
            loader.configure(mod, modProject, versionProject, sideProjects, mergedAccessorConfigFile)
        } else {
            val multiSideJarTasks = mutableListOf<TaskProvider<out Jar>>()
            val libsDirectory = versionProject.getBuildDirectory("libs")
            val isFabricFamily = mod.loader.family == ModLoaderFamily.FABRIC
            sideProjects.forEach { (side, sideProject) ->
                with(versionProject) {
                    tasks {
                        build {
                            dependsOn(sideProject.tasks.build)
                        }
                    }
                }
                loader.configure(
                    mod,
                    modProject,
                    sideProject,
                    mapOf(side to sideProject),
                    sideAccessorConfigFiles.getValue(side)
                )
                val sideJarTask = when {
                    isFabricFamily -> sideProject.tasks.fabricRemapJar
                    else -> sideProject.tasks.jar
                }
                with(sideProject) {
                    tasks {
                        val versionJarTask = versionProject.tasks.jar.get()
                        jar {
                            destinationDirectory.set(
                                if (isFabricFamily) versionProject.getBuildDirectory("devlibs")
                                else libsDirectory
                            )
                            val jarClassifier = buildString {
                                append(side.getName())
                                archiveClassifier.orNull?.let { append(Constants.Char.HYPHEN + it) }
                            }
                            copyArchiveName(versionJarTask, classifier = jarClassifier)
                        }
                        if (isFabricFamily) {
                            sideJarTask {
                                destinationDirectory.set(libsDirectory)
                                copyArchiveName(versionJarTask, classifier = side.getName())
                            }
                        }
                    }
                }
                if (environment == ModEnvironment.CLIENT_SERVER) {
                    multiSideJarTasks.add(sideJarTask)
                } else {
                    with(versionProject) {
                        val singleSideJar = sideJarTask.get()
                        tasks {
                            jar {
                                copyArchiveName(singleSideJar)
                                disable()
                            }
                        }
                        artifacts {
                            add("archives", singleSideJar.archiveFile) {
                                builtBy(singleSideJar)
                            }
                        }
                    }
                }
            }
            if (multiSideJarTasks.isNotEmpty()) {
                versionProject.registerTask<ZipMultiSideMinecraftModTask> {
                    dependsOn(multiSideJarTasks)
                    val sideJarTask = multiSideJarTasks.first().get()
                    from(multiSideJarTasks.map { task -> task.map { jar -> jar.archiveFile.get().asFile } })
                    destinationDirectory.set(sideJarTask.destinationDirectory)
                    copyArchiveName(sideJarTask, classifier = null, extension = Constants.File.Extension.ZIP)
                }
            }
        }
        sideProjects.forEach { (side, sideProject) ->
            val pluginProject = if (isMergedMappings) versionProject else sideProject
            when (side) {
                ModSide.CLIENT -> versionProject.registerTask<TestClientModTask>()
                ModSide.SERVER -> versionProject.registerTask<TestServerModTask>()
            } {
                dependsSequentiallyOn(
                    buildList {
                        add(versionProject.tasks.build.get())
                        addAll(loader.getPrepareRunTasks(pluginProject, side))
                        addIfNotNull(pluginProject.getTaskOrNull(side.getRunTaskName()))
                    }
                )
            }
        }
    }
}
