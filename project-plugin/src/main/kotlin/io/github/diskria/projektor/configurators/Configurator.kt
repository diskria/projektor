package io.github.diskria.projektor.configurators

import com.github.gmazzo.buildconfig.BuildConfigExtension
import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.requirePlugins
import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmImplementation
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

sealed class Configurator<T : IProjekt> {

    abstract fun configure(project: Project, projekt: IProjekt): T

    protected fun applyCommonConfiguration(project: Project, projekt: IProjekt) = with(project) {
        requirePlugins("kotlin")
        group = projekt.namespace
        version = projekt.jarVersion
        runExtension<BasePluginExtension> {
            archivesName = projekt.repo
        }
        runExtension<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(projekt.javaVersion))
                vendor.set(JvmVendorSpec.ADOPTIUM)
                implementation.set(JvmImplementation.VENDOR_SPECIFIC)
            }
            withSourcesJar()
            withJavadocJar()
        }
        runExtension<KotlinProjectExtension> {
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
            from("LICENSE") {
                rename { oldName ->
                    oldName + Constants.Char.UNDERSCORE + projekt.repo
                }
            }
            archiveVersion.set(projekt.jarVersion)
        }
        val unpackJarTask = tasks.register<Sync>("unpackJar") {
            val jarTask = tasks.named<Jar>("jar")
            from(zipTree(jarTask.flatMap { it.archiveFile }))
            into(getBuildDirectory("unpacked"))
            dependsOn(jarTask)
        }
        tasks.named("build") {
            finalizedBy(unpackJarTask)
        }
        runExtension<SourceSetContainer> {
            named("main") {
                val generatedDirectory = "src/main/generated"
                resources.srcDirs(generatedDirectory)
                java.srcDirs("$generatedDirectory/java")
            }
        }
        val metadata = projekt.getMetadata()
        if (metadata.isNotEmpty()) {
            requirePlugins("com.github.gmazzo.buildconfig")
            runExtension<BuildConfigExtension> {
                packageName(projekt.packageName)
                className("ProjektMetadata")
                metadata.forEach { field ->
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
