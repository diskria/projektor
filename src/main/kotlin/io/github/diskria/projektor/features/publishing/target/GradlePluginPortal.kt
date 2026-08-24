package io.github.diskria.projektor.features.publishing.target

import io.github.diskria.projektor.core.model.GradlePlugin
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.ensurePluginApplied
import io.github.diskria.projektor.extensions.isCI
import io.github.diskria.projektor.features.generation.readme.shields.common.ReadmeShield
import io.github.diskria.projektor.features.generation.readme.shields.live.GradlePluginPortalShield
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.SecretsHelper
import io.github.diskria.projektor.internal.utils.check
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

internal object GradlePluginPortal : ExternalPublishingTarget() {

    override fun configurePublishTask(project: Project, projekt: Projekt): TaskProvider<out Task> = with(project) {
        Errors.frontend.check(projekt is GradlePlugin) {
            "This kind of project doesn't support publishing to Gradle Plugin Portal"
        }
        ensurePluginApplied("com.gradle.plugin-publish")
        if (project.providers.isCI) {
            val secrets = SecretsHelper(project.providers)
            secrets.gradlePublishKey
            secrets.gradlePublishSecret
        }
        return tasks.named("publishPlugins")
    }

    override fun getHomepage(metadata: ProjektMetadata): String =
        "https://plugins.gradle.org/plugin/${metadata.packageName}"

    override fun getReadmeShield(metadata: ProjektMetadata): ReadmeShield =
        GradlePluginPortalShield(metadata)
}
