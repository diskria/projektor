package io.github.diskria.projektor.core.configurators

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.ToolchainDefaults
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.configureJvmTarget
import io.github.diskria.projektor.extensions.findByType
import io.github.diskria.projektor.extensions.jar
import io.github.diskria.projektor.extensions.jvmTargetOf
import io.github.diskria.projektor.features.distribution.target.mapToModel
import io.github.diskria.projektor.features.generation.tasks.GenerateLicenseTask
import io.github.diskria.projektor.features.generation.tasks.GenerateReleaseWorkflowTask
import io.github.diskria.projektor.features.metadata.tasks.UpdateGithubRepoMetadataTask
import io.github.diskria.projektor.features.release.ReleaseProjektTask
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
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
            configureJvmTarget(jvmTargetOf(projekt.jvmTarget))
            withType<KotlinCompile>().configureEach { kotlinCompile ->
                kotlinCompile.compilerOptions.freeCompilerArgs.addAll("-module-name", projekt.name)
            }
            withType<JavaCompile>().configureEach { javaCompile ->
                javaCompile.options.encoding = Charsets.UTF_8.toString()
            }
            jar {
                if (projekt is Projekt.Distributable) {
                    project.rootProject.tasks.findByType<GenerateLicenseTask>()?.let { generateLicenseTask ->
                        from(generateLicenseTask) { copySpec ->
                            copySpec.rename { fileName -> "${fileName}_${projekt.metadata.repo.name}" }
                        }
                    }
                    archiveVersion.set(projekt.version)
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
        val rootTaskContainer = project.rootProject.tasks
        val distributeTasks = projekt.distributionTargetTypes.map {
            it.mapToModel().configureDistributeTask(project, projekt)
        }
        val generateReleaseWorkflowTask = rootTaskContainer.withType<GenerateReleaseWorkflowTask>()
        distributeTasks.forEach { distributeTask ->
            distributeTask.configure { task ->
                task.mustRunAfter(generateReleaseWorkflowTask)
            }
        }
        rootTaskContainer.withType<UpdateGithubRepoMetadataTask>().configureEach { task ->
            task.mustRunAfter(distributeTasks)
        }
        rootTaskContainer.withType<ReleaseProjektTask>().configureEach { task ->
            task.dependsOn(distributeTasks)
        }
    }
}
