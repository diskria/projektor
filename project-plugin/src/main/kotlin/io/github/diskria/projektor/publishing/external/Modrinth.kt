package io.github.diskria.projektor.publishing.external

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.modrinth
import io.github.diskria.projektor.projekt.MinecraftMod
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.github.diskria.projektor.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.readme.shields.live.ModrinthShield
import io.ktor.http.*
import org.gradle.api.Project
import org.gradle.api.Task

data object Modrinth : ExternalPublishingTarget() {

    override val publishTaskName: String
        get() = "modrinth"

    override fun configurePublishTask(projekt: Projekt, project: Project): Task? = with(project) {
        val mod = projekt as? MinecraftMod ?: return null
        modrinth {
            projectId.set(mod.id)
        }
        return project.tasks.named(publishTaskName).get()
    }

    override fun getHomepage(metadata: ProjektMetadata): String =
        buildUrl("modrinth.com") {
            path("mod", metadata.repo.name)
        }

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        ModrinthShield(metadata)
}
