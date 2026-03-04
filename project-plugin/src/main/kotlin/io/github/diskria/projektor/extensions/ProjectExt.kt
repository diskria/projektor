package io.github.diskria.projektor.extensions

import com.github.gmazzo.buildconfig.BuildConfigExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.projektor.projekt.common.BaseProjekt
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.invoke
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

fun Project.configureShadowJar(
    projects: List<Project>,
    classifier: String? = null,
    destination: Directory? = null,
    shouldDisableJar: Boolean = false,
    configure: ShadowJar.() -> Unit = {},
) {
    tasks {
        ensurePluginApplied("com.gradleup.shadow")
        val jar = jar.get()

        shadowJar {
            configurations = emptyList()
            destination?.let { destinationDirectory = it }
            copyArchiveName(jar, classifier.orEmpty())
            projects.forEach {
                val jarTask = it.tasks.jar
                dependsOn(jarTask)
                from(zipTree(jarTask.flatMap { jar -> jar.archiveFile }))
            }
            configure()
        }
        if (shouldDisableJar) {
            jar.disable()
        }
    }
}

fun Project.ensureKotlinPluginsApplied() {
    ensurePluginApplied("org.jetbrains.kotlin.jvm")
    ensurePluginApplied("org.jetbrains.kotlin.plugin.serialization")
}

fun Project.toProjekt(): BaseProjekt =
    BaseProjekt.of(this)

fun Project.kotlin(configure: KotlinProjectExtension.() -> Unit = {}) {
    configureExtension<KotlinProjectExtension>(configure)
}

fun Project.buildConfig(configure: BuildConfigExtension.() -> Unit = {}) {
    withPluginExtension<BuildConfigExtension>("com.github.gmazzo.buildconfig", configure)
}

fun Project.publishing(configure: PublishingExtension.() -> Unit = {}) {
    withPluginExtension<PublishingExtension>("maven-publish", configure)
}

fun Project.signing(configure: SigningExtension.() -> Unit = {}) {
    withPluginExtension<SigningExtension>("signing", configure)
}

fun Project.gradlePlugin(configure: GradlePluginDevelopmentExtension.() -> Unit = {}) {
    configureExtension<GradlePluginDevelopmentExtension>(configure)
}
