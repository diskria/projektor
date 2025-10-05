package io.github.diskria.projektor.publishing

import com.modrinth.minotaur.ModrinthExtension
import io.github.diskria.gradle.utils.extensions.kotlin.common.gradleError
import io.github.diskria.gradle.utils.extensions.kotlin.requirePlugins
import io.github.diskria.gradle.utils.extensions.kotlin.runExtension
import io.github.diskria.kotlin.utils.extensions.common.className
import io.github.diskria.projektor.projekt.IProjekt
import io.github.diskria.projektor.projekt.MinecraftMod
import org.gradle.api.Project

data object Modrinth : PublishingTarget {

    override val configure: Project.(IProjekt) -> Unit = configure@{ projekt ->
        val mod = projekt as? MinecraftMod
            ?: gradleError(
                "Only Minecraft mod projects supported for publishing to Modrinth" +
                        ", but got " + projekt::class.className()
            )
        requirePlugins("com.modrinth.minotaur")
        runExtension<ModrinthExtension> {
            projectId.set(mod.id)
        }
    }
}
