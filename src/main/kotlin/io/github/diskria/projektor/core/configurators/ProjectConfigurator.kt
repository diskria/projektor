package io.github.diskria.projektor.core.configurators

import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.extensions.configureJvmTarget
import io.github.diskria.projektor.extensions.getTask
import io.github.diskria.projektor.extensions.jar
import io.github.diskria.projektor.extensions.projektDistributionTaskNames
import io.github.diskria.projektor.features.generation.tasks.GenerateLicenseTask
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.attributes
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal abstract class ProjectConfigurator<T : Projekt> {

    fun configure(project: Project): T {
        val projekt = buildProjekt(project)
        applyCommonConfiguration(project, projekt)
        configureProject(project, projekt)
        configureDistribution(project, projekt)
        return projekt
    }

    abstract fun buildProjekt(project: Project): T

    abstract fun configureProject(project: Project, projekt: T): Any

    private fun applyCommonConfiguration(project: Project, projekt: T) {
        project.group = projekt.metadata.repo.owner.namespace
        project.version = projekt.version
        project.extensions.configure<BasePluginExtension> {
            archivesName.set(projekt.metadata.repo.name)
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
                    rename { "${it}_${projekt.metadata.repo.name}" }
                }
                val developer = projekt.metadata.repo.owner.developer
                manifest.attributes(
                    "Specification-Version" to 1,
                    "Specification-Title" to projekt.metadata.repo.name,
                    "Specification-Vendor" to developer,

                    "Implementation-Version" to projekt.version,
                    "Implementation-Title" to projekt.displayName,
                    "Implementation-Vendor" to developer,
                )
            }
        }
    }

    private fun configureDistribution(project: Project, projekt: T) {
        project.projektDistributionTaskNames = projekt.distributionTargets.map { target ->
            val distributeTask = target.configureDistributeTask(project, projekt)
            distributeTask.name
        }
    }
}
