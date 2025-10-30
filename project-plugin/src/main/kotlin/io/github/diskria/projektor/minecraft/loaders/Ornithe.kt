package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.gradle.utils.helpers.jvm.Size
import io.github.diskria.kotlin.shell.dsl.git.commits.CommitType
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPath
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.buildPath
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.common.snake_case
import io.github.diskria.kotlin.utils.extensions.ensureDirectoryExists
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.common.minecraft.versions.Release
import io.github.diskria.projektor.common.minecraft.versions.common.*
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.minecraft.ModSide
import io.github.diskria.projektor.minecraft.getSourceSets
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.tasks.minecraft.drift.common.OrnitheDriftTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModConfigTask
import io.github.diskria.projektor.tasks.minecraft.generate.GenerateModMixinsConfigTask
import io.github.diskria.projektor.tasks.minecraft.test.TestClientModTask
import io.github.diskria.projektor.tasks.minecraft.test.TestServerModTask
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import java.io.File

data object Ornithe : ModLoader {

    override val supportedVersionRange: MinecraftVersionRange =
        MinecraftVersionRange(MinecraftVersion.EARLIEST, Release.V_1_13_2)

    override fun getConfigFilePath(): String =
        Fabric.getConfigFilePath()

    override fun configure(project: Project, mod: MinecraftMod) = with(project) {
        val minSupportedVersion = mod.minSupportedVersion
        dependencies {
            minecraft("com.mojang", "minecraft", minSupportedVersion.asString())
            modImplementation("net.fabricmc", "fabric-loader", Versions.FABRIC_LOADER)

            ploceus {
                dependOsl("0.16.3")
                mappings(featherMappings(minSupportedVersion.getLatestOrnitheFeatherVersion(project)))
            }

            /*
            clientExceptions(ploceus.raven("2", "client"))
            serverExceptions(ploceus.raven("2", "server"))


            clientNests(ploceus.nests("7", "client"))
            serverNests(ploceus.nests("4", "server"))
            */
        }
        fabric {
            configureDataGeneration {
                client = true
            }
        }
        loom {
            if (minSupportedVersion.supportsEnvironmentSplit()) {
                splitEnvironmentSourceSets()
                mods {
                    create(mod.id) {
                        sourceSets {
                            mod.config.environment.getSourceSets(minSupportedVersion).forEach {
                                sourceSet(getByName(it.getName()))
                            }
                        }
                    }
                }
            }
            runs {
                val datagenName = "datagen"
                named(datagenName) {
                    inherit(findByName(ModSide.SERVER.getName()))

                    name = "Data Generation"
                    runDir = "build/$datagenName"

                    val argumentNamePrefix = "fabric-api.$datagenName"
                    vmArgs(
                        JvmArguments.property(argumentNamePrefix),
                        JvmArguments.property(
                            "$argumentNamePrefix.output-dir",
                            file("src/main/generated").toString()
                        ),
                        JvmArguments.property(
                            "$argumentNamePrefix.modid",
                            mod.id
                        ),
                        *JvmArguments.memory(
                            ModSide.SERVER.minMemoryGigabytes..ModSide.SERVER.maxMemoryGigabytes,
                            Size.GIGABYTES
                        ),
                    )
                }
                ModSide.entries.forEach { side ->
                    val sideName = side.getName()
                    named(sideName) {
                        val hasSide = mod.config.environment.sides.contains(side)
                        ideConfigGenerated(hasSide)
                        if (hasSide) {
                            name = sideName.setCase(snake_case, `Title Case`)
                            runDir = MinecraftMod.RUN_DIRECTORY_NAME.appendPath(sideName)
                            when (side) {
                                ModSide.CLIENT -> {
                                    client()
                                    programArgs(
                                        *JvmArguments.program(
                                            "username",
                                            mod.repo.owner.developer + Constants.Char.HYPHEN + sideName
                                        ),
                                        *JvmArguments.program(
                                            "userProperties",
                                            "{}"
                                        ),
                                    )
                                }

                                ModSide.SERVER -> server()
                            }
                            vmArgs(
                                *JvmArguments.memory(
                                    side.minMemoryGigabytes..side.maxMemoryGigabytes,
                                    Size.GIGABYTES
                                ),
                            )
                        }
                    }
                }
            }
            accessWidenerPath = ensureAccessWidenerCreated(project, mod)
        }
        tasks {
            val generatedResourcesDirectory = project.getBuildDirectory("generated/resources").get().asFile
            val generateMixinsConfigTask = registerTask<GenerateModMixinsConfigTask> {
                minecraftMod.set(mod)
                sourceSetsRoot.set(project.getDirectory("src"))
                outputFile.set(generatedResourcesDirectory.resolve(mod.mixinsConfigFileName))
            }
            val generateModConfigTask = registerTask<GenerateModConfigTask> {
                minecraftMod.set(mod)
                sourceSetsRoot.set(project.getDirectory("src"))
                outputFile.set(generatedResourcesDirectory.resolve(Fabric.getConfigFilePath()))
            }
            processResources {
                dependsOn(generateMixinsConfigTask, generateModConfigTask)
                from(generateMixinsConfigTask, generateModConfigTask)
                from(rootProject.getFile(fileName("icon", Constants.File.Extension.PNG))) {
                    into(buildPath("assets", mod.id))
                }
            }
            registerTask<TestClientModTask> {
                minecraftMod.set(mod)
            }
            val hasServerSide = mod.config.environment.sides.contains(ModSide.SERVER)
            val runDirectory = project.getDirectory(MinecraftMod.RUN_DIRECTORY_NAME).asFile.ensureDirectoryExists()
            if (hasServerSide) {
                val eulaName = "eula"
                val serverName = ModSide.SERVER.getName()
                val serverDirectory = runDirectory.resolve(serverName)

                val eulaFile = serverDirectory.resolve(fileName(eulaName, Constants.File.Extension.TXT))
                if (!eulaFile.exists()) {
                    eulaFile.ensureFileExists().writeText("$eulaName=${true}")
                }

                val propertiesFile = serverDirectory.resolve(fileName(serverName, Constants.File.Extension.PROPERTIES))
                if (!propertiesFile.exists()) {
                    propertiesFile.writeText("online-mode=${false}")
                }

                registerTask<TestServerModTask> {
                    minecraftMod.set(mod)
                }
            }
        }
        rootProject.ensureTaskRegistered<OrnitheDriftTask>()
    }

    private fun ensureAccessWidenerCreated(project: Project, mod: MinecraftMod): File {
        val file = project.getFile(buildPath("src", "main", "resources", fileName(mod.id, "accesswidener"))).asFile
        if (!file.exists()) {
            file.ensureFileExists().writeText(buildString {
                appendLine("accessWidener v2 named")
                appendLine()
                appendLine("# region classes")
                appendLine()
                appendLine("# endregion classes")
                appendLine()
                appendLine("# region methods")
                appendLine()
                appendLine("# endregion methods")
                appendLine()
                appendLine("# region fields")
                appendLine()
                appendLine("# endregion fields")
            })
            mod.repo.pushFile(project.layout.projectDirectory.asFile, CommitType.CHORE, file, false)
        }
        return file
    }
}
