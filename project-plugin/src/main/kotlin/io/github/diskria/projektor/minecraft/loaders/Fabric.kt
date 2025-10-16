package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.gradle.utils.extensions.getBuildFile
import io.github.diskria.gradle.utils.extensions.getFile
import io.github.diskria.gradle.utils.extensions.getFileNames
import io.github.diskria.gradle.utils.extensions.resolveCatalogVersion
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.capitalizeFirstChar
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.extensions.serialization.serialize
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.minecraft.*
import io.github.diskria.projektor.minecraft.config.FabricModConfig
import io.github.diskria.projektor.minecraft.config.MixinsConfig
import io.github.diskria.projektor.minecraft.version.getVersion
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named
import org.gradle.language.jvm.tasks.ProcessResources

data object Fabric : ModLoader {

    override fun getConfigFilePath(): String =
        fileName(getName(), "mod", Constants.File.Extension.JSON)

    override fun configureMod(project: Project, minecraftMod: MinecraftMod) = with(project) {
        val loaderVersion = Versions.FABRIC_LOADER
        val mixins = minecraftMod.config.environment.getSourceSets().mapNotNull { sourceSet ->
            val logicalName = sourceSet.logicalName()
            val pathBase = "src/$logicalName/java/${minecraftMod.packagePath}/mixins"
            getFileNames(
                if (sourceSet == SourceSet.MAIN) pathBase
                else "$pathBase/$logicalName"
            ).toNullIfEmpty()?.let { sourceSet to it }
        }.toMap()
        val datagenClasses = getFileNames("src/datagen/kotlin/${minecraftMod.packagePath}").map {
            minecraftMod.packageName + Constants.Char.DOT + it
        }
        val minecraftVersionString = minecraftMod.minecraftVersion.getVersion()
        dependencies {
            minecraft("com.mojang:minecraft:$minecraftVersionString")
            modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

            val yarnVersion = resolveCatalogVersion("fabric-yarn") { "$minecraftVersionString+build.$it" }
            mappings("net.fabricmc:yarn:$yarnVersion:v2")

            modImplementation(
                "net.fabricmc:fabric-language-kotlin:${Versions.FABRIC_KOTLIN}+kotlin.${minecraftMod.kotlinVersion}"
            )

            if (minecraftMod.config.isFabricApiRequired) {
                val apiVersion = resolveCatalogVersion("fabric-api") { "$it+$minecraftVersionString" }
                modImplementation("net.fabricmc.fabric-api:fabric-api:$apiVersion")
            }
        }
        loom {
            splitEnvironmentSourceSets()
            mods {
                create(minecraftMod.id) {
                    sourceSets {
                        minecraftMod.config.environment.getSourceSets().forEach { sourceSet ->
                            sourceSet(getByName(sourceSet.logicalName()))
                        }
                    }
                }
            }
            runs {
                ModSide.entries.forEach { side ->
                    val sideName = side.logicalName()
                    named(sideName) {
                        val hasSide = minecraftMod.config.environment.sides.contains(side)
                        ideConfigGenerated(hasSide)
                        if (hasSide) {
                            name = sideName.capitalizeFirstChar()
                            runDir = "run/$sideName"
                            when (side) {
                                ModSide.CLIENT -> client()
                                ModSide.SERVER -> server()
                            }
                            programArgs("--username", "${minecraftMod.developer}-$sideName")
                            vmArgs(side.getMinMemoryJvmArgument(), side.getMaxMemoryJvmArgument())
                        }
                    }
                }
            }
            accessWidenerPath.set(file("src/main/resources/" + fileName(minecraftMod.id, "accesswidener")))
        }
        if (datagenClasses.isNotEmpty()) {
            loom {
                runs {
                    create("data") {
                        name = "Datagen"
                        runDir = "datagen"
                        environment("server")
                        vmArgs(
                            "-Dfabric-api.datagen",
                            "-Dfabric-api.datagen.output-dir=${file("src/main/generated")}",
                            "-Dfabric-api.datagen.modid=${minecraftMod.id}",
                            ModSide.SERVER.getMinMemoryJvmArgument(),
                            ModSide.SERVER.getMaxMemoryJvmArgument(),
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
            val modConfigFile = getBuildFile("generated/resources/${getConfigFilePath()}")
            val mixinsConfigFile = getBuildFile("generated/resources/${minecraftMod.mixinsConfigFileName}")
            outputs.files(modConfigFile, mixinsConfigFile)
            doLast {
                modConfigFile.get().asFile.apply {
                    parentFile.mkdirs()
                    FabricModConfig.of(
                        mod = minecraftMod,
                        minecraftVersion = minecraftMod.minecraftVersion,
                        loaderVersion = loaderVersion,
                        isApiRequired = minecraftMod.config.isFabricApiRequired,
                        datagenClasses = datagenClasses,
                    ).serialize(this)
                }
                mixinsConfigFile.get().asFile.apply {
                    parentFile.mkdirs()
                    MixinsConfig.of(
                        mod = minecraftMod,
                        mixins = mixins,
                    ).serialize(this)
                }
            }
        }
        tasks.named<ProcessResources>("processResources") {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            from(generateFabricConfigTask)
            from(rootProject.getFile(fileName("icon", Constants.File.Extension.PNG))) {
                into("assets/${minecraftMod.id}/")
            }
        }
    }
}
