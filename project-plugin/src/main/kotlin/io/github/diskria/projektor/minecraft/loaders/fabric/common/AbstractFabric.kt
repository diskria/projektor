package io.github.diskria.projektor.minecraft.loaders.fabric.common

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.gradle.utils.helpers.jvm.Size
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.ensureDirectoryExists
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.mappers.toEnumOrNull
import io.github.diskria.kotlin.utils.words.PascalCase
import io.github.diskria.projektor.common.ProjectDirectories
import io.github.diskria.projektor.common.minecraft.MinecraftConstants
import io.github.diskria.projektor.common.minecraft.era.common.MappingsEra
import io.github.diskria.projektor.common.minecraft.era.common.MinecraftEra
import io.github.diskria.projektor.common.minecraft.sides.ModSide
import io.github.diskria.projektor.common.minecraft.versions.asString
import io.github.diskria.projektor.common.minecraft.versions.getMappingsEra
import io.github.diskria.projektor.common.minecraft.versions.getMinJavaVersion
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.helpers.AccessWidenerHelper
import io.github.diskria.projektor.minecraft.loaders.common.ModLoader
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModEntryPointTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModMixinsConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.RemapShadowJarTask
import io.github.diskria.projektor.tasks.minecraft.test.TestClientModTask
import io.github.diskria.projektor.tasks.minecraft.test.TestServerModTask
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.*

abstract class AbstractFabric(
    val isOrnithe: Boolean = false,
    val isLegacy: Boolean = false,
) : ModLoader() {

    override fun configure(modProject: Project, mod: MinecraftMod) = with(modProject) {
        val mappingsEra = mod.minecraftVersion.getMappingsEra()

        subprojects {
            val side = name.toEnumOrNull<ModSide>() ?: return@subprojects
            val sideName = side.getName()
            log("[Crafter] Configuring $sideName side...")

            ensureKotlinPluginsApplied()

            val generateModEntryPointTask = registerTask<GenerateModEntryPointTask> {
                minecraftMod = mod
                modSide = side
                outputDirectory = getBuildDirectory("generated/sources/crafter")
            }
            val (main, mixins) = configureSourceSets(
                sourceSets = sourceSets,
                mainSources = listOf(generateModEntryPointTask.map { it.outputDirectory })
            )
            fabric {
                if (mappingsEra != MappingsEra.MERGED) {
                    when (side) {
                        ModSide.CLIENT -> clientOnlyMinecraftJar()
                        ModSide.SERVER -> serverOnlyMinecraftJar()
                    }
                }
                runs {
                    named(side.getName()) {
                        name = side.getName(`Title Case`)
                        runDir = ProjectDirectories.MINECRAFT_RUN
                        source(mixins)
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
                        )
                        if (mod.minecraftVersion.getEra() < MinecraftEra.ALPHA) {
                            vmArgs(
                                JvmArguments.property("fabric.gameVersion", mod.minecraftVersion.asString()),
                            )
                        }
                        if (side == ModSide.CLIENT) {
                            programArgs(
                                *JvmArguments.program(
                                    "username",
                                    mod.repo.owner.developer + MinecraftConstants.PLAYER_NAME_DEVELOPER_SUFFIX
                                ),
                                *JvmArguments.program("userProperties", "{}"),
                            )
                        }
                    }
                }
                accessWidenerPath = main.resourcesDirectory.resolve(mod.accessorConfigFileName).ensureFileExists {
                    writeText(AccessWidenerHelper.TEMPLATE)
                }
            }
            restoreDependencyResolutionRepositories()
            dependencies {
                minecraft("com.mojang", "minecraft", mod.minecraftVersion.asString())
                val loaderVersion = when {
                    isOrnithe -> mod.config.ornithe.loader
                    isLegacy -> mod.config.legacyFabric.loader
                    else -> mod.config.fabric.loader
                }
                modImplementation("net.fabricmc", "fabric-loader", loaderVersion)

                when {
                    isOrnithe -> {
                        ornithe {
                            if (mappingsEra != MappingsEra.MERGED) {
                                @Suppress("DEPRECATION")
                                when (side) {
                                    ModSide.CLIENT -> clientOnlyMappings()
                                    ModSide.SERVER -> serverOnlyMappings()
                                }
                            }
                            mappings(featherMappings(mod.config.ornithe.feather))
                        }
                    }

                    isLegacy -> {
                        legacyFabric {
                            mappings(yarn(mod.config.legacyFabric.yarnMinecraft, mod.config.legacyFabric.yarnMappings))
                        }
                    }

                    else -> {
                        fabric {
                            mappings("net.fabricmc", "yarn", mod.config.fabric.yarn, "v2")
                        }
                    }
                }
            }
            val runDirectory = projectDirectory.resolve(ProjectDirectories.MINECRAFT_RUN).ensureDirectoryExists()
            if (side == ModSide.SERVER) {
                runDirectory.resolve(fileName("eula", Constants.File.Extension.TXT)).ensureFileExists {
                    writeText("$nameWithoutExtension=${true}")
                }
                runDirectory.resolve(fileName("server", Constants.File.Extension.PROPERTIES)).ensureFileExists {
                    writeText("online-mode=${false}")
                }
            }
            tasks {
                configureJvmTarget(mod.jvmTarget)
                withType<JavaCompile>().configureEach {
                    options.encoding = Charsets.UTF_8.toString()
                }
                jar {
                    from(mixins.output)
                }
                named<JavaExec>("run" + side.getName(PascalCase)) {
                    val shadowJarTask = modProject.tasks.shadowJar.get()
                    dependsOn(shadowJarTask)
                    addToClasspath(shadowJarTask.archiveFile)

                    javaLauncher = this@subprojects.getExtension<JavaToolchainService>().launcherFor {
                        val javaVersion = mod.minecraftVersion.getMinJavaVersion()
                        configureJavaVendor(javaVersion, JvmVendorSpec.ADOPTIUM, JvmVendorSpec.AZUL)
                    }
                }
                processResources {
                    exclude(mod.accessorConfigFileName)
                }
            }
            registerTask<RemapShadowJarTask> {
                val shadowJarTask = modProject.tasks.shadowJar.get()
                dependsOn(shadowJarTask)
                inputFile.set(shadowJarTask.archiveFile)

                destinationDirectory = modProject.getBuildDirectory("libs")
                copyArchiveNameParts(shadowJarTask, classifier = Constants.Char.EMPTY)
            }
        }
        val generateMixinsConfigTask = registerTask<GenerateModMixinsConfigTask> {
            minecraftMod.set(mod)
            outputFile.set(getTempFile(mod.mixinsConfigFileName))
        }
        val generateModConfigTask = registerTask<GenerateModConfigTask> {
            minecraftMod.set(mod)
            outputFile.set(getTempFile(mod.configFileName))
        }
        tasks {
            build {
                dependsOn(children.first().getTask<RemapShadowJarTask>())
            }
            processResources {
                copyTaskOutput(generateMixinsConfigTask, mod.assetsPath)
                copyFile(rootProject.getFile(mod.iconFileName).asFile, mod.assetsPath)
                copyTaskOutput(generateModConfigTask)
            }
        }
        mod.config.environment.sides.forEach {
            when (it) {
                ModSide.CLIENT -> modProject.registerTask<TestClientModTask>()
                ModSide.SERVER -> modProject.registerTask<TestServerModTask>()
            }
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
