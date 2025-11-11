package io.github.diskria.projektor.configurators.common

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.`Train-Case`
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.common.configurators.IProjektConfigurator
import io.github.diskria.projektor.common.extensions.getProjektMetadata
import io.github.diskria.projektor.common.projekt.ProjektType
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.KotlinLibrary
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.tasks.*
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

    fun configure(project: Project): T {
        val projekt = buildProjekt(project)
        applyCommonConfiguration(project, projekt, project.getProjektMetadata().type)
        configureProject(project, projekt)

        with(project) {
            if (!isCommonProject()) {
                val commonProject = rootProject.findCommonProject()
                if (commonProject != null) {
                    if (projekt is GradlePlugin || projekt is KotlinLibrary) {
                        dependencies {
                            compileOnly(commonProject)
                        }
                        configureShadowJar(listOf(commonProject))
                    }
                }
                configurePublishing(project, projekt)
            }
        }
        return projekt
    }

    abstract fun buildProjekt(project: Project): T

    abstract fun configureProject(project: Project, projekt: T): Any

    private fun applyCommonConfiguration(project: Project, projekt: T, rootType: ProjektType) = with(project) {
        ensureKotlinPluginsApplied()

        group = projekt.repo.owner.namespace
        version = projekt.archiveVersion

        if (rootType != ProjektType.MINECRAFT_MOD) {
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
                configureJavaVendor(projekt.javaVersion, JvmVendorSpec.ADOPTIUM, JvmVendorSpec.AZUL)
            }
            if (projekt.isSourcesEnabled) {
                withSourcesJar()
            }
            if (projekt.isJavadocEnabled) {
                withJavadocJar()
            }
        }
        kotlin {
            jvmToolchain(projekt.javaVersion)
        }
        tasks {
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
            if (rootType != ProjektType.MINECRAFT_MOD) {
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
    }

    private fun configurePublishing(project: Project, projekt: T) = with(project) {
        val publishingTargetTasks = projekt.publishingTargets
            .filter { it.configurePublishTask(this, projekt) }
            .map { target ->
                val publishTask = tasks.named(target.publishTaskName).get()
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
        rootProject.ensureTaskRegistered<CleanIncludedBuildsTask>()
        rootProject.ensureTaskRegistered<CleanSubprojectsTask>()
        rootProject.ensureTaskRegistered<CleanAllTask>()
    }

    companion object {
        private const val BUILD_CONFIG_CLASS_NAME: String = "ProjektBuildConfig"
    }
}
