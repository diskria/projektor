package io.github.diskria.projektor.minecraft.loaders.forge

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.common.ProjectDirectories
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.getResourcePackFormat
import io.github.diskria.projektor.common.minecraft.versions.minJavaVersion
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.minecraft.loaders.ModLoader
import io.github.diskria.projektor.minecraft.loaders.SideSourceSets
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.generate.*
import io.github.diskria.projektor.tasks.minecraft.test.TestClientModTask
import io.github.diskria.projektor.tasks.minecraft.test.TestServerModTask
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType

object Neoforge : ModLoader() {

    override fun configure(project: Project, sideProjects: Map<ModSide, Project>, mod: MinecraftMod) = with(project) {
        val sides = sideProjects.keys
        val generateModEntryPointsTask = registerTask<GenerateModEntryPointsTask> {
            minecraftMod = mod
            modSides = sides
            outputDirectory = getBuildDirectory("generated/sources/crafter")
        }
        val modMain = sourceSets.main
        val sourceSetBySides = sideProjects.mapValues { (side, project) ->
            val sideMain = project.sourceSets.main.apply { addToClasspath(modMain) }
            val sideMixins = project.sourceSets.create(SideSourceSets.MIXINS_NAME).apply { addToClasspath(modMain) }
            SideSourceSets(side, sideMain, sideMixins)
        }
        val generateMixinsConfigTask = registerTask<GenerateModMixinsConfigTask> {
            minecraftMod = mod
            sideMixinSourceSetDirectories = sourceSetBySides.mapValues { it.value.mixins.javaSourcesDirectory }
            outputFile = getTempFile(mod.mixinsConfigFileName)
        }
        val generateResourcePackConfigTask = registerTask<GenerateResourcePackConfigTask> {
            minecraftMod = mod
            outputFile = getTempFile(mod.resourcePackConfigFileName)
            minFormat = mod.minSupportedVersion.getResourcePackFormat(project)
            maxFormat = mod.maxSupportedVersion.getResourcePackFormat(project)
        }
        val accessorConfigFiles = sourceSetBySides.map {
            it.value.main.resourcesDirectory.resolve(mod.accessorConfigFileName).ensureFileExists()
        }
        val generateMergedAccessorConfigTask = registerTask<GenerateMergedAccessorConfigTask> {
            accessorConfigs = accessorConfigFiles
            outputFile = getTempFile(mod.accessorConfigFileName)
        }
        val generateModConfigTask = registerTask<GenerateModConfigTask> {
            minecraftMod = mod
            outputFile = getTempFile(mod.configFileName)
        }
        neoforge {
            version = mod.config.neoforge.loader
            parchment {
                minecraftVersion = mod.config.neoforge.minecraft
                mappingsVersion = mod.config.neoforge.mappings
            }
            setAccessTransformers(accessorConfigFiles)
            runs {
                sides.forEach { side ->
                    create(side.getName()) {
                        when (side) {
                            ModSide.CLIENT -> {
                                client()
                                programArguments.addAll(
                                    *JvmArguments.program("username", mod.developerUsername),
                                )
                            }

                            ModSide.SERVER -> {
                                server()
                                programArguments.addAll(
                                    *JvmArguments.program("nogui"),
                                )
                            }
                        }
                    }
                }
            }
        }
        tasks {
            withType<AbstractCopyTask> {
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
            processResources {
                copyTaskOutput(generateResourcePackConfigTask)
                copyTaskOutput(generateMixinsConfigTask, mod.assetsPath)
                copyTaskOutput(generateModConfigTask, mod.configFileParentPath)
                copyTaskOutput(generateMergedAccessorConfigTask, mod.accessorConfigParentPath)

                copyFile(rootProject.getFile(mod.iconFileName).asFile, mod.assetsPath)
            }
            shadowJar {
                exclude(mod.accessorConfigFileName)
            }
            sides.forEach { side ->
                lazyConfigure<JavaExec>("run" + side.getName(PascalCase)) {
                    val shadowJarTask = tasks.shadowJar.get()
                    dependsOn(shadowJarTask)
                    addToClasspath(shadowJarTask.archiveFile)

                    javaLauncher = this@with.getExtension<JavaToolchainService>().launcherFor {
                        val javaVersion = mod.minecraftVersion.minJavaVersion
                        configureJavaVendor(javaVersion, JvmVendorSpec.ADOPTIUM, JvmVendorSpec.AZUL)
                    }
                }
            }
        }
        sideProjects.values.forEach { project ->
            project.tasks {
                jar {
                    from(project.sourceSets.mixins.output)
                }
            }
        }
        sides.forEach { side ->
            when (side) {
                ModSide.CLIENT -> registerTask<TestClientModTask>()
                ModSide.SERVER -> registerTask<TestServerModTask>()
            } {
                dependsOn(tasks.findByName("run" + side.getName(PascalCase)))
            }
        }
        modMain.apply {
            val sourceSetDirectory = getBuildDirectory("sourcesSets")
            val sideSourceSets = sideProjects.flatMap { it.value.sourceSets }
            java {
                srcDirs(generateModEntryPointsTask.map { it.outputDirectory })
                srcDirs(sideSourceSets.map { it.javaSourcesDirectory })
                destinationDirectory = sourceSetDirectory
            }
            resources {
                exclude(mod.accessorConfigFileName)
                srcDirs(sideSourceSets.map { it.resourcesDirectory })
            }
            output.setResourcesDir(sourceSetDirectory)
        }
    }

    private fun configureSourceSets(
        sourceSets: SourceSetContainer,
        mainSources: List<Any>
    ): Pair<SourceSet, SourceSet> {
        val main = sourceSets.main.apply { java.srcDirs(mainSources) }
        val mixins = sourceSets.create(ProjectDirectories.MINECRAFT_MIXINS).apply { addToClasspath(main) }
        return main to mixins
    }
}
