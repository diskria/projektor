package io.github.diskria.projektor.publishing

import com.modrinth.minotaur.ModrinthExtension
import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.requirePlugins
import io.github.diskria.gradle.utils.extensions.runExtension
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

data object Modrinth : PublishingTarget {

    override fun configure(projekt: IProjekt, project: Project) = with(project) {
        val mod = projekt as? MinecraftMod ?: gradleError(
            "Only Minecraft mod projects supported for publishing to Modrinth" +
                    ", but got " + projekt::class.className()
        )
        requirePlugins("com.modrinth.minotaur")
        runExtension<ModrinthExtension> {
            projectId.set(mod.id)
        }
    }
}
