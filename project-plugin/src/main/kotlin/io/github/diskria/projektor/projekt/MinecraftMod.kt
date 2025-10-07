package io.github.diskria.projektor.projekt

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.capitalizeFirstChar
import io.github.diskria.kotlin.utils.extensions.common.SCREAMING_SNAKE_CASE
import io.github.diskria.kotlin.utils.extensions.common.`Train-Case`
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.mappers.toEnum
import io.github.diskria.kotlin.utils.extensions.serialization.serialize
import io.github.diskria.kotlin.utils.poet.Property
import io.github.diskria.kotlin.utils.properties.toAutoNamedProperty
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.extensions.mappings
import io.github.diskria.projektor.extensions.minecraft
import io.github.diskria.projektor.extensions.modImplementation
import io.github.diskria.projektor.minecraft.*
import io.github.diskria.projektor.minecraft.config.FabricModConfig
import io.github.diskria.projektor.minecraft.config.MixinsConfig
import io.github.diskria.projektor.minecraft.utils.ModrinthUtils
import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import io.github.diskria.projektor.minecraft.version.getMinJavaVersion
import io.github.diskria.projektor.minecraft.version.getVersion
import io.github.diskria.projektor.projekt.common.AbstractProjekt
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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import kotlin.properties.Delegates

open class MinecraftMod(
    projekt: IProjekt,
    project: Project
) : AbstractProjekt(projekt, project), IProjekt by projekt {

    val id: String = projekt.repo
    val mixinsConfigFileName: String = fileName(id, "mixins", Constants.File.Extension.JSON)
    val minecraftVersion: MinecraftVersion = MinecraftVersion.of(project.projectDir.name)
    val modLoader: ModLoader = project.projectDir.parentFile.name.toEnum<ModLoader>()

    val modrinthProjectUrl: String
        get() = ModrinthUtils.getProjectUrl(modrinthProjectId)

    var modrinthProjectId: String by Delegates.notNull()
    var environment: ModEnvironment by Delegates.notNull()
    var isFabricApiRequired: Boolean by Delegates.notNull()

    override fun getJvmTarget(): JvmTarget =
        minecraftVersion.getMinJavaVersion().toJvmTarget()

    override fun getJarVersion(): String =
        buildString {
            append(modLoader.getName())
            append(Constants.Char.HYPHEN)
            append(version)
            append(Constants.Char.PLUS)
            append(SHORT_MINECRAFT_NAME)
            append(minecraftVersion.getVersion())
        }

    override fun configureProject() = with(project) {
        requirePlugins("org.jetbrains.kotlin.plugin.serialization")
        tasks.named<Jar>("jar") {
            manifest {
                val specificationVersion by 1.toString().toAutoNamedProperty(`Train-Case`)
                val specificationTitle by id.toAutoNamedProperty(`Train-Case`)
                val specificationVendor by developer.toAutoNamedProperty(`Train-Case`)

                val implementationVersion by getJarVersion().toAutoNamedProperty(`Train-Case`)
                val implementationTitle by name.toAutoNamedProperty(`Train-Case`)
                val implementationVendor by developer.toAutoNamedProperty(`Train-Case`)

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
        when (modLoader) {
            ModLoader.FABRIC -> configureFabricMod(this@MinecraftMod)
            ModLoader.QUILT -> configureQuiltMod(this@MinecraftMod)
            ModLoader.FORGE -> configureForgeMod(this@MinecraftMod)
            ModLoader.NEOFORGE -> configureNeoForgeMod(this@MinecraftMod)
        }
    }

    private fun configureFabricMod(mod: MinecraftMod) = with(project) {
        requirePlugins("fabric-loom")
        val loaderVersion = Versions.FABRIC_LOADER
        val mixins = environment.getSourceSets().mapNotNull { sourceSet ->
            val logicalName = sourceSet.logicalName()
            val pathBase = "src/$logicalName/java/${getPackagePath()}/mixins"
            getFileNames(
                if (sourceSet == SourceSet.MAIN) pathBase
                else "$pathBase/$logicalName"
            ).toNullIfEmpty()?.let { sourceSet to it }
        }.toMap()
        val datagenClasses = getFileNames("src/datagen/kotlin/${getPackagePath()}").map {
            getPackageName() + Constants.Char.DOT + it
        }
        val minecraftVersionString = minecraftVersion.getVersion()
        dependencies {
            minecraft("com.mojang:minecraft:$minecraftVersionString")
            modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

            val yarnVersion = resolveCatalogVersion("fabric-yarn") { "$minecraftVersionString+build.$it" }
            mappings("net.fabricmc:yarn:$yarnVersion:v2")

            modImplementation(
                "net.fabricmc:fabric-language-kotlin:${Versions.FABRIC_KOTLIN}+kotlin.$kotlinVersion"
            )

            if (isFabricApiRequired) {
                val apiVersion = resolveCatalogVersion("fabric-api") { "$it+$minecraftVersionString" }
                modImplementation("net.fabricmc.fabric-api:fabric-api:$apiVersion")
            }
        }
        runExtension<LoomGradleExtensionAPI> {
            splitEnvironmentSourceSets()
            mods {
                create(id) {
                    runExtension<SourceSetContainer> {
                        environment.getSourceSets().forEach { sourceSet ->
                            sourceSet(getByName(sourceSet.logicalName()))
                        }
                    }
                }
            }
            runs {
                ModSide.entries.forEach { side ->
                    val sideName = side.logicalName()
                    named(sideName) {
                        val hasSide = mod.environment.sides.contains(side)
                        ideConfigGenerated(hasSide)
                        if (hasSide) {
                            name = sideName.capitalizeFirstChar()
                            runDir = "run/$sideName"
                            when (side) {
                                ModSide.CLIENT -> client()
                                ModSide.SERVER -> server()
                            }
                            programArgs("--username", "$developer-$sideName")
                            vmArgs(side.getMinMemoryJvmArgument(), side.getMaxMemoryJvmArgument())
                        }
                    }
                }
            }
            accessWidenerPath.set(file("src/main/resources/" + fileName(id, "accesswidener")))
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
                            "-Dfabric-api.datagen.modid=$id",
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
            val mixinsConfigFile = getBuildFile("generated/resources/$mixinsConfigFileName")
            outputs.files(modConfigFile, mixinsConfigFile)
            doLast {
                modConfigFile.get().asFile.apply {
                    parentFile.mkdirs()
                    FabricModConfig.of(
                        mod = mod,
                        minecraftVersion = minecraftVersion,
                        loaderVersion = loaderVersion,
                        isApiRequired = isFabricApiRequired,
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
                into("assets/$id/")
            }
        }
    }

    private val configureQuiltMod: Project.(MinecraftMod) -> Unit = { mod ->
        TODO()
    }

    private val configureForgeMod: Project.(MinecraftMod) -> Unit = { mod ->
        TODO()
    }

    private val configureNeoForgeMod: Project.(MinecraftMod) -> Unit = { mod ->
        TODO()
    }

    override fun getMetadata(): List<Property<String>> {
        val modId by id.toAutoNamedProperty(SCREAMING_SNAKE_CASE)
        val modName by name.toAutoNamedProperty(SCREAMING_SNAKE_CASE)
        return listOf(modId, modName)
    }

    companion object {
        private const val SHORT_MINECRAFT_NAME: String = "mc"
    }
}
