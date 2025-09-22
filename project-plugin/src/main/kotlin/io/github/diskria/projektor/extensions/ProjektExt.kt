package io.github.diskria.projektor.extensions

import com.github.gmazzo.buildconfig.BuildConfigExtension
import io.github.diskria.projektor.extensions.mappers.toInt
import io.github.diskria.projektor.projekt.IProjekt
import io.github.diskria.utils.kotlin.Constants
import io.github.diskria.utils.kotlin.poet.Property
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
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

fun <R> Project.base(block: BasePluginExtension.() -> R): R =
    getExtensionOrThrow<BasePluginExtension>().block()

fun <R> Project.java(block: JavaPluginExtension.() -> R): R =
    getExtensionOrThrow<JavaPluginExtension>().block()

fun <R> Project.kotlin(block: KotlinProjectExtension.() -> R): R =
    getExtensionOrThrow<KotlinProjectExtension>().block()

fun <R> Project.sourceSets(block: SourceSetContainer.() -> R): R =
    getExtensionOrThrow<SourceSetContainer>().block()

fun <R> Project.buildConfig(block: BuildConfigExtension.() -> R): R =
    getExtensionOrThrow<BuildConfigExtension>().block()

fun DependencyHandler.testImplementation(dependencyNotation: Any): Dependency? =
    add("testImplementation", dependencyNotation)

fun Project.configureBuildConfig(packageName: String, className: String, fields: () -> List<Property<String>>) {
    buildConfig {
        packageName(packageName)
        className(className)
        fields().forEach { field ->
            buildConfigField(field.name, field.value)
        }
        useKotlinOutput {
            internalVisibility = false
            topLevelConstants = false
        }
    }
}

fun Project.configureProjekt(
    projekt: IProjekt,
    baseArtifactName: String = projekt.slug,
    artifactVersion: String = projekt.version,
) {
    requirePlugins("kotlin")
    group = projekt.owner.namespace
    version = projekt.version
    base {
        archivesName = baseArtifactName
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(projekt.javaVersion))
            vendor.set(JvmVendorSpec.ADOPTIUM)
            implementation.set(JvmImplementation.VENDOR_SPECIFIC)
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
        from("LICENSE") {
            rename { oldName ->
                oldName + Constants.Char.UNDERSCORE + projekt.slug
            }
        }
        archiveVersion.set(artifactVersion)
    }
    val unpackJarTask = tasks.register<Sync>("unpackJar") {
        val jarTask = tasks.named<Jar>("jar")

        from(zipTree(jarTask.flatMap { it.archiveFile }))
        into(buildDirectory("unpacked"))
        dependsOn(jarTask)
    }
    tasks.named("build") {
        finalizedBy(unpackJarTask)
    }
    sourceSets {
        named("main") {
            val generatedDirectory = "src/main/generated"

            resources.srcDirs(generatedDirectory)
            java.srcDirs("$generatedDirectory/java")
        }
    }
}
