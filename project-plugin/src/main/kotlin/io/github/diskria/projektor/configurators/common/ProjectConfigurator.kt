package io.github.diskria.projektor.configurators.common

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.kotlin.utils.extensions.appendPath
import io.github.diskria.kotlin.utils.extensions.common.`Train-Case`
import io.github.diskria.kotlin.utils.properties.autoNamedProperty
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.common.ProjectModules
import io.github.diskria.projektor.common.configurators.IProjektConfigurator
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.KotlinLibrary
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.tasks.ReleaseProjektTask
import io.github.diskria.projektor.tasks.UnarchiveProjektArtifactTask
import io.github.diskria.projektor.tasks.UpdateProjektRepoMetadataTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektGitAttributesTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektGitIgnoreTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektLicenseTask
import io.github.diskria.projektor.tasks.generate.GenerateProjektReadmeTask
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

abstract class ProjectConfigurator<T : Projekt> : IProjektConfigurator {

    fun configure(project: Project): T =
        configureProject(project).apply { applyCommonConfiguration(this, project) }

    abstract fun configureProject(project: Project): T

    private fun applyCommonConfiguration(projekt: Projekt, project: Project) = with(project) {
        group = projekt.repo.owner.namespace
        version = projekt.archiveVersion

        ensurePluginApplied("org.jetbrains.kotlin.jvm")
        ensurePluginApplied("org.jetbrains.kotlin.plugin.serialization")
        dependencies {
            testImplementation(kotlin("test"))
            testImplementation("org.junit.jupiter", "junit-jupiter", Versions.JUNIT)
            implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", Versions.KOTLIN_SERIALIZATION)
        }
        base {
            archivesName = projekt.repo.name
        }
        java {
            sourceCompatibility = JavaVersion.toVersion(projekt.javaVersion)
            targetCompatibility = JavaVersion.toVersion(projekt.jvmTarget.toInt())
            toolchain {
                languageVersion = JavaLanguageVersion.of(projekt.javaVersion)
                implementation = JvmImplementation.VENDOR_SPECIFIC
                vendor = JvmVendorSpec.ADOPTIUM
            }
            withSourcesJar()
            if (projekt.isJavadocEnabled) {
                withJavadocJar()
            }
        }
        kotlin {
            jvmToolchain(projekt.javaVersion)
        }
        sourceSets {
            named("main") {
                val generatedDirectory = "src/main/generated"
                resources.srcDirs(generatedDirectory)
                java.srcDirs(generatedDirectory.appendPath("java"))
            }
        }
        tasks {
            withType<JavaCompile>().configureEach {
                with(options) {
                    release = projekt.jvmTarget.toInt()
                    encoding = Charsets.UTF_8.toString()
                }
            }
            withType<KotlinCompile>().configureEach {
                compilerOptions {
                    jvmTarget = projekt.jvmTarget
                }
            }
            configureJarTask {
                dependsOn(withType<GenerateProjektLicenseTask>())
                from(GenerateProjektLicenseTask.LICENSE_FILE_NAME) {
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
            val unarchiveArtifactTask = registerTask<UnarchiveProjektArtifactTask>()
            named("build") {
                finalizedBy(unarchiveArtifactTask)
            }
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
        if (project.path == ProjectModules.Common.PATH) {
            return@with
        }
        if (project.rootProject.findProject(ProjectModules.Common.PATH) != null) {
            if (projekt is GradlePlugin || projekt is KotlinLibrary) {
                ensurePluginApplied("com.gradleup.shadow")
                dependencies {
                    compileOnly(project(ProjectModules.Common.PATH))
                }
                tasks {
                    shadowJar {
                        archiveClassifier = Constants.Char.EMPTY
                        configurations = emptyList()

                        val jarTask = project(ProjectModules.Common.PATH).tasks.jar
                        dependsOn(jarTask)
                        from(zipTree(jarTask.flatMap { it.archiveFile }))
                    }
                }
            } else {
                dependencies {
                    implementation(rootProject.project(ProjectModules.Common.PATH))
                }
            }
        }
        val rootProject = project.rootProject
        val publishingTargetTasks = projekt.publishingTargets
            .filter { it.configurePublishTask(projekt, project) }
            .map { target ->
                val publishTask = project.tasks.named(target.publishTaskName).get()
                val rootPublishTask = rootProject.tasks.findByName(target.publishTaskName)
                    ?: target.configureRootPublishTask(rootProject, publishTask)
                val distributeTask = target.configureDistributeTask(rootProject)
                rootPublishTask to distributeTask
            }
        rootProject.ensureTaskRegistered<ReleaseProjektTask> {
            val tasksOrder = mutableListOf(
                rootProject.getTask<GenerateProjektGitAttributesTask>(),
                rootProject.getTask<GenerateProjektGitIgnoreTask>(),
                rootProject.getTask<GenerateProjektLicenseTask>(),
                rootProject.getTask<GenerateProjektReadmeTask>(),
            )
            publishingTargetTasks.forEach { (rootPublishTask, distributeTask) ->
                tasksOrder.add(rootPublishTask)
                distributeTask?.let { tasksOrder.add(it) }
            }
            tasksOrder.add(rootProject.getTask<UpdateProjektRepoMetadataTask>())

            dependsOn(tasksOrder)
            tasksOrder.windowed(2).forEach { (before, after) ->
                after.mustRunAfter(before)
            }
        }
    }

    companion object {
        private const val BUILD_CONFIG_CLASS_NAME: String = "ProjektBuildConfig"
    }
}
