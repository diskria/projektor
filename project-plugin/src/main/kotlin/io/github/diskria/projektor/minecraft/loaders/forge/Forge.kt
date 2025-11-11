package io.github.diskria.projektor.minecraft.loaders.forge

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.common.ProjectDirectories
import io.github.diskria.projektor.common.minecraft.era.Release
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.common.minecraft.versions.compareTo
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
import org.gradle.internal.Actions.with
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.*

object Forge : ModLoader() {

    override fun configure(
        modProject: Project,
        sideProjects: Map<ModSide, Project>,
        mod: MinecraftMod
    ) = with(modProject) {
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
        val generateMergedAccessorConfigTask = registerTask<GenerateMergedAccessorConfigTask> {
            minecraftMod = mod
            sideResourcesDirectories = sourceSetBySides.values.map { it.main.resourcesDirectory }
            outputFile = getTempFile(mod.accessorConfigFileName)
        }
        val generateModConfigTask = registerTask<GenerateModConfigTask> {
            minecraftMod = mod
            outputFile = getTempFile(mod.configFileName)
        }
        forge {
            reobf = mod.minecraftVersion < Release.V_1_20_6
            mappings("official", mod.minecraftVersion.asString())
            setAccessTransformer(generateMergedAccessorConfigTask.map { it.outputFile })
            runs {
                sides.forEach { side ->
                    create(side.getName()) {
                        workingDirectory = sideProjects.getValue(side)
                            .projectDirectory
                            .resolve(ProjectDirectories.MINECRAFT_RUN)
                            .absolutePath
                        args(
                            *JvmArguments.program("mixin.config", mod.mixinsConfigPath),
                        )
                        when (side) {
                            ModSide.CLIENT -> args(
                                *JvmArguments.program("username", mod.developerPlayerName),
                            )

                            ModSide.SERVER -> args(
                                *JvmArguments.program("nogui"),
                            )
                        }
                    }
                }
            }
        }
        dependencies {
            val forgeVersion = "${mod.minecraftVersion.asString()}-${mod.config.forge.loader}"
            minecraft("net.minecraftforge", "forge", forgeVersion)
        }
        tasks {
            jar {
                manifest {
                    val mixinConfigs by mod.mixinsConfigPath.autoNamedProperty(PascalCase)
                    attributes(
                        listOf(
                            mixinConfigs,
                        ).associate { it.name to it.value }
                    )
                }
            }
            withType<AbstractCopyTask> {
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            }
            processResources {
                copyTaskOutput(generateResourcePackConfigTask)
                copyTaskOutput(generateMixinsConfigTask, mod.assetsPath)
                copyTaskOutput(generateModConfigTask, mod.configFileParentPath)
                copyTaskOutput(generateMergedAccessorConfigTask, mod.assetsPath)

                copyFile(rootProject.getFile(mod.iconFileName).asFile, mod.assetsPath)
            }
            shadowJar {
                exclude(mod.accessorConfigFileName)
            }
            withType<JavaExec> {
                val shadowJarTask = modProject.tasks.shadowJar.get()
                dependsOn(shadowJarTask)
                addToClasspath(shadowJarTask.archiveFile)

                javaLauncher = modProject.getExtension<JavaToolchainService>().launcherFor {
                    val javaVersion = mod.minecraftVersion.minJavaVersion
                    configureJavaVendor(javaVersion, JvmVendorSpec.ADOPTIUM, JvmVendorSpec.AZUL)
                }
            }
        }
        sideProjects.values.forEach { project ->
            project.tasks {
                jar {
                    from(project.sourceSets.mixins.output)
                }
                processResources {
                    exclude(mod.accessorConfigFileName)
                }
            }
        }
        sides.forEach { side ->
            when (side) {
                ModSide.CLIENT -> registerTask<TestClientModTask>()
                ModSide.SERVER -> registerTask<TestServerModTask>()
            } {
                dependsSequentiallyOn(
                    listOfNotNull(
                        tasks.findByName("prepareRun" + side.getName(PascalCase)),
                        tasks.findByName("run" + side.getName(PascalCase))
                    )
                )
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
}
