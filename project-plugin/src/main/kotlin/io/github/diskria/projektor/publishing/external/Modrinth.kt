package io.github.diskria.projektor.publishing.external

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.`Sentence case`
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.modrinth
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.live.ModrinthShield
import io.ktor.http.*
import org.gradle.api.Project

data object Modrinth : ExternalPublishingTarget() {

    override val publishTaskName: String get() = TODO()

    override fun configure(projekt: Projekt, project: Project) = with(project) {
        val mod = projekt.asMinecraftMod()
        modrinth {
            projectId.set(mod.id)
        }
        TODO()
    }

    override fun getHomepage(metadata: ProjektMetadata): String =
        buildUrl("modrinth.com") {
            path("mod", metadata.repo.name)
        }

    override fun configureDistributeTask(rootProject: Project) = TODO()

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        ModrinthShield(metadata)

    private fun Projekt.asMinecraftMod(): MinecraftMod =
        this as? MinecraftMod ?: gradleError(
            "Only Minecraft mod projects supported for publishing to Modrinth" +
                    ", but got " + type.getName(`Sentence case`)
        )
}
