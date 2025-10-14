package io.github.diskria.projektor.configurators

import io.github.diskria.gradle.utils.extensions.ensurePluginApplied
import io.github.diskria.gradle.utils.extensions.implementation
import io.github.diskria.gradle.utils.extensions.test
import io.github.diskria.gradle.utils.extensions.testImplementation
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.projektor.Versions
import io.github.diskria.projektor.extensions.*
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.tasks.generate.GenerateLicenseTask
import org.gradle.api.Project
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

sealed class Configurator<T : IProjekt> {

    abstract fun configure(project: Project, projekt: IProjekt): T

    protected fun applyCommonConfiguration(project: Project, projekt: IProjekt) = with(project) {
        if (projekt !is GradlePlugin) {
            ensurePluginApplied("org.jetbrains.kotlin.jvm")
        }
        ensurePluginApplied("org.jetbrains.kotlin.plugin.serialization")
        dependencies {
            testImplementation(kotlin("test"))
            testImplementation("org.junit.jupiter:junit-jupiter:${Versions.JUNIT}")
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
        group = projekt.namespace
        version = projekt.jarVersion
        base {
            archivesName = projekt.repo
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
        val jarTask = tasks.named<Jar>("jar") {
            from(GenerateLicenseTask.FILE_NAME) {
                rename { it + Constants.Char.UNDERSCORE + projekt.repo }
            }
            archiveVersion.set(projekt.jarVersion)
        }
        val unarchiveArtifact by tasks.registering(Sync::class) {
            val jarFile = jarTask.flatMap { it.archiveFile }.get().asFile
            from(zipTree(jarFile))
            into(jarFile.parentFile.resolve(jarFile.nameWithoutExtension))
            dependsOn(jarTask)
        }
        tasks.named("build") {
            finalizedBy(unarchiveArtifact)
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
        projekt.publishingTarget?.configure(projekt, project)
    }
}
