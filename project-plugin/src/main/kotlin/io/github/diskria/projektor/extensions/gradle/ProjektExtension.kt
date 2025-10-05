package io.github.diskria.projektor.extensions.gradle

import com.github.gmazzo.buildconfig.BuildConfigExtension
import io.github.diskria.gradle.utils.extensions.gradle.ProjectExtension
import io.github.diskria.gradle.utils.extensions.kotlin.*
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.kotlin.utils.extensions.capitalizeFirstChar
import io.github.diskria.kotlin.utils.extensions.common.`Train-Case`
import io.github.diskria.kotlin.utils.extensions.common.fileName
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.kotlin.utils.extensions.serialization.serialize
import io.github.diskria.kotlin.utils.properties.toAutoNamedProperty
import io.github.diskria.projektor.extensions.kotlin.mappers.toInt
import io.github.diskria.projektor.extensions.kotlin.mappings
import io.github.diskria.projektor.extensions.kotlin.minecraft
import io.github.diskria.projektor.extensions.kotlin.modImplementation
import io.github.diskria.projektor.extensions.kotlin.toProjekt
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.minecraft.*
import io.github.diskria.projektor.minecraft.config.FabricModConfig
import io.github.diskria.projektor.minecraft.config.MixinsConfig
import io.github.diskria.projektor.minecraft.version.getVersion
import io.github.diskria.projektor.projekt.*
import io.github.diskria.projektor.publishing.PublishingTarget
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.api.fabricapi.FabricApiExtension
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import javax.inject.Inject

open class ProjektExtension @Inject constructor(objects: ObjectFactory) : ProjectExtension() {

    val license: Property<License> = objects.property(License::class.java)
    val publishingTarget: Property<PublishingTarget> = objects.property(PublishingTarget::class.java)

    fun gradlePlugin(block: GradlePlugin.() -> Unit = {}): GradlePlugin = script {
        val plugin = buildProjekt().toGradlePlugin().apply(block)
        applyCommonConfiguration(plugin)
        runExtension<GradlePluginDevelopmentExtension> {
            website.set(plugin.getRepoUrl())
            vcsUrl.set(plugin.getRepoPath(true))
            plugins {
                create(plugin.id) {
                    id = plugin.id
                    implementationClass = plugin.packageName.appendPackageName(plugin.classNameBase + "GradlePlugin")
                    println("id: $id")
                    println("implementationClass: $implementationClass")
                    println("plugin.classNameBase: ${plugin.classNameBase}")

                    displayName = plugin.name
                    description = plugin.description

                    tags.set(plugin.tags.toNullIfEmpty())
                }
            }
        }
        return@script plugin
    }

