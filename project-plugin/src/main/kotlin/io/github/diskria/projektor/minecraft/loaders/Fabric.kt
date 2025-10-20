package io.github.diskria.projektor.minecraft.loaders

import io.github.diskria.gradle.utils.extensions.getBuildFile
import io.github.diskria.gradle.utils.extensions.getFile
import io.github.diskria.gradle.utils.extensions.getFileNames
import io.github.diskria.gradle.utils.extensions.resolveCatalogVersion
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPath
import io.github.diskria.kotlin.utils.extensions.capitalizeFirstChar
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.common.modifyUnless
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.extensions.mappers.getName
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

    override fun configure(project: Project, mod: MinecraftMod) = with(project) {
        val loaderVersion = Versions.FABRIC_LOADER
        val mixins = mod.config.environment.getSourceSets().mapNotNull { sourceSet ->
            val sourceSetName = sourceSet.getName()
            getFileNames(
                "src/$sourceSetName/java/${mod.packagePath}/mixins".modifyUnless(sourceSet == SourceSet.MAIN) {
                    "$it/$sourceSetName"
                }
            ).toNullIfEmpty()?.let { sourceSet to it }
        }.toMap()
        val datagenClasses = getFileNames("src/datagen/kotlin/${mod.packagePath}").map {
            mod.packageName + Constants.Char.DOT + it
        }
        val minecraftVersionString = mod.minecraftVersion.getVersion()
        dependencies {
            minecraft("com.mojang:minecraft:$minecraftVersionString")
            modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

            val yarnVersion = resolveCatalogVersion("fabric-yarn") { "$minecraftVersionString+build.$it" }
            mappings("net.fabricmc:yarn:$yarnVersion:v2")

            modImplementation(
                "net.fabricmc:fabric-language-kotlin:${Versions.FABRIC_KOTLIN}+kotlin.${mod.kotlinVersion}"
            )

            if (mod.config.isFabricApiRequired) {
                val apiVersion = resolveCatalogVersion("fabric-api") { "$it+$minecraftVersionString" }
                modImplementation("net.fabricmc.fabric-api:fabric-api:$apiVersion")
            }
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
                            name = sideName.capitalizeFirstChar()
                            runDir = "run/$sideName"
                            when (side) {
                                ModSide.CLIENT -> client()
                                ModSide.SERVER -> server()
                            }
                            programArgs("--username", "${mod.repo.owner.developer}-$sideName")
                            vmArgs(side.getMinMemoryJvmArgument(), side.getMaxMemoryJvmArgument())
                        }
                    }
                }
            }
            accessWidenerPath.set(file("src/main/resources".appendPath(fileName(mod.id, "accesswidener"))))
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
                            "-Dfabric-api.datagen.modid=${mod.id}",
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
            val mixinsConfigFile = getBuildFile("generated/resources/${mod.mixinsConfigFileName}")
            outputs.files(modConfigFile, mixinsConfigFile)
            doLast {
                modConfigFile.get().asFile.apply {
                    parentFile.mkdirs()
                    FabricModConfig.of(
                        mod = mod,
                        minecraftVersion = mod.minecraftVersion,
                        loaderVersion = loaderVersion,
                        isApiRequired = mod.config.isFabricApiRequired,
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
                into("assets".appendPath(mod.id))
            }
        }
    }
}
