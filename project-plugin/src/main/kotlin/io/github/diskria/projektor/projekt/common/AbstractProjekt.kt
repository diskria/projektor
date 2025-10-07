package io.github.diskria.projektor.projekt.common

import com.github.gmazzo.buildconfig.BuildConfigExtension
import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.gradle.utils.extensions.requirePlugins
import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.kotlin.utils.Constants
import io.github.diskria.projektor.extensions.mappers.toInt
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

abstract class AbstractProjekt(val projekt: IProjekt, val projectProvider: () -> Project) {

    protected fun <R> script(block: Project.() -> R): R =
        projectProvider().block()

    open fun configureProject(): Any? = null

    fun configure() {
        applyCommonConfiguration()
        configureProject()
    }

    private fun applyCommonConfiguration() = script {
        requirePlugins("kotlin")
        group = projekt.getNamespace()
        version = projekt.getJarVersion()
        runExtension<BasePluginExtension> {
            archivesName.assign(projekt.repo)
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
                release.set(projekt.getJvmTarget().toInt())
                encoding = Charsets.UTF_8.toString()
            }
        }
        tasks.withType<KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(projekt.getJvmTarget())
            }
        }
        tasks.named<Jar>("jar") {
            from("LICENSE") {
                rename { oldName ->
                    oldName + Constants.Char.UNDERSCORE + projekt.repo
                }
            }
            archiveVersion.set(projekt.getJarVersion())
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
                packageName(projekt.getPackageName())
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
        projekt.publishingTarget?.configure(projekt, this)
    }
}
