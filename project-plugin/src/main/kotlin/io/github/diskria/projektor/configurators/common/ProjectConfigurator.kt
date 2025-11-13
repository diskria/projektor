package io.github.diskria.projektor.configurators.common

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.common.`Train-Case`
import io.github.diskria.kotlin.utils.extensions.generics.addIfNotNull
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.common.configurators.IProjektConfigurator
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.KotlinLibrary
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.tasks.ReleaseProjektTask
import io.github.diskria.projektor.tasks.UpdateProjektRepoMetadataTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektGitAttributesTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektGitIgnoreTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektLicenseTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektReadmeTask
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

abstract class ProjectConfigurator<T : Projekt> : IProjektConfigurator {

    fun configure(project: Project): T {
        val projekt = buildProjekt(project)
        applyCommonConfiguration(project, projekt)
        configureProject(project, projekt)

        if (!project.isCommonProject()) {
            val commonProject = project.rootProject.findCommonProject()
            if (commonProject != null) {
                if (projekt is GradlePlugin || projekt is KotlinLibrary) {
                    with(project) {
                        dependencies {
                            compileOnly(commonProject)
                        }
                        configureShadowJar(listOf(commonProject))
                    }
                }
            }
            configurePublishing(project, projekt)
        }
        return projekt
    }

    abstract fun buildProjekt(project: Project): T

    abstract fun configureProject(project: Project, projekt: T): Any

    private fun applyCommonConfiguration(project: Project, projekt: T) = with(project) {
        ensureKotlinPluginsApplied()

        group = projekt.repo.owner.namespace
        version = projekt.archiveVersion

        base {
            archivesName = projekt.archiveName
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
            withType<KotlinCompile>().configureEach {
                @Suppress("MISSING_DEPENDENCY_SUPERCLASS_IN_TYPE_ARGUMENT")
                compilerOptions {
                    freeCompilerArgs.addAll("-module-name", project.name)
                }
            }
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
                        addIfNotNull(distributeTask)
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
