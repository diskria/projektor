package io.github.diskria.projektor.configurators.common

import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.common.configurators.IProjektConfigurator
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.tasks.UnarchiveArtifactTask
import io.github.diskria.projektor.tasks.generate.GenerateLicenseTask
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

abstract class ProjectConfigurator<T : Projekt> : IProjektConfigurator {

    fun configure(project: Project): T =
        configureProject(project).apply { applyCommonConfiguration(project, this) }

    abstract fun configureProject(project: Project): T

    private fun applyCommonConfiguration(project: Project, projekt: Projekt) = with(project) {
        if (projekt !is GradlePlugin) {
            ensurePluginApplied("org.jetbrains.kotlin.jvm")
        }
        ensurePluginApplied("org.jetbrains.kotlin.plugin.serialization")
        dependencies {
            testImplementation(kotlin("test"))
            testImplementation("org.junit.jupiter", "junit-jupiter", Versions.JUNIT)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KOTLIN_SERIALIZATION}")
        }
        tasks.test {
            useJUnitPlatform()
            testLogging {
                events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
                exceptionFormat = TestExceptionFormat.FULL

                ignoreFailures = true

                showCauses = true
                showExceptions = true
                showStackTraces = true
                showStandardStreams = true
            }
        }
        group = projekt.repo.owner.namespace
        version = projekt.archiveVersion
        base {
            archivesName.assign(projekt.repo.name)
        }
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(projekt.javaVersion))
                implementation.set(JvmImplementation.VENDOR_SPECIFIC)
                vendor.set(JvmVendorSpec.ADOPTIUM)
            }
            withSourcesJar()
            withJavadocJar()
        }
        kotlin {
            jvmToolchain(projekt.javaVersion)
        }
        tasks.withType<JavaCompile>().configureEach {
            with(options) {
                release.set(projekt.jvmTarget.toInt())
                encoding = Charsets.UTF_8.toString()
            }
        }
        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(projekt.jvmTarget)
            }
        }
        tasks.named<Jar>("jar") {
            dependsOn(tasks.withType<GenerateLicenseTask>())
            from(GenerateLicenseTask.OUTPUT_FILE_NAME) {
                rename { it + Constants.Char.UNDERSCORE + projekt.repo.name }
            }
            archiveVersion.set(projekt.archiveVersion)
        }
        val unarchiveArtifactTask = registerTask<UnarchiveArtifactTask>()
        tasks.named("build") {
            finalizedBy(unarchiveArtifactTask)
        }
        sourceSets {
            named("main") {
                val generatedDirectory = "src/main/generated"
                resources.srcDirs(generatedDirectory)
                java.srcDirs("$generatedDirectory/java")
            }
        }
        val buildConfigFields = projekt.getBuildConfigFields()
        if (buildConfigFields.isNotEmpty()) {
            buildConfig {
                packageName(projekt.packageName)
                className("ProjektBuildConfig")
                buildConfigFields.forEach { field ->
                    buildConfigField(field.name, field.value)
                }
                useKotlinOutput {
                    internalVisibility = false
                    topLevelConstants = false
                }
            }
        }
        projekt.publishingTarget.configure(projekt, project)
    }
}
