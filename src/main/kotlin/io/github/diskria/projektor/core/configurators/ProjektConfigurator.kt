package io.github.diskria.projektor.core.configurators

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.ToolchainDefaults
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.namedByType
import io.github.diskria.projektor.features.distribution.target.mapToModel
import io.github.diskria.projektor.features.generation.tasks.GenerateLicenseTask
import io.github.diskria.projektor.features.generation.tasks.GenerateReleaseWorkflowTask
import io.github.diskria.projektor.features.metadata.tasks.UpdateGithubRepoMetadataTask
import io.github.diskria.projektor.features.release.ReleaseProjektTask
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal abstract class ProjektConfigurator<P : Projekt, D : Projekt.Distributable, BL : Projekt.BuildLogic> {

    @Suppress("UNCHECKED_CAST")
    fun configure(project: Project, projektMetadata: ProjektMetadata.Distributable): D {
        val projekt = buildProjekt(project, projektMetadata)
        applyCommonConfiguration(project, projekt)
        configureProject(project, projekt)
        if (projekt is Projekt.Distributable) {
            configureDistribution(project, projekt)
        }
        return projekt as D
    }

    @Suppress("UNCHECKED_CAST")
    fun configure(project: Project, projektMetadata: ProjektMetadata.BuildLogic): BL {
        val projekt = buildProjekt(project, projektMetadata)
        applyCommonConfiguration(project, projekt)
        configureProject(project, projekt)
        return projekt as BL
    }

    abstract fun buildProjekt(project: Project, projektMetadata: ProjektMetadata): P

    abstract fun configureProject(project: Project, projekt: P): Any

    private fun applyCommonConfiguration(project: Project, projekt: P) {
        project.group = projekt.metadata.namespace
        if (projekt is Projekt.Distributable) {
            project.version = projekt.version
        }
        project.extensions.configure<BasePluginExtension> {
            archivesName.set(projekt.name)
        }
        project.extensions.configure<KotlinProjectExtension> {
            jvmToolchain(projekt.javaVersion)
        }
        project.extensions.configure<JavaPluginExtension> {
            toolchain.apply {
                languageVersion.set(JavaLanguageVersion.of(projekt.javaVersion))
                vendor.set(JvmVendorSpec.of(ToolchainDefaults.JVM_VENDOR))
                implementation.set(JvmImplementation.VENDOR_SPECIFIC)
            }
        }
        project.tasks {
            withType<KotlinCompile>().configureEach { kotlinCompile ->
                kotlinCompile.compilerOptions.apply {
                    jvmTarget.set(
                        if (projekt.jvmTarget == 8) JvmTarget.JVM_1_8
                        else JvmTarget.fromTarget(projekt.jvmTarget.toString())
                    )
                    freeCompilerArgs.addAll("-module-name", projekt.name)
                }
            }
            withType<JavaCompile>().configureEach { javaCompile ->
                javaCompile.options.apply {
                    release.set(projekt.jvmTarget)
                    encoding = Charsets.UTF_8.toString()
                }
            }
            if (projekt is Projekt.Distributable) {
                named<Jar>("jar").configure { jar ->
                    project.rootProject.tasks.withType<GenerateLicenseTask>().configureEach { generateLicenseTask ->
                        val fileNameSuffix = "_${projekt.metadata.repo.name}"
                        jar.inputs.property("licenseFileNameSuffix", fileNameSuffix)
                        jar.from(generateLicenseTask.outputFile) { copySpec ->
                            copySpec.rename { fileName -> "$fileName$fileNameSuffix" }
                        }
                    }
                    jar.archiveVersion.set(projekt.version)
                }
            }
        }
    }

    private fun configureDistribution(project: Project, projekt: Projekt.Distributable) {
        if (projekt.distributionTargetTypes.isEmpty()) return
        project.extensions.configure<JavaPluginExtension> {
            if (projekt.isSourcesEnabled) withSourcesJar()
            if (projekt.isJavadocEnabled) withJavadocJar()
        }
        val distributeTaskNames = projekt.distributionTargetTypes.flatMap {
            it.mapToModel().configureDistributeTasks(project, projekt)
        }
        val distributeTasks = project.tasks.matching { it.name in distributeTaskNames }
        val rootTasks = project.rootProject.tasks
        val generateReleaseWorkflowTask = rootTasks.namedByType<GenerateReleaseWorkflowTask>()
        distributeTasks.configureEach { it.mustRunAfter(generateReleaseWorkflowTask) }
        rootTasks.namedByType<UpdateGithubRepoMetadataTask>().configure { it.mustRunAfter(distributeTasks) }
        rootTasks.namedByType<ReleaseProjektTask>().configure { it.dependsOn(distributeTasks) }
    }
}
