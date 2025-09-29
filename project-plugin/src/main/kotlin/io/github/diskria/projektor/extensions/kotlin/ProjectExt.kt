package io.github.diskria.projektor.extensions.kotlin

import com.github.gmazzo.buildconfig.BuildConfigExtension
import com.modrinth.minotaur.ModrinthExtension
import io.github.diskria.gradle.utils.extensions.kotlin.getExtensionOrThrow
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.api.fabricapi.FabricApiExtension
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

fun <R> Project.gradlePlugin(block: GradlePluginDevelopmentExtension.() -> R): R =
    getExtensionOrThrow<GradlePluginDevelopmentExtension>().block()

fun <R> Project.fabric(block: LoomGradleExtensionAPI.() -> R): R =
    getExtensionOrThrow<LoomGradleExtensionAPI>().block()

fun <R> Project.fabricApi(block: FabricApiExtension.() -> R): R =
    getExtensionOrThrow<FabricApiExtension>().block()

fun <R> Project.modrinth(block: ModrinthExtension.() -> R): R =
    getExtensionOrThrow<ModrinthExtension>().block()

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

fun <R> Project.signing(block: SigningExtension.() -> R): R =
    getExtensionOrThrow<SigningExtension>().block()

fun <R> Project.publishing(block: PublishingExtension.() -> R): R =
    getExtensionOrThrow<PublishingExtension>().block()
