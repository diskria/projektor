package io.github.diskria.projektor.publishing.external

import com.gradle.publish.PublishTask
import io.github.diskria.gradle.utils.extensions.ensurePluginApplied
import io.github.diskria.gradle.utils.extensions.getTask
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.Secrets
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.ktor.http.*
import org.gradle.api.Project

data object GradlePluginPortal : ExternalPublishingTarget() {

    override fun getPublishTaskName(project: Project): String =
        project.getTask<PublishTask>().name

    override fun configurePublishTask(projekt: Projekt, project: Project): Boolean = with(project) {
        val plugin = projekt as? GradlePlugin ?: return false
        project.ensurePluginApplied("com.gradle.plugin-publish")
        if (EnvironmentHelper.isCI()) {
            listOf(Secrets.gradlePublishKey, Secrets.gradlePublishSecret)
        }
        return true
    }

    override fun getHomepage(metadata: ProjektMetadata): String =
        buildUrl("plugins.gradle.org") {
            path("plugin", metadata.packageNameBase)
        }
}
