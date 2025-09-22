package io.github.diskria.projektor.extensions

import com.modrinth.minotaur.ModrinthExtension
import io.github.diskria.projektor.extensions.mappers.toJvmTarget
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.licenses.MitLicense
import io.github.diskria.projektor.minecraft.*
import io.github.diskria.projektor.minecraft.config.FabricModConfig
import io.github.diskria.projektor.minecraft.config.MixinsConfig
import io.github.diskria.projektor.minecraft.utils.ModrinthUtils
import io.github.diskria.projektor.minecraft.version.MinecraftVersion
import io.github.diskria.projektor.minecraft.version.getMinJavaVersion
import io.github.diskria.projektor.minecraft.version.getVersion
import io.github.diskria.projektor.owner.GithubProfile
import io.github.diskria.projektor.owner.domain.MinecraftDomain
import io.github.diskria.projektor.projekt.*
import io.github.diskria.utils.kotlin.Constants
import io.github.diskria.utils.kotlin.extensions.capitalizeFirstChar
import io.github.diskria.utils.kotlin.extensions.common.`Train-Case`
import io.github.diskria.utils.kotlin.extensions.generics.toNullIfEmpty
import io.github.diskria.utils.kotlin.extensions.mappers.toEnum
import io.github.diskria.utils.kotlin.properties.toAutoNamedProperty
import io.github.diskria.utils.kotlin.words.ScreamingSnakeCase
import kotlinx.serialization.json.Json
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.api.fabricapi.FabricApiExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.Copy
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources

fun <R> Project.fabric(block: LoomGradleExtensionAPI.() -> R): R =
    getExtensionOrThrow<LoomGradleExtensionAPI>().block()

fun <R> Project.fabricApi(block: FabricApiExtension.() -> R): R =
    getExtensionOrThrow<FabricApiExtension>().block()

fun <R> Project.modrinth(block: ModrinthExtension.() -> R): R =
    getExtensionOrThrow<ModrinthExtension>().block()

fun DependencyHandler.minecraft(dependencyNotation: Any): Dependency? =
    add("minecraft", dependencyNotation)

fun DependencyHandler.mappings(dependencyNotation: Any): Dependency? =
    add("mappings", dependencyNotation)

fun DependencyHandler.modImplementation(dependencyNotation: Any): Dependency? =
    add("modImplementation", dependencyNotation)

