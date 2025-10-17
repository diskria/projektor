package io.github.diskria.projektor.extensions

import com.github.gmazzo.buildconfig.BuildConfigExtension
import com.modrinth.minotaur.ModrinthExtension
import io.github.diskria.gradle.utils.extensions.ensurePluginApplied
import io.github.diskria.gradle.utils.extensions.hasTask
import io.github.diskria.gradle.utils.extensions.registerTask
import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.api.fabricapi.FabricApiExtension
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.internal.extensions.core.extra
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

fun Project.base(block: BasePluginExtension.() -> Unit) {
    runExtension<BasePluginExtension>(block)
}

fun Project.java(block: JavaPluginExtension.() -> Unit) {
    runExtension<JavaPluginExtension>(block)
}

fun Project.kotlin(block: KotlinProjectExtension.() -> Unit) {
    runExtension<KotlinProjectExtension>(block)
}

fun Project.buildConfig(block: BuildConfigExtension.() -> Unit) {
    withPluginExtension<BuildConfigExtension>("com.github.gmazzo.buildconfig", block)
}

fun Project.sourceSets(block: SourceSetContainer.() -> Unit) {
    runExtension<SourceSetContainer>(block)
}

fun Project.publishing(block: PublishingExtension.() -> Unit) {
    withPluginExtension<PublishingExtension>("maven-publish", block)
}

fun Project.signing(block: SigningExtension.() -> Unit) {
    withPluginExtension<SigningExtension>("signing", block)
}

fun Project.gradlePlugin(block: GradlePluginDevelopmentExtension.() -> Unit) {
    runExtension<GradlePluginDevelopmentExtension>(block)
}

fun Project.loom(block: LoomGradleExtensionAPI.() -> Unit) {
    withPluginExtension<LoomGradleExtensionAPI>("fabric-loom", block)
}

fun Project.fabric(block: FabricApiExtension.() -> Unit) {
    withPluginExtension<FabricApiExtension>("fabric-loom", block)
}

fun Project.modrinth(block: ModrinthExtension.() -> Unit) {
    withPluginExtension<ModrinthExtension>("com.modrinth.minotaur", block)
}

inline fun <reified T : Task> Project.ensureTaskRegistered(noinline configuration: T.() -> Unit = {}) {
    if (hasTask<T>()) {
        return
    }
    registerTask<T>(configuration)
}

private inline fun <reified E : Any> Project.withPluginExtension(pluginId: String, block: E.() -> Unit) {
    ensurePluginApplied(pluginId)
    runExtension<E>(block)
}

fun Project.getMetadata(): ProjektMetadata {
    val projektMetadata: ProjektMetadata by rootProject.extra.properties
    return projektMetadata
}
