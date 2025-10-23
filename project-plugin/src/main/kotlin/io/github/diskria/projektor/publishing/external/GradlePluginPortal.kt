package io.github.diskria.projektor.publishing.external

import io.github.diskria.gradle.utils.extensions.common.gradleError
import io.github.diskria.gradle.utils.extensions.ensurePluginApplied
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.extensions.common.`Sentence case`
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.kotlin.utils.extensions.mappers.getName
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.ktor.http.*
import org.gradle.api.Project

data object GradlePluginPortal : ExternalPublishingTarget() {

    override fun configure(projekt: Projekt, project: Project) {
        val gradlePlugin = projekt.asGradlePlugin()
        project.ensurePluginApplied("com.gradle.plugin-publish")
        if (!EnvironmentHelper.isCI()) {
            return
        }
        listOf(Secrets.gradlePublishKey, Secrets.gradlePublishSecret)
    }

    override fun getPublishTaskName(): String =
        "publishPlugins"

    override fun getHomepage(metadata: ProjektMetadata): String =
        buildUrl("plugins.gradle.org") {
            path("plugin", metadata.packageNameBase)
        }

    private fun Projekt.asGradlePlugin(): GradlePlugin =
        this as? GradlePlugin ?: gradleError(
            "Only Gradle plugin projects supported for publishing to Gradle Plugin Portal" +
                    ", but got " + type.getName(`Sentence case`)
        )
}
