package io.github.diskria.projektor.configurators.common

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.`Train-Case`
import io.github.diskria.kotlin.utils.extensions.generics.joinByNewLine
import io.github.diskria.kotlin.utils.extensions.generics.toNullIfEmpty
import io.github.diskria.kotlin.utils.extensions.toNullIfEmpty
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.common.configurators.IProjektConfigurator
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderFamily
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.KotlinLibrary
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.tasks.ReleaseProjektTask
import io.github.diskria.projektor.tasks.UpdateProjektRepoMetadataTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektGitAttributesTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektGitIgnoreTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektLicenseTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektReadmeTask
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.*

abstract class ProjectConfigurator<T : Projekt> : IProjektConfigurator {

    fun configure(project: Project): T =
        configureProject(project).apply { applyCommonConfiguration(this, project) }

    abstract fun configureProject(project: Project): T

    private fun applyCommonConfiguration(projekt: Projekt, project: Project) = with(project) {
        group = projekt.repo.owner.namespace
        version = projekt.archiveVersion

        ensureKotlinPluginsApplied()
        dependencies {
            implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", Versions.KOTLIN_SERIALIZATION)
        }

        val rootProjektType = project.getProjektMetadata().type
        if (rootProjektType != ProjektType.MINECRAFT_MOD) {
            dependencies {
                testImplementation(kotlin("test"))
                testImplementation("org.junit.jupiter", "junit-jupiter", Versions.JUNIT)
            }
        }
        base {
            archivesName = projekt.repo.name
        }
        java {
            toolchain {
                configureJavaVendor(Versions.JAVA, JvmVendorSpec.ADOPTIUM, JvmVendorSpec.AZUL)
            }
            if (projekt.isSourcesEnabled) {
                withSourcesJar()
            }
            if (projekt.isJavadocEnabled) {
                withJavadocJar()
            }
        }
        kotlin {
            jvmToolchain(Versions.JAVA)
        }
        tasks {
            clean {
                dependsOnIncludedBuilds()
                dependsOnSubprojects()
            }
            configureJvmTarget(projekt.jvmTarget)
            withType<JavaCompile>().configureEach {
                options.encoding = Charsets.UTF_8.toString()
            }
            jar {
                val generateLicenseTask = rootProject.getTask<GenerateProjektLicenseTask>()
                dependsOn(generateLicenseTask)
                from(generateLicenseTask) {
                    rename { it + Constants.Char.UNDERSCORE + projekt.repo.name }
                }
                manifest {
                    val developer = projekt.repo.owner.developer

                    val specificationVersion by 1.toString().autoNamedProperty(`Train-Case`)
                    val specificationTitle by projekt.repo.name.autoNamedProperty(`Train-Case`)
                    val specificationVendor by developer.autoNamedProperty(`Train-Case`)

                    val implementationVersion by projekt.archiveVersion.autoNamedProperty(`Train-Case`)
                    val implementationTitle by projekt.name.autoNamedProperty(`Train-Case`)
                    val implementationVendor by developer.autoNamedProperty(`Train-Case`)

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
                archiveVersion = projekt.archiveVersion
            }
            if (rootProjektType != ProjektType.MINECRAFT_MOD) {
                test {
                    useJUnitPlatform()
                    testLogging {
                        events(
                            TestLogEvent.PASSED,
                            TestLogEvent.SKIPPED,
                            TestLogEvent.FAILED,
                        )
                        exceptionFormat = TestExceptionFormat.FULL

                        ignoreFailures = true

                        showCauses = true
                        showExceptions = true
                        showStackTraces = true
                        showStandardStreams = true
                    }
                }
            }
        }
        val buildConfigFields = projekt.getBuildConfigFields()
        if (buildConfigFields.isNotEmpty()) {
            buildConfig {
                packageName(projekt.packageName)
                className(BUILD_CONFIG_CLASS_NAME)
                buildConfigFields.forEach {
                    buildConfigField(it.name, it.value)
                }
                useKotlinOutput {
                    internalVisibility = false
                    topLevelConstants = false
                }
            }
        }
        if (project.isCommonProject()) {
            return@with
        }
        val commonProject = project.rootProject.findCommonProject()
        if (commonProject != null) {
            if (projekt is GradlePlugin || projekt is KotlinLibrary) {
                configureShadowJar(listOf(commonProject))
            } else if (projekt is MinecraftMod) {
                val mod = projekt
                val sideProjects = children
                val projectsToShadow = sideProjects + commonProject
                val isFabricFamily = mod.loader.family == ModLoaderFamily.FABRIC
                configureShadowJar(
                    projects = projectsToShadow,
                    classifier = if (isFabricFamily) "dev" else null,
                    destination = if (isFabricFamily) getBuildDirectory("devlibs").get() else null,
                    shouldDisableJar = true,
                ) {
                    val mergedAccessorConfigFile = getTempFile(mod.accessorConfigFileName)
                    copyFile(mergedAccessorConfigFile, mod.assetsPath)
                    doFirst {
                        mergedAccessorConfigFile.writeText(
                            sideProjects.mapNotNull { sideProject ->
                                sideProject.sourceSets.main.resourcesDirectory
                                    .resolve(mod.accessorConfigFileName)
                                    .readLines()
                                    .mapNotNull { it.trim().toNullIfEmpty() }
                                    .toNullIfEmpty()
                            }.flatten().toSet().joinByNewLine()
                        )
                    }
                }
            }
        }
        val publishingTargetTasks = projekt.publishingTargets
            .filter { it.configurePublishTask(projekt, project) }
            .map { target ->
                val publishTask = project.tasks.named(target.publishTaskName).get()
                val rootPublishTask = rootProject.getTaskOrNull(target.publishTaskName)
                    ?: target.configureRootPublishTask(rootProject, publishTask, projekt)
                val distributeTask = when {
                    EnvironmentHelper.isCI() -> target.configureDistributeTask(rootProject)
                    else -> null
                }
                rootPublishTask to distributeTask
            }
        rootProject.ensureTaskRegistered<ReleaseProjektTask> {
            dependsSequentiallyOn(
                buildList {
                    addAll(
                        listOf(
                            rootProject.getTask<GenerateProjektGitAttributesTask>(),
                            rootProject.getTask<GenerateProjektGitIgnoreTask>(),
                            rootProject.getTask<GenerateProjektLicenseTask>(),
                            rootProject.getTask<GenerateProjektReadmeTask>(),
                        )
                    )
                    publishingTargetTasks.forEach { (rootPublishTask, distributeTask) ->
                        add(rootPublishTask)
                        distributeTask?.let { add(it) }
                    }
                    add(rootProject.getTask<UpdateProjektRepoMetadataTask>())
                }
            )
        }
    }

    companion object {
        private const val BUILD_CONFIG_CLASS_NAME: String = "ProjektBuildConfig"
    }
}
