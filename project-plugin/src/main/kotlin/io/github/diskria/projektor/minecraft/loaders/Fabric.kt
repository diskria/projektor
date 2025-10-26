package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.gradle.utils.extensions.getBuildFile
import io.github.diskria.gradle.utils.extensions.getFile
import io.github.diskria.gradle.utils.extensions.getFileNames
import io.github.diskria.gradle.utils.extensions.processResources
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.gradle.utils.helpers.jvm.Size
import io.github.diskria.kotlin.shell.dsl.git.commits.CommitType
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPath
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.buildPath
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.common.modifyUnless
import io.github.diskria.kotlin.utils.extensions.common.snake_case
import io.github.diskria.kotlin.utils.extensions.ensureFileExists
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.serialization.serialize
import io.github.diskria.kotlin.utils.extensions.setCase
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.minecraft.ModSide
import io.github.diskria.projektor.minecraft.SourceSet
import io.github.diskria.projektor.minecraft.config.FabricModConfig
import io.github.diskria.projektor.minecraft.config.MixinsConfig
import io.github.diskria.projektor.minecraft.getSourceSets
import io.github.diskria.projektor.minecraft.version.asString
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import java.io.File
import kotlin.collections.isNotEmpty
import kotlin.collections.map
import kotlin.text.appendLine
import kotlin.text.buildString

data object Fabric : ModLoader {

    override fun getConfigFilePath(): String =
        fileName(getName(), "mod", Constants.File.Extension.JSON)

    override fun configure(project: Project, mod: MinecraftMod) = with(project) {
        val loaderVersion = Versions.FABRIC_LOADER
        val mixins = mod.config.environment.getSourceSets().mapNotNull { sourceSet ->
            val sourceSetName = sourceSet.getName()
            val mixinsPath = buildPath("src", sourceSetName, "java", mod.packagePath, "mixins")
                .modifyUnless(sourceSet == SourceSet.MAIN) { it.appendPath(sourceSetName) }
            getFileNames(mixinsPath).toNullIfEmpty()?.let { sourceSet to it }
        }.toMap()
        val datagenClasses = getFileNames(buildPath("src", "datagen", "kotlin", mod.packagePath))
            .map { mod.packageName + Constants.Char.DOT + it }
        val minMinecraftVersionString = mod.supportedVersionRange.min.asString()
        loom {
            splitEnvironmentSourceSets()
            mods {
                create(mod.id) {
                    sourceSets {
                        mod.config.environment.getSourceSets().forEach {
                            sourceSet(getByName(it.getName()))
                        }
                    }
                }
            }
            runs {
                ModSide.entries.forEach { side ->
                    val sideName = side.getName()
                    named(sideName) {
                        val hasSide = mod.config.environment.sides.contains(side)
                        ideConfigGenerated(hasSide)
                        if (hasSide) {
                            name = sideName.setCase(snake_case, `Title Case`)
                            runDir = buildPath(MinecraftMod.SHORT_NAME, sideName)
                            when (side) {
                                ModSide.CLIENT -> {
                                    client()
                                    programArgs(
                                        *JvmArguments.program(
                                            "username",
                                            mod.repo.owner.developer + Constants.Char.HYPHEN + sideName
                                        )
                                    )
                                }

                                ModSide.SERVER -> server()
                            }
                            vmArgs(
                                *JvmArguments.memory(
                                    side.minMemoryGigabytes..side.maxMemoryGigabytes,
                                    Size.GIGABYTES
                                ),
                                *JvmArguments.program("enable-native-access", "ALL-UNNAMED"),
                            )
                        }
                    }
                }
            }
            accessWidenerPath = ensureAccessWidenerCreated(project, mod)
        }
        if (datagenClasses.isNotEmpty()) {
            loom {
                runs {
                    create("Data Generation") {
                        runDir = name.setCase(`Title Case`, `kebab-case`)
                        environment(ModSide.SERVER.getName())

                        val argumentNamePrefix = "fabric-api.datagen"
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
                            *JvmArguments.program("enable-native-access", "ALL-UNNAMED"),
                        )
                    }
                }
            }
            fabric {
                configureDataGeneration {
                    client = true
                }
            }
        }
        dependencies {
            minecraft("com.mojang", "minecraft", minMinecraftVersionString)
            modImplementation("net.fabricmc", "fabric-loader", loaderVersion)
            mappings("net.fabricmc", "yarn", "$minMinecraftVersionString+build.${mod.config.fabric.yarnBuild}", "v2")
            mod.config.fabric.apiVersion?.let { apiVersion ->
                modImplementation("net.fabricmc.fabric-api", "fabric-api", "$apiVersion+$minMinecraftVersionString")
            }
            modImplementation(
                "net.fabricmc",
                "fabric-language-kotlin",
                "${Versions.FABRIC_KOTLIN}+kotlin.${mod.kotlinVersion}"
            )
        }
        tasks {
            val generatedResourcesPath = buildPath("generated", "resources")
            val generateFabricConfigTask = register("generateFabricConfig") {
                val modConfigFile = getBuildFile(buildPath(generatedResourcesPath, getConfigFilePath()))
                outputs.files(modConfigFile)
                doLast {
                    modConfigFile.get().asFile.apply {
                        parentFile.mkdirs()
                        FabricModConfig.of(
                            mod = mod,
                            minSupportedVersion = mod.supportedVersionRange.min,
                            loaderVersion = loaderVersion,
                            isApiRequired = mod.config.fabric.isApiRequired,
                            datagenClasses = datagenClasses,
                        ).serialize(this)
                    }
                }
            }
            val generateMixinsConfigTask = register("generateMixinsConfig") {
                val mixinsConfigFile = getBuildFile(buildPath(generatedResourcesPath, mod.mixinsConfigFileName))
                outputs.files(mixinsConfigFile)
                doLast {
                    mixinsConfigFile.get().asFile.apply {
                        parentFile.mkdirs()
                        MixinsConfig.of(
                            mod = mod,
                            mixins = mixins,
                        ).serialize(this)
                    }
                }
            }
            processResources {
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
                from(generateFabricConfigTask, generateMixinsConfigTask)
                from(rootProject.getFile(fileName("icon", Constants.File.Extension.PNG))) {
                    into(buildPath("assets", mod.id))
                }
            }
        }
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
