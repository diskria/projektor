package io.github.diskria.projektor.core.configurators

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.features.generation.tasks.GenerateLicenseTask
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

    private fun applyCommonConfiguration(project: Project, projekt: T) {
        project.group = projekt.repo.owner.namespace
        project.version = projekt.version
        project.base {
            archivesName.set(projekt.repo.name)
        }
        project.kotlin {
            jvmToolchain(projekt.javaVersion)
        }
        project.java {
            toolchain.apply {
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
        project.tasks {
            configureJvmTarget(jvmTargetOf(projekt.jvmTarget))
            withType<KotlinCompile>().configureEach { kotlinCompile ->
                kotlinCompile.compilerOptions.freeCompilerArgs.addAll("-module-name", project.name)
            }
            withType<JavaCompile>().configureEach { javaCompile ->
                javaCompile.options.encoding = Charsets.UTF_8.toString()
            }
            jar {
                archiveVersion.set(projekt.version)
                val generateLicenseTask = project.rootProject.tasks.getTask<GenerateLicenseTask>()
                dependsOn(generateLicenseTask)
                from(generateLicenseTask) {
                    rename { "${it}_${projekt.repo.name}" }
                }
                val developer = projekt.repo.owner.developer
                manifest.attributes(
                    "Specification-Version" to 1,
                    "Specification-Title" to projekt.repo.name,
                    "Specification-Vendor" to developer,

                    "Implementation-Version" to projekt.version,
                    "Implementation-Title" to projekt.displayName,
                    "Implementation-Vendor" to developer,
                )
            }
        }
    }

    private fun configurePublishing(project: Project, projekt: T) {
        project.projektPublishingTaskNames = buildList {
            projekt.publishingTargets.forEach { target ->
                val publishTask = target.configurePublishTask(project, projekt)
                add(publishTask.name)
                target.configureDistributeTask(project, projekt)?.let { distributeTask ->
                    distributeTask.configure { it.mustRunAfter(publishTask) }
                    add(distributeTask.name)
                }
            }
        }
    }
}
