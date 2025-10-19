package io.github.diskria.projektor.publishing.external

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.`Sentence case`
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadataExtra
import io.github.diskria.projektor.extensions.modrinth
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.dynamic.ModrinthShield
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

    override fun getPublishTaskName(): String = TODO()

    override fun getHomepage(metadata: ProjektMetadataExtra): String =
        buildUrl("modrinth.com") {
            path("mod", metadata.repository.name)
        }

    override fun configureDistributeTask(project: Project) = TODO()

    override fun getReadmeShield(metadata: ProjektMetadataExtra): ReadmeShield =
        ModrinthShield(metadata)

    private fun IProjekt.asMinecraftMod(): MinecraftMod =
        this as? MinecraftMod ?: gradleError(
            "Only Minecraft mod projects supported for publishing to Modrinth" +
                    ", but got " + metadata.type.getName(`Sentence case`)
        )
}
