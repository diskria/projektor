package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.GradlePlugin
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import io.github.diskria.projektor.extensions.isCI
import io.github.diskria.projektor.features.generation.readme.GradlePluginPortalShield
import io.github.diskria.projektor.features.generation.readme.ReadmeShield
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.SecretsHelper
import io.github.diskria.projektor.internal.utils.requireNotNull
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

internal object GradlePluginPortalDistributionTarget : DistributionTarget {

    override fun configureDistributeTask(
        project: Project,
        projekt: Projekt.Distributable,
        projektMetadata: ProjektMetadata,
    ): TaskProvider<out Task> {
        projekt.requireGradlePlugin()
        project.pluginManager.apply("com.gradle.plugin-publish")
        val taskName = if (project.providers.isCI) {
            SecretsHelper(project.providers).requireGradlePublishCredentials()
            "publishPlugins"
        } else {
            "validatePlugins"
        }
        return project.tasks.named(taskName)
    }

    override fun getHomepage(projekt: Projekt.Distributable): String =
        "https://plugins.gradle.org/plugin/${projekt.requireGradlePlugin().id}"

    override fun getReadmeShield(projekt: Projekt.Distributable): ReadmeShield =
        GradlePluginPortalShield(projekt.requireGradlePlugin())

    private fun Projekt.requireGradlePlugin(): GradlePlugin.Distributable =
        Errors.frontend.requireNotNull(this as? GradlePlugin.Distributable) {
            "This kind of project doesn't support distribution to " +
                DistributionTargetType.GRADLE_PLUGIN_PORTAL.displayName
        }
}

private fun SecretsHelper.requireGradlePublishCredentials() {
    gradlePublishKey; gradlePublishSecret
}