fun Project.configureMinecraftMod(
    environment: ModEnvironment,
    isFabricApiRequired: Boolean,
    modrinthProjectId: String,
    license: License = MitLicense,
): IProjekt {
    requirePlugins(
        "org.jetbrains.kotlin.plugin.serialization",
        "com.github.gmazzo.buildconfig",
    )
    val minecraftVersion = MinecraftVersion.of(projectDir.name)
    val jvmTarget = minecraftVersion.getMinJavaVersion().toJvmTarget()
    val mod = Projekt.of(this, MinecraftDomain, license, jvmTarget).toMinecraftMod(
        modLoader = projectDir.parentFile.name.toEnum<ModLoader>(),
        minecraftVersion = minecraftVersion,
        environment = environment,
        modrinthProjectUrl = ModrinthUtils.getProjectUrl(modrinthProjectId),
    )
    val artifactVersion = buildString {
        append(mod.modLoader.logicalName())
        append(Constants.Char.HYPHEN)
        append(mod.version)
        append(Constants.Char.PLUS)
        append(MinecraftDomain.suffix)
        append(minecraftVersion.getVersion())
    }
    configureBuildConfig(mod.packageName, "MinecraftMod") {
        val modId by mod.id.toAutoNamedProperty(ScreamingSnakeCase)
        val modName by mod.name.toAutoNamedProperty(ScreamingSnakeCase)
        listOf(modId, modName)
    }
    when (mod.modLoader) {
        ModLoader.FABRIC -> configureFabricMod(mod, isFabricApiRequired)
        ModLoader.QUILT -> configureQuiltMod(mod)
        else -> TODO()
    }
    tasks.named<Jar>("jar") {
        manifest {
            val specificationVersion by 1.toString().toAutoNamedProperty(`Train-Case`)
            val specificationTitle by mod.id.toAutoNamedProperty(`Train-Case`)
            val specificationVendor by GithubProfile.username.toAutoNamedProperty(`Train-Case`)

            val implementationVersion by artifactVersion.toAutoNamedProperty(`Train-Case`)
            val implementationTitle by mod.name.toAutoNamedProperty(`Train-Case`)
            val implementationVendor by GithubProfile.username.toAutoNamedProperty(`Train-Case`)

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
    configureProjekt(mod, artifactVersion = artifactVersion)
    configurePublishing(mod, PublishingTarget.MODRINTH)

    val mainJar = tasks.named<Jar>("jar")
    val unpackJar = tasks.register<Copy>("unpackJar") {
        dependsOn(mainJar)
        from(zipTree(mainJar.flatMap { it.archiveFile }))
        into(layout.buildDirectory.dir("mod-jar-unpacked"))
    }
    tasks.named("build") {
        finalizedBy(unpackJar)
    }
    return mod
}

fun Project.configureFabricMod(mod: MinecraftMod, isFabricApiRequired: Boolean) {
    requirePlugins("fabric-loom")
    val loaderVersion = Versions.FABRIC_LOADER
    val mixins = mod.environment
        .getSourceSets()
        .mapNotNull { sourceSet ->
            val logicalName = sourceSet.logicalName()
            val pathBase = "src/$logicalName/java/${mod.packagePath}/mixins"
            getFileNames(
                if (sourceSet == SourceSet.MAIN) pathBase
                else "$pathBase/$logicalName"
            ).toNullIfEmpty()?.let { sourceSet to it }
        }
        .toMap()
    val datagenClasses = getFileNames("src/datagen/kotlin/${mod.packagePath}").map {
        mod.packageName + Constants.Char.DOT + it
    }
    val minecraftVersion = mod.minecraftVersion.getVersion()
    dependencies {
        minecraft("com.mojang:minecraft:$minecraftVersion")
        modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

        val yarnVersion = resolveCatalogVersion("fabric-yarn") { "$minecraftVersion+build.$it" }
        mappings("net.fabricmc:yarn:$yarnVersion:v2")

        modImplementation("net.fabricmc:fabric-language-kotlin:${Versions.FABRIC_KOTLIN}+kotlin.${mod.kotlinVersion}")

        if (isFabricApiRequired) {
            val apiVersion = resolveCatalogVersion("fabric-api") { "$it+$minecraftVersion" }
            modImplementation("net.fabricmc.fabric-api:fabric-api:$apiVersion")
        }
    }
    fabric {
        splitEnvironmentSourceSets()
        mods {
            create(mod.id) {
                sourceSets {
                    mod.environment.getSourceSets().forEach { sourceSet ->
                        sourceSet(getByName(sourceSet.logicalName()))
                    }
                }
            }
        }
        runs {
            ModSide.entries.forEach { side ->
                val logicalName = side.logicalName()
                named(logicalName) {
                    val hasSide = mod.environment.sides.contains(side)
                    ideConfigGenerated(hasSide)

                    if (hasSide) {
                        name = logicalName.capitalizeFirstChar()
                        runDir = "run/$logicalName"
                        when (side) {
                            ModSide.CLIENT -> client()
                            ModSide.SERVER -> server()
                        }
                        programArgs("--username", "${GithubProfile.username}-$logicalName")
                        vmArgs("-Xms2G", side.getMaxMemoryJvmArgument())
                    }
                }
            }
        }
        accessWidenerPath.set(file("src/main/resources/${mod.id}.accesswidener"))
    }
    if (datagenClasses.isNotEmpty()) {
        fabric {
            runs {
                create("data") {
                    name = "Datagen"
                    runDir = "datagen"
                    environment("server")
                    vmArgs(
                        "-Dfabric-api.datagen",
                        "-Dfabric-api.datagen.output-dir=${file("src/main/generated")}",
                        "-Dfabric-api.datagen.modid=${mod.id}",
                        "-Xms2G",
                        ModSide.SERVER.getMaxMemoryJvmArgument(),
                    )
                }
            }
        }
        fabricApi {
            configureDataGeneration {
                client = true
            }
        }
    }
    val generateFabricConfigTask by tasks.registering {
        val modConfigFile = buildFile("generated/resources/${ModLoader.FABRIC.getConfigFilePath()}")
        val mixinsConfigFile = buildFile("generated/resources/${mod.mixinsConfigFileName}")
        outputs.files(modConfigFile, mixinsConfigFile)
        doLast {
            modConfigFile.get().asFile.apply {
                parentFile.mkdirs()
                writeText(
                    Json { prettyPrint = true }.encodeToString(
                        FabricModConfig.of(
                            mod = mod,
                            minecraftVersion = mod.minecraftVersion,
                            loaderVersion = loaderVersion,
                            isApiRequired = isFabricApiRequired,
                            datagenClasses = datagenClasses,
                        )
                    )
                )
            }
            mixinsConfigFile.get().asFile.apply {
                parentFile.mkdirs()
                writeText(
                    Json { prettyPrint = true }.encodeToString(
                        MixinsConfig.of(
                            mod = mod,
                            mixins = mixins,
                        )
                    )
                )
            }
        }
    }
    tasks.named<ProcessResources>("processResources") {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(generateFabricConfigTask)

        from(rootFile("icon.png")) {
            into("assets/${mod.slug}/")
        }
    }
}

fun Project.configureQuiltMod(mod: MinecraftMod) {

}

fun Project.configureModrinthPublishing(project: IProjekt) {
    requirePlugins("com.modrinth.minotaur")
    modrinth {
        projectId.set(project.slug)
    }
}
