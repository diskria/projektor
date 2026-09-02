package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.GradlePlugin
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.features.generation.readme.GradlePluginPortalShield
import io.github.diskria.projektor.features.generation.readme.ReadmeShield
import io.github.diskria.projektor.generated.Envs
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.configure
import org.gradle.plugin.compatibility.compatibility
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

internal object GradlePluginPortalDistributionTarget : DistributionTarget {

    override fun configureDistributeTask(project: Project, projekt: Projekt.Distributable): TaskProvider<out Task> {
        val gradlePlugin = projekt.ensureGradlePlugin()
        project.pluginManager.apply("com.gradle.plugin-publish")
        project.extensions.configure<GradlePluginDevelopmentExtension> {
            website.set(gradlePlugin.metadata.repo.url)
            vcsUrl.set(gradlePlugin.metadata.repo.vcsUrl)
            plugins.getByName(gradlePlugin.id).apply {
                displayName = gradlePlugin.displayName
                description = gradlePlugin.description
                tags.set(gradlePlugin.tags)
                project.pluginManager.apply("org.gradle.plugin-compatibility")
                compatibility {
                    it.features.apply {
                        configurationCache.set(gradlePlugin.configuration.supportsConfigurationCache)
                    }
                }
            }
        }
        val envs = Envs(project.providers)
        val taskName = if (envs.isCI) {
            envs.requireGradlePublishCredentials()
            "publishPlugins"
        } else {
            "validatePlugins"
        }
        return project.tasks.named(taskName)
    }

    override fun getHomepage(projekt: Projekt.Distributable): String =
        "https://plugins.gradle.org/plugin/${projekt.ensureGradlePlugin().id}"

    override fun getReadmeShield(projekt: Projekt.Distributable): ReadmeShield =
        GradlePluginPortalShield(projekt.ensureGradlePlugin())

    private fun Projekt.ensureGradlePlugin(): GradlePlugin.Distributable =
        checkNotNull(this as? GradlePlugin.Distributable) {
            "This kind of project doesn't support distribution to " +
                DistributionTargetType.GRADLE_PLUGIN_PORTAL.displayName
        }
}

private fun Envs.requireGradlePublishCredentials() {
    gradlePublishKey; gradlePublishSecret
}
