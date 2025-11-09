package io.github.diskria.projektor.extensions

import com.github.gmazzo.buildconfig.BuildConfigExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar
import com.modrinth.minotaur.ModrinthExtension
import io.github.diskria.gradle.utils.extensions.*
import io.github.diskria.projektor.projekt.common.BaseProjekt
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.legacyfabric.legacylooming.LegacyUtilsExtension
import net.minecraftforge.gradle.userdev.UserDevExtension
import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import net.ornithemc.ploceus.api.PloceusGradleExtensionApi
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
    configuration: ShadowJar.() -> Unit = {},
) {
    tasks {
        ensurePluginApplied("com.gradleup.shadow")
        val jar = jar.get()

        shadowJar {
            configurations = emptyList()
            copyArchiveNameParts(jar, classifier.orEmpty())
            destination?.let { destinationDirectory = it }
            projects.forEach {
                val jarTask = it.tasks.jar
                dependsOn(jarTask)
                from(zipTree(jarTask.flatMap { jar -> jar.archiveFile }))
            }
            configuration()
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

fun Project.kotlin(configuration: KotlinProjectExtension.() -> Unit = {}) {
    configureExtension<KotlinProjectExtension>(configuration)
}

fun Project.buildConfig(configuration: BuildConfigExtension.() -> Unit = {}) {
    withPluginExtension<BuildConfigExtension>("com.github.gmazzo.buildconfig", configuration)
}

fun Project.publishing(configuration: PublishingExtension.() -> Unit = {}) {
    withPluginExtension<PublishingExtension>("maven-publish", configuration)
}

fun Project.signing(configuration: SigningExtension.() -> Unit = {}) {
    withPluginExtension<SigningExtension>("signing", configuration)
}

fun Project.gradlePlugin(configuration: GradlePluginDevelopmentExtension.() -> Unit = {}) {
    configureExtension<GradlePluginDevelopmentExtension>(configuration)
}

fun Project.modrinth(configuration: ModrinthExtension.() -> Unit = {}) {
    withPluginExtension<ModrinthExtension>("com.modrinth.minotaur", configuration)
}

fun Project.fabric(configuration: LoomGradleExtensionAPI.() -> Unit = {}) {
    withPluginExtension<LoomGradleExtensionAPI>("fabric-loom", configuration)
}

fun Project.legacyFabric(configuration: LegacyUtilsExtension.() -> Unit = {}) {
    withPluginExtension<LegacyUtilsExtension>("legacy-looming", configuration)
}

fun Project.ornithe(configuration: PloceusGradleExtensionApi.() -> Unit = {}) {
    withPluginExtension<PloceusGradleExtensionApi>("ploceus", configuration)
}

fun Project.forge(configuration: UserDevExtension.() -> Unit = {}) {
    withPluginExtension<UserDevExtension>("net.minecraftforge.gradle", configuration)
}

fun Project.neoforge(configuration: NeoForgeExtension.() -> Unit = {}) {
    withPluginExtension<NeoForgeExtension>("net.neoforged.moddev", configuration)
}
