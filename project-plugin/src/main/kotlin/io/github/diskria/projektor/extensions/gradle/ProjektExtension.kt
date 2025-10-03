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
import io.github.diskria.kotlin.utils.words.ScreamingSnakeCase
import io.github.diskria.projektor.extensions.kotlin.mappers.toInt
import io.github.diskria.projektor.extensions.kotlin.mappings
import io.github.diskria.projektor.extensions.kotlin.minecraft
import io.github.diskria.projektor.extensions.kotlin.modImplementation
import io.github.diskria.projektor.licenses.License
import io.github.diskria.projektor.minecraft.*
import io.github.diskria.projektor.minecraft.config.FabricModConfig
import io.github.diskria.projektor.minecraft.config.MixinsConfig
import io.github.diskria.projektor.minecraft.version.getVersion
import io.github.diskria.projektor.owner.GithubProfile
import io.github.diskria.projektor.owner.ProjektOwner
import io.github.diskria.projektor.owner.domain.MinecraftDomain
import io.github.diskria.projektor.projekt.*
import io.github.diskria.projektor.publishing.PublishingTarget
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.api.fabricapi.FabricApiExtension
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
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

private typealias GradleProperty<T> = org.gradle.api.provider.Property<T>
private typealias BuildConfigProperty<T> = io.github.diskria.kotlin.utils.poet.Property<T>

open class ProjektExtension @Inject constructor(objects: ObjectFactory) : ProjectExtension() {

    val owner: GradleProperty<ProjektOwner> = objects.property(ProjektOwner::class.java)
    val license: GradleProperty<License> = objects.property(License::class.java)
    val publishingTarget: GradleProperty<PublishingTarget> = objects.property(PublishingTarget::class.java)

    fun gradlePlugin(block: GradlePlugin.() -> Unit = {}): GradlePlugin = script {
        val plugin = buildProjekt().toGradlePlugin(this).apply(block)
        applyCommonConfiguration(plugin)
        val pluginId = plugin.packageName
        val className = plugin.classNameBase + "GradlePlugin"
        buildConfigs(plugin.packageName, "GradlePluginMetadata") {
            val pluginId by pluginId.toAutoNamedProperty(ScreamingSnakeCase)
            val pluginName by plugin.name.toAutoNamedProperty(ScreamingSnakeCase)
            listOf(pluginId, pluginName)
        }
        getExtensionOrThrow<GradlePluginDevelopmentExtension>().apply {
            website.set(plugin.owner.getRepositoryUrl(plugin.slug))
            vcsUrl.set(plugin.owner.getRepositoryUrl(plugin.slug, isVcsUrl = true))

            plugins {
                create(pluginId) {
                    id = pluginId
                    implementationClass = plugin.packageName.appendPackageName(className)

                    displayName = plugin.name
                    description = plugin.description

                    tags.set(plugin.tags.toNullIfEmpty())
                }
            }
        }
        publishingTarget.orNull?.configurePublishing(this, plugin)
        return@script plugin
    }

    fun kotlinLibrary(block: KotlinLibrary.() -> Unit = {}): KotlinLibrary = script {
        val library = buildProjekt().toKotlinLibrary(this).apply(block)
        buildConfigs(library.packageName, "KotlinLibraryMetadata") {
            val libraryName by library.name.toAutoNamedProperty(ScreamingSnakeCase)
            listOf(libraryName)
        }
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
        applyCommonConfiguration(library)
        publishingTarget.orNull?.configurePublishing(this, library)
        return@script library
    }

    fun androidLibrary(block: AndroidLibrary.() -> Unit = {}): AndroidLibrary = script {
        val library = buildProjekt().toAndroidLibrary(this).apply(block)
        publishingTarget.orNull?.configurePublishing(this, library)
        return@script library
    }

    fun androidApplication(block: AndroidApplication.() -> Unit = {}): AndroidApplication = script {
        val application = buildProjekt().toAndroidApplication(this).apply(block)
        applyCommonConfiguration(application)
        publishingTarget.orNull?.configurePublishing(this, application)
        return@script application
    }

