package io.github.diskria.projektor.extensions

import com.github.gmazzo.buildconfig.BuildConfigExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import com.modrinth.minotaur.ModrinthExtension
import io.github.diskria.gradle.utils.extensions.hasTask
import io.github.diskria.gradle.utils.extensions.jar
import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.gradle.utils.extensions.withPluginExtension
import io.github.diskria.projektor.projekt.common.BaseProjekt
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.api.fabricapi.FabricApiExtension
import net.ornithemc.ploceus.api.PloceusGradleExtensionApi
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

fun Project.toProjekt(): BaseProjekt =
    BaseProjekt.of(this)

fun Project.getLeafProjects(ignoredProjects: List<String> = emptyList()): List<Project> {
    fun Project.collectLeaves(): List<Project> {
        val validChildren = children().filterNot { it.name in ignoredProjects }
        return if (validChildren.isEmpty()) listOf(this)
        else validChildren.flatMap { it.collectLeaves() }
    }
    return rootProject.collectLeaves()
}

fun Project.getJarTask(): TaskProvider<out Jar> =
    if (hasTask(ShadowJar.SHADOW_JAR_TASK_NAME)) tasks.shadowJar
    else tasks.jar

fun Project.configureJarTask(configuration: Action<in Jar>) {
    getJarTask().configure(configuration)
}

fun Project.children(): List<Project> =
    childProjects.values.toList()

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

fun Project.ploceus(block: PloceusGradleExtensionApi.() -> Unit) {
    withPluginExtension<PloceusGradleExtensionApi>("ploceus", block)
}
