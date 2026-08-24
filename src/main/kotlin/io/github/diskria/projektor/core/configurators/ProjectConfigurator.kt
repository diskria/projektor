package io.github.diskria.projektor.core.configurators

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.features.generation.readme.tasks.GenerateProjektReadmeTask
import io.github.diskria.projektor.features.generation.tasks.GenerateProjektGitAttributesTask
import io.github.diskria.projektor.features.generation.tasks.GenerateProjektGitIgnoreTask
import io.github.diskria.projektor.features.generation.tasks.GenerateProjektLicenseTask
import io.github.diskria.projektor.features.metadata.tasks.UpdateGithubRepoMetadataTask
import io.github.diskria.projektor.features.release.ReleaseProjektTask
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal abstract class ProjectConfigurator<T : Projekt> {

    fun configure(project: Project): T {
        val projekt = buildProjekt(project)
        applyCommonConfiguration(project, projekt)
        configureProject(project, projekt)
        configurePublishing(project, projekt)
        return projekt
    }

    abstract fun buildProjekt(project: Project): T

    abstract fun configureProject(project: Project, projekt: T): Any

    private fun applyCommonConfiguration(project: Project, projekt: T) = with(project) {
        group = projekt.repo.owner.namespace
        version = projekt.version
        base {
            archivesName.set(projekt.archiveName)
        }
        java {
            with(toolchain) {
                languageVersion.set(JavaLanguageVersion.of(projekt.javaVersion))
                vendor.set(JvmVendorSpec.ADOPTIUM)
                implementation.set(JvmImplementation.VENDOR_SPECIFIC)
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
            configureJvmTarget(jvmTargetOf(projekt.jvmTarget))
            withType<KotlinCompile>().configureEach { kotlinCompile ->
                with(kotlinCompile.compilerOptions) {
                    freeCompilerArgs.addAll("-module-name", project.name)
                }
            }
            withType<JavaCompile>().configureEach { javaCompile ->
                with(javaCompile.options) {
                    encoding = Charsets.UTF_8.toString()
                }
            }
            jar {
                archiveVersion.set(projekt.version)
                val generateLicenseTask = rootProject.tasks.getTask<GenerateProjektLicenseTask>()
                dependsOn(generateLicenseTask)
                from(generateLicenseTask) {
                    rename { "${it}_${projekt.repo.name}" }
                }
                with(manifest) {
                    val developer = projekt.repo.owner.developer
                    attributes(
                        "Specification-Version" to 1,
                        "Specification-Title" to projekt.repo.name,
                        "Specification-Vendor" to developer,

                        "Implementation-Version" to projekt.version,
                        "Implementation-Title" to projekt.name,
                        "Implementation-Vendor" to developer,
                    )
                }
            }
        }
    }

    private fun configurePublishing(project: Project, projekt: T) = with(project.rootProject.tasks) {
        ensureTaskRegistered<ReleaseProjektTask>().dependsSequentiallyOn(
            buildList {
                addAll(
                    listOf(
                        getTask<GenerateProjektGitAttributesTask>(),
                        getTask<GenerateProjektGitIgnoreTask>(),
                        getTask<GenerateProjektLicenseTask>(),
                        getTask<GenerateProjektReadmeTask>(),
                    )
                )
                projekt.publishingTargets.forEach { target ->
                    add(target.configurePublishTask(project, projekt))
                    target.configureDistributeTask(project)?.let { add(it) }
                }
                add(getTask<UpdateGithubRepoMetadataTask>())
            }
        )
    }
}
