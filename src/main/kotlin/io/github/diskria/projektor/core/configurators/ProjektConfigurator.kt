package io.github.diskria.projektor.core.configurators

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.features.distribution.target.mapToModel
import io.github.diskria.projektor.features.generation.readme.tasks.GenerateReadmeTask
import io.github.diskria.projektor.features.generation.tasks.GenerateLicenseTask
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

internal abstract class ProjektConfigurator<T : Projekt> {

    fun configure(project: Project, projektMetadata: ProjektMetadata): T {
        val projekt = buildProjekt(project, projektMetadata)
        applyCommonConfiguration(project, projekt)
        configureProject(project, projekt)
        if (projekt is Projekt.Distributable) {
            configureDistribution(project, projektMetadata, projekt)
        }
        return projekt
    }

    abstract fun buildProjekt(project: Project, projektMetadata: ProjektMetadata): T

    abstract fun configureProject(project: Project, projekt: T): Any

    private fun applyCommonConfiguration(project: Project, projekt: T) {
        if (projekt is Projekt.Distributable) {
            project.group = projekt.metadata.repo.owner.namespace
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
                vendor.set(JvmVendorSpec.ADOPTIUM)
                implementation.set(JvmImplementation.VENDOR_SPECIFIC)
            }
        }
        project.tasks {
            configureJvmTarget(jvmTargetOf(projekt.jvmTarget))
            withType<KotlinCompile>().configureEach { kotlinCompile ->
                kotlinCompile.compilerOptions.freeCompilerArgs.addAll("-module-name", project.name)
            }
            withType<JavaCompile>().configureEach { javaCompile ->
                javaCompile.options.encoding = Charsets.UTF_8.toString()
            }
            jar {
                project.rootProject.tasks.find<GenerateLicenseTask>()?.let { generateLicenseTask ->
                    dependsOn(generateLicenseTask)
                    from(generateLicenseTask) {
                        it.rename { fileName -> "${fileName}_${projekt.metadata.repo.name}" }
                    }
                }
                if (projekt is Projekt.Distributable) {
                    archiveVersion.set(projekt.version)
                }
            }
        }
    }

    private fun configureDistribution(
        project: Project,
        projektMetadata: ProjektMetadata,
        projekt: Projekt.Distributable,
    ) {
        if (projekt.distributionTargetTypes.isEmpty()) return
        project.extensions.configure<JavaPluginExtension> {
            if (projekt.isSourcesEnabled) withSourcesJar()
            if (projekt.isJavadocEnabled) withJavadocJar()
        }
        val rootTaskContainer = project.rootProject.tasks
        val distributeTasks = projekt.distributionTargetTypes.map {
            it.mapToModel().configureDistributeTask(project, projekt, projektMetadata)
        }
        val generateReadmeTask = rootTaskContainer.get<GenerateReadmeTask>()
        distributeTasks.forEach { distributeTask ->
            distributeTask.configure { it.mustRunAfter(generateReadmeTask) }
        }
        rootTaskContainer.get<UpdateGithubRepoMetadataTask>().configure {
            it.mustRunAfter(distributeTasks)
        }
        rootTaskContainer.get<ReleaseProjektTask>().configure {
            it.dependsOn(distributeTasks)
        }
    }
}
