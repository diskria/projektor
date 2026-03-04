package io.github.diskria.projektor.extensions

import com.github.gmazzo.buildconfig.BuildConfigExtension
import io.github.diskria.gradle.utils.extensions.configureExtension
import io.github.diskria.gradle.utils.extensions.ensurePluginApplied
import io.github.diskria.gradle.utils.extensions.withPluginExtension
import io.github.diskria.projektor.projekt.common.BaseProjekt
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

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