    fun kotlinLibrary(block: KotlinLibrary.() -> Unit = {}): KotlinLibrary = script {
        val library = buildProjekt().toKotlinLibrary().apply(block)
        applyCommonConfiguration(library)
        dependencies {
            testImplementation(kotlin("test"))
            testImplementation("org.junit.jupiter:junit-jupiter:${Versions.JUNIT}")
        }
        tasks.named<Test>("test") {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
                exceptionFormat = TestExceptionFormat.FULL
                showStandardStreams = true
            }
        }
        return@script library
    }

    fun androidLibrary(block: AndroidLibrary.() -> Unit = {}): AndroidLibrary = script {
        val library = buildProjekt().toAndroidLibrary().apply(block)
        applyCommonConfiguration(library)
        return@script library
    }

    fun androidApplication(block: AndroidApplication.() -> Unit = {}): AndroidApplication = script {
        val application = buildProjekt().toAndroidApplication().apply(block)
        applyCommonConfiguration(application)
        return@script application
    }

    fun minecraftMod(block: MinecraftMod.() -> Unit = {}): MinecraftMod = script {
        val mod = buildProjekt().toMinecraftMod(this).apply(block)
        val jarVersion = buildString {
            append(mod.modLoader.getName())
            append(Constants.Char.HYPHEN)
            append(mod.semver.toString())
            append(Constants.Char.PLUS)
            append("mc")
            append(mod.minecraftVersion.getVersion())
        }
        applyCommonConfiguration(mod, jarVersion = jarVersion)
        requirePlugins("org.jetbrains.kotlin.plugin.serialization")
        when (mod.modLoader) {
            ModLoader.FABRIC -> minecraftFabricMod(mod, mod.isFabricApiRequired)
            else -> TODO()
        }
        tasks.named<Jar>("jar") {
            manifest {
                val specificationVersion by 1.toString().toAutoNamedProperty(`Train-Case`)
                val specificationTitle by mod.id.toAutoNamedProperty(`Train-Case`)
                val specificationVendor by mod.developer.toAutoNamedProperty(`Train-Case`)

                val implementationVersion by jarVersion.toAutoNamedProperty(`Train-Case`)
                val implementationTitle by mod.name.toAutoNamedProperty(`Train-Case`)
                val implementationVendor by mod.developer.toAutoNamedProperty(`Train-Case`)

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
        return@script mod
    }

    private fun buildProjekt(): Projekt = script {
        toProjekt(
            requireProperty(license, ::license.name)
        )
    }

    private fun applyCommonConfiguration(
        projekt: IProjekt,
        jarBaseName: String = projekt.repo,
        jarVersion: String = projekt.semver.toString(),
    ) = script {
        requirePlugins("kotlin")
        group = projekt.namespace
        println("group = $group")
        version = jarVersion
        println("version = $version")
        runExtension<BasePluginExtension> {
            archivesName = jarBaseName
        }
        runExtension<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(projekt.javaVersion))
                vendor.set(JvmVendorSpec.ADOPTIUM)
                implementation.set(JvmImplementation.VENDOR_SPECIFIC)
            }
            withSourcesJar()
            withJavadocJar()
        }
        runExtension<KotlinProjectExtension> {
            jvmToolchain(projekt.javaVersion)
        }
        tasks.withType<JavaCompile>().configureEach {
            with(options) {
                release.set(projekt.jvmTarget.toInt())
                encoding = Charsets.UTF_8.toString()
            }
        }
        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(projekt.jvmTarget)
            }
        }
        tasks.named<Jar>("jar") {
            from("LICENSE") {
                rename { oldName ->
                    oldName + Constants.Char.UNDERSCORE + projekt.repo
                }
            }
            archiveVersion.set(jarVersion)
        }
        val unpackJarTask = tasks.register<Sync>("unpackJar") {
            val jarTask = tasks.named<Jar>("jar")

            from(zipTree(jarTask.flatMap { it.archiveFile }))
            into(getBuildDirectory("unpacked"))
            dependsOn(jarTask)
        }
        tasks.named("build") {
            finalizedBy(unpackJarTask)
        }
        runExtension<SourceSetContainer> {
            named("main") {
                val generatedDirectory = "src/main/generated"

                resources.srcDirs(generatedDirectory)
                java.srcDirs("$generatedDirectory/java")
            }
        }
        val metadata = projekt.getMetadata()
        if (metadata.isNotEmpty()) {
            requirePlugins("com.github.gmazzo.buildconfig")
            runExtension<BuildConfigExtension> {
                packageName(projekt.packageName)
                className("ProjektMetadata")
                metadata.forEach { field ->
                    buildConfigField(field.name, field.value)
                }
                useKotlinOutput {
                    internalVisibility = false
                    topLevelConstants = false
                }
            }
        }
        publishingTarget.orNull?.configure(this, projekt)
    }

    private fun minecraftFabricMod(mod: MinecraftMod, isFabricApiRequired: Boolean) = script {
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

            modImplementation(
                "net.fabricmc:fabric-language-kotlin:${Versions.FABRIC_KOTLIN}+kotlin.${mod.kotlinVersion}"
            )

            if (isFabricApiRequired) {
                val apiVersion = resolveCatalogVersion("fabric-api") { "$it+$minecraftVersion" }
                modImplementation("net.fabricmc.fabric-api:fabric-api:$apiVersion")
            }
        }
        runExtension<LoomGradleExtensionAPI> {
            splitEnvironmentSourceSets()
            mods {
                create(mod.id) {
                    runExtension<SourceSetContainer> {
                        mod.environment.getSourceSets().forEach { sourceSet ->
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
                            programArgs("--username", "${mod.developer}-$sideName")
                            vmArgs("-Xms2G", side.getMaxMemoryJvmArgument())
                        }
                    }
                }
            }
            accessWidenerPath.set(file("src/main/resources/${mod.id}.accesswidener"))
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
                            "-Dfabric-api.datagen.modid=${mod.id}",
                            "-Xms2G",
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
        val generateFabricConfigTask by tasks.registering {
            val modConfigFile = getBuildFile("generated/resources/${ModLoader.FABRIC.getConfigFilePath()}")
            val mixinsConfigFile = getBuildFile("generated/resources/${mod.mixinsConfigFileName}")
            outputs.files(modConfigFile, mixinsConfigFile)
            doLast {
                modConfigFile.get().asFile.apply {
                    parentFile.mkdirs()
                    FabricModConfig.of(
                        mod = mod,
                        minecraftVersion = mod.minecraftVersion,
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
                into("assets/${mod.id}/")
            }
        }
    }
}
