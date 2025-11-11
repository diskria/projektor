package io.github.diskria.projektor.publishing.external

import io.github.diskria.gradle.utils.extensions.ensurePluginApplied
import io.github.diskria.gradle.utils.helpers.EnvironmentHelper
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.common.metadata.ProjektMetadata
import io.github.diskria.projektor.helpers.SecretsHelper
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.common.Projekt
import io.github.diskria.projektor.publishing.external.common.ExternalPublishingTarget
import io.ktor.http.*
import org.gradle.api.Project

object GradlePluginPortal : ExternalPublishingTarget() {

    override val publishTaskName: String = "publishPlugins"

    override fun configurePublishTask(project: Project, projekt: Projekt): Boolean = with(project) {
        if (projekt !is GradlePlugin) return false
        ensurePluginApplied("com.gradle.plugin-publish")
        if (EnvironmentHelper.isCI()) {
            listOf(SecretsHelper.gradlePublishKey, SecretsHelper.gradlePublishSecret)
        }
        return true
    }

    override fun getHomepage(metadata: ProjektMetadata): Url =
        buildUrl("plugins.gradle.org") {
            path("plugin", metadata.packageNameBase)
        }
}
