package io.github.diskria.projektor.publishing.external

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.`Sentence case`
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.common.`kebab-case`
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.modrinth
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.ktor.http.*
import org.gradle.api.Project

data object Modrinth : ExternalPublishingTarget() {

    override fun configurePublishing(projekt: IProjekt, project: Project) = with(project) {
        val mod = projekt as? MinecraftMod ?: gradleError(
            "Only  projects supported for publishing to Modrinth" +
                    ", but got " + projekt.metadata.type.getName(`Sentence case`)
        )
        modrinth {
            projectId.set(mod.id)
        }
        TODO()
    }

    override fun configureDistributeTask(project: Project) = TODO()

    override fun getPublishTaskName(): String = TODO()

    override fun getHomepage(metadata: ProjektMetadata): String =
        buildUrl("modrinth.com") {
            path("mod", TODO())
        }

    private fun IProjekt.asMinecraftMod(): MinecraftMod =
        this as? MinecraftMod ?: gradleError(
            "Only Minecraft mod projects supported for publishing to Modrinth" +
                    ", but got " + metadata.type.getName(`Sentence case`)
        )
}
