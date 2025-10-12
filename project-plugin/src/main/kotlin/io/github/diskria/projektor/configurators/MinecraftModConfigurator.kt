package io.github.diskria.projektor.configurators

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.capitalizeFirstChar
import io.github.diskria.kotlin.utils.extensions.common.`Train-Case`
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.extensions.mappers.toEnum
import io.github.diskria.kotlin.utils.extensions.serialization.serialize
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.common.minecraft.ModLoader
import io.github.diskria.projektor.common.minecraft.getConfigFilePath
import io.github.diskria.projektor.configurations.MinecraftModConfiguration
import io.github.diskria.projektor.extensions.mappings
import io.github.diskria.projektor.extensions.minecraft
import io.github.diskria.projektor.extensions.modImplementation
import io.github.diskria.projektor.minecraft.*
import io.github.diskria.projektor.minecraft.config.FabricModConfig
import io.github.diskria.projektor.minecraft.config.MixinsConfig
import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import io.github.diskria.projektor.minecraft.version.getVersion
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.projekt.common.IProjekt
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.api.fabricapi.FabricApiExtension
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.language.jvm.tasks.ProcessResources

open class MinecraftModConfigurator(
    val config: MinecraftModConfiguration
) : Configurator<MinecraftMod>() {

    override fun configure(project: Project, projekt: IProjekt): MinecraftMod = with(project) {
        val modLoader = projectDir.parentFile.name.toEnum<ModLoader>()
        val minecraftVersion = MinecraftVersion.of(projectDir.name)
        val minecraftMod = MinecraftMod(projekt, config, modLoader, minecraftVersion)
        applyCommonConfiguration(project, minecraftMod)
        requirePlugins("org.jetbrains.kotlin.plugin.serialization")
        tasks.named<Jar>("jar") {
            manifest {
                val specificationVersion by 1.toString().autoNamedProperty(`Train-Case`)
                val specificationTitle by minecraftMod.id.autoNamedProperty(`Train-Case`)
                val specificationVendor by minecraftMod.developer.autoNamedProperty(`Train-Case`)

                val implementationVersion by minecraftMod.jarVersion.autoNamedProperty(`Train-Case`)
                val implementationTitle by name.autoNamedProperty(`Train-Case`)
                val implementationVendor by minecraftMod.developer.autoNamedProperty(`Train-Case`)

                attributes(
                    listOf(
                        specificationVersion,
                        specificationTitle,
                        specificationVendor,

                        implementationVersion,
                        implementationTitle,
                        implementationVendor,
                    ).associate { it.name to it.value }
                )
            }
        }
        when (minecraftMod.modLoader) {
            ModLoader.FABRIC -> configureFabricMod(project, minecraftMod)
            ModLoader.QUILT -> configureQuiltMod(project, minecraftMod)
            ModLoader.FORGE -> configureForgeMod(project, minecraftMod)
            ModLoader.NEOFORGE -> configureNeoForgeMod(project, minecraftMod)
        }
        return minecraftMod
    }

    private fun configureFabricMod(project: Project, minecraftMod: MinecraftMod) = with(project) {
        requirePlugins("fabric-loom")
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
        runExtension<LoomGradleExtensionAPI> {
            splitEnvironmentSourceSets()
            mods {
                create(minecraftMod.id) {
                    runExtension<SourceSetContainer> {
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
            runExtension<LoomGradleExtensionAPI> {
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
            runExtension<FabricApiExtension> {
                configureDataGeneration {
                    client = true
                }
            }
        }
        val generateFabricConfigTask = tasks.register("generateFabricConfig") {
            val modConfigFile = getBuildFile("generated/resources/${ModLoader.FABRIC.getConfigFilePath()}")
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

    private fun configureQuiltMod(project: Project, projekt: MinecraftMod) = with(project) {

    }

    private fun configureForgeMod(project: Project, projekt: MinecraftMod) = with(project) {

    }

    private fun configureNeoForgeMod(project: Project, projekt: MinecraftMod) = with(project) {

    }
}
