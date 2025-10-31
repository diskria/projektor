package io.github.diskria.projektor.configurators

import io.github.diskria.projektor.configurations.MinecraftModConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.extensions.toProjekt
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project

open class MinecraftModConfigurator(
    val config: MinecraftModConfiguration = MinecraftModConfiguration()
) : ProjectConfigurator<MinecraftMod>() {

    override fun configureProject(project: Project): MinecraftMod = with(project) {
        val mod = project.toProjekt().toMinecraftMod(project, config)
        mod.loader.configure(project, mod)
        return mod
    }
}
