package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.extra
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal var Project.projektMetadata: ProjektMetadata
    get() = rootProject.extra["projektMetadata"] as ProjektMetadata
    set(value) {
        rootProject.extra["projektMetadata"] = value
    }

internal fun Project.kotlin(configure: KotlinProjectExtension.() -> Unit) {
    configureExtension<KotlinProjectExtension>(configure)
}

internal fun Project.publishing(configure: PublishingExtension.() -> Unit) {
    configureExtension<PublishingExtension>(configure)
}

internal fun Project.signing(configure: SigningExtension.() -> Unit) {
    configureExtension<SigningExtension>(configure)
}

internal fun Project.gradlePlugin(configure: GradlePluginDevelopmentExtension.() -> Unit) {
    configureExtension<GradlePluginDevelopmentExtension>(configure)
}

internal fun Project.base(configure: BasePluginExtension.() -> Unit) {
    configureExtension<BasePluginExtension>(configure)
}

internal fun Project.java(configure: JavaPluginExtension.() -> Unit) {
    configureExtension<JavaPluginExtension>(configure)
}