    fun minecraftMod(block: MinecraftMod.() -> Unit = {}): MinecraftMod = script {
        val mod = buildProjekt().toMinecraftMod(this).apply(block)
        val artifactVersion = buildString {
            append(mod.modLoader.getName())
            append(Constants.Char.HYPHEN)
            append(mod.semver.toString())
            append(Constants.Char.PLUS)
            append(MinecraftDomain.suffix)
            append(mod.minecraftVersion.getVersion())
        }
        applyCommonConfiguration(mod, jarVersion = artifactVersion)
        requirePlugins("org.jetbrains.kotlin.plugin.serialization")
        buildConfigs(mod.packageName, "MinecraftModMetadata") {
            val modId by mod.id.toAutoNamedProperty(ScreamingSnakeCase)
            val modName by mod.name.toAutoNamedProperty(ScreamingSnakeCase)
            listOf(modId, modName)
        }
        when (mod.modLoader) {
            ModLoader.FABRIC -> minecraftFabricMod(mod, mod.isFabricApiRequired)
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

        publishingTarget.orNull?.configurePublishing(this, mod)
        return@script mod
    }

    private fun buildConfigs(
        packageName: String,
        className: String,
        fields: () -> List<BuildConfigProperty<String>>
    ) = script {
        requirePlugins("com.github.gmazzo.buildconfig")
        getExtensionOrThrow<BuildConfigExtension>().apply {
            packageName(packageName)
            className(className)
            fields().forEach { field ->
                buildConfigField(field.name, field.value)
            }
            useKotlinOutput {
                internalVisibility = false
                topLevelConstants = false
            }
        }
    }

    private fun buildProjekt(): Projekt = script {
        Projekt.of(
            project = this,
            owner = requireProperty(owner, ::owner.name),
            license = requireProperty(license, ::license.name),
        )
    }

    private fun applyCommonConfiguration(
        projekt: IProjekt,
        jarBaseName: String = projekt.slug,
        jarVersion: String = projekt.semver.toString(),
    ) = script {
        requirePlugins("kotlin")
        group = projekt.owner.namespace
        version = jarVersion
        getExtensionOrThrow<BasePluginExtension>().apply {
            archivesName = jarBaseName
        }
        getExtensionOrThrow<JavaPluginExtension>().apply {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(projekt.javaVersion))
                vendor.set(JvmVendorSpec.ADOPTIUM)
                implementation.set(JvmImplementation.VENDOR_SPECIFIC)
            }
            withSourcesJar()
            withJavadocJar()
        }
        getExtensionOrThrow<KotlinProjectExtension>().apply {
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
                    oldName + Constants.Char.UNDERSCORE + projekt.slug
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
        getExtensionOrThrow<SourceSetContainer>().apply {
            named("main") {
                val generatedDirectory = "src/main/generated"

                resources.srcDirs(generatedDirectory)
                java.srcDirs("$generatedDirectory/java")
            }
        }
    }

    private fun minecraftFabricMod(minecraftMod: MinecraftMod, isFabricApiRequired: Boolean) = script {
        requirePlugins("fabric-loom")
        val loaderVersion = Versions.FABRIC_LOADER
        val mixins = minecraftMod.environment
            .getSourceSets()
            .mapNotNull { sourceSet ->
                val logicalName = sourceSet.logicalName()
                val pathBase = "src/$logicalName/java/${minecraftMod.packagePath}/mixins"
                getFileNames(
                    if (sourceSet == SourceSet.MAIN) pathBase
                    else "$pathBase/$logicalName"
                ).toNullIfEmpty()?.let { sourceSet to it }
            }
            .toMap()
        val datagenClasses = getFileNames("src/datagen/kotlin/${minecraftMod.packagePath}").map {
            minecraftMod.packageName + Constants.Char.DOT + it
        }
        val minecraftVersion = minecraftMod.minecraftVersion.getVersion()
        dependencies {
            minecraft("com.mojang:minecraft:$minecraftVersion")
            modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

            val yarnVersion = resolveCatalogVersion("fabric-yarn") { "$minecraftVersion+build.$it" }
            mappings("net.fabricmc:yarn:$yarnVersion:v2")

            modImplementation(
                "net.fabricmc:fabric-language-kotlin:${Versions.FABRIC_KOTLIN}+kotlin.${minecraftMod.kotlinVersion}"
            )

            if (isFabricApiRequired) {
                val apiVersion = resolveCatalogVersion("fabric-api") { "$it+$minecraftVersion" }
                modImplementation("net.fabricmc.fabric-api:fabric-api:$apiVersion")
            }
        }
        getExtensionOrThrow<LoomGradleExtensionAPI>().apply {
            splitEnvironmentSourceSets()
            mods {
                create(minecraftMod.id) {
                    getExtensionOrThrow<SourceSetContainer>().apply {
                        minecraftMod.environment.getSourceSets().forEach { sourceSet ->
                            sourceSet(getByName(sourceSet.logicalName()))
                        }
                    }
                }
            }
            runs {
                ModSide.entries.forEach { side ->
                    val logicalName = side.logicalName()
                    named(logicalName) {
                        val hasSide = minecraftMod.environment.sides.contains(side)
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
            accessWidenerPath.set(file("src/main/resources/${minecraftMod.id}.accesswidener"))
        }
        if (datagenClasses.isNotEmpty()) {
            getExtensionOrThrow<LoomGradleExtensionAPI>().apply {
                runs {
                    create("data") {
                        name = "Datagen"
                        runDir = "datagen"
                        environment("server")
                        vmArgs(
                            "-Dfabric-api.datagen",
                            "-Dfabric-api.datagen.output-dir=${file("src/main/generated")}",
                            "-Dfabric-api.datagen.modid=${minecraftMod.id}",
                            "-Xms2G",
                            ModSide.SERVER.getMaxMemoryJvmArgument(),
                        )
                    }
                }
            }
            getExtensionOrThrow<FabricApiExtension>().apply {
                configureDataGeneration {
                    client = true
                }
            }
        }
        val generateFabricConfigTask by tasks.registering {
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
                        isApiRequired = isFabricApiRequired,
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
                into("assets/${minecraftMod.slug}/")
            }
        }
    }
}
