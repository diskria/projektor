package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.gradle.utils.extensions.getBuildFile
import io.github.diskria.gradle.utils.extensions.getFile
import io.github.diskria.gradle.utils.extensions.getFileNames
import io.github.diskria.gradle.utils.helpers.jvm.JvmArguments
import io.github.diskria.gradle.utils.helpers.jvm.Size
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPath
import io.github.diskria.kotlin.utils.extensions.common.`Title Case`
import io.github.diskria.kotlin.utils.extensions.common.buildPath
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.common.modifyUnless
import io.github.diskria.kotlin.utils.extensions.common.snake_case
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
import org.gradle.kotlin.dsl.named
import org.gradle.language.jvm.tasks.ProcessResources
import kotlin.collections.isNotEmpty
import kotlin.collections.map

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
        val minecraftVersionString = mod.minecraftVersion.asString()
        dependencies {
            minecraft("com.mojang", "minecraft", minecraftVersionString)
            modImplementation("net.fabricmc", "fabric-loader", loaderVersion)
            mappings("net.fabricmc", "yarn", "$minecraftVersionString+build.${mod.config.fabric.yarnBuild}", "v2")
            mod.config.fabric.apiVersion?.let { apiVersion ->
                modImplementation("net.fabricmc.fabric-api", "fabric-api", "$apiVersion+$minecraftVersionString")
            }
            modImplementation(
                "net.fabricmc",
                "fabric-language-kotlin",
                "${Versions.FABRIC_KOTLIN}+kotlin.${mod.kotlinVersion}"
            )
        }
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
                            runDir = buildPath("run", sideName)
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
            accessWidenerPath.set(file(buildPath("src", "main", "resources", fileName(mod.id, "accesswidener"))))
        }
        if (datagenClasses.isNotEmpty()) {
            loom {
                runs {
                    create("Data Generation") {
                        runDir = name.setCase(`Title Case`, `kebab-case`)
                        environment(ModSide.SERVER.getName())
                        vmArgs(
                            JvmArguments.property("fabric-api.datagen"),
                            JvmArguments.property(
                                "fabric-api.datagen.output-dir",
                                file("src/main/generated").toString()
                            ),
                            JvmArguments.property(
                                "fabric-api.datagen.modid",
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
        val generateFabricConfigTask = tasks.register("generateFabricConfig") {
            val generatedResourcesPath = buildPath("generated", "resources")
            val modConfigFile = getBuildFile(buildPath(generatedResourcesPath, getConfigFilePath()))
            val mixinsConfigFile = getBuildFile(buildPath(generatedResourcesPath, mod.mixinsConfigFileName))
            outputs.files(modConfigFile, mixinsConfigFile)
            doLast {
                modConfigFile.get().asFile.apply {
                    parentFile.mkdirs()
                    FabricModConfig.of(
                        mod = mod,
                        minecraftVersion = mod.minecraftVersion,
                        loaderVersion = loaderVersion,
                        isApiRequired = mod.config.fabric.isApiRequired,
                        datagenClasses = datagenClasses,
                    ).serialize(this)
                }
                mixinsConfigFile.get().asFile.apply {
                    parentFile.mkdirs()
                    MixinsConfig.of(
                        mod = mod,
                        mixins = mixins,
                    ).serialize(this)
                }
            }
        }
        tasks.named<ProcessResources>("processResources") {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            from(generateFabricConfigTask)
            from(rootProject.getFile(fileName("icon", Constants.File.Extension.PNG))) {
                into(buildPath("assets", mod.id))
            }
        }
    }
}
