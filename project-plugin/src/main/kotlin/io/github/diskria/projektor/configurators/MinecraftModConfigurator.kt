package io.github.diskria.projektor.configurators

import io.github.diskria.gradle.utils.extensions.findCommonProject
import io.github.diskria.gradle.utils.extensions.getBuildDirectory
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.minecraft.loaders.ModLoaderFamily
import io.github.diskria.projektor.configurations.minecraft.MinecraftModConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.extensions.configureShadowJar
import io.github.diskria.projektor.extensions.ensureKotlinPluginsApplied
import io.github.diskria.projektor.extensions.toProjekt
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project

open class MinecraftModConfigurator(
    val config: MinecraftModConfiguration = MinecraftModConfiguration()
) : ProjectConfigurator<MinecraftMod>() {

    override fun buildProjekt(project: Project): MinecraftMod =
        project.toProjekt().toMinecraftMod(project, config)

    override fun configureProject(project: Project, projekt: MinecraftMod) = with(project) {
        val modProject = project
        val mod = projekt
        val sides = mod.config.environment.sides
        val sideProjects = sides.associateWith {
            val sideProject = modProject.project(it.getName())
            sideProject.ensureKotlinPluginsApplied()
            return@associateWith sideProject
        }
        val projectsToShadow = buildList {
            rootProject.findCommonProject()?.let { add(it) }
            addAll(sideProjects.values)
        }
        val isFabricFamily = mod.loader.family == ModLoaderFamily.FABRIC
        configureShadowJar(
            projects = projectsToShadow,
            classifier = if (isFabricFamily) "dev" else null,
            destination = if (isFabricFamily) getBuildDirectory("devlibs").get() else null,
            shouldDisableJar = true,
        )
        mod.loader.configure(modProject, sideProjects, mod)
    }
}
