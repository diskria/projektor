package io.github.diskria.projektor.publishing.external

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.kotlin.utils.extensions.common.`Sentence case`
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.common.IProjekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.ktor.http.*
import org.gradle.api.Project

data object GradlePluginPortal : ExternalPublishingTarget() {

    override fun configurePublishing(projekt: IProjekt, project: Project) = with(project) {
        val plugin = projekt.asGradlePlugin()
        TODO()
    }

    override fun configureDistributeTask(project: Project) = TODO()

    override fun getPublishTaskName(): String = TODO()

    override fun getHomepage(metadata: ProjektMetadata): String =
        buildUrl("plugins.gradle.org") {
            path("plugin", TODO())
        }

    private fun IProjekt.asGradlePlugin(): GradlePlugin =
        this as? GradlePlugin ?: gradleError(
            "Only Gradle plugin projects supported for publishing to Gradle Plugin Portal" +
                    ", but got " + metadata.type.getName(`Sentence case`)
        )
}
