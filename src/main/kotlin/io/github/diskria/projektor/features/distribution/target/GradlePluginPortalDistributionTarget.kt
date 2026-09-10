package io.github.diskria.projektor.features.distribution.target

import io.github.diskria.projektor.core.model.DistributionTargetType
import io.github.diskria.projektor.core.model.GradlePlugin
import io.github.diskria.projektor.core.model.Projekt
import io.github.diskria.projektor.features.generation.readme.GradlePluginPortalShield
import io.github.diskria.projektor.features.generation.readme.ReadmeShield
import io.github.diskria.projektor.generated.EnvProvider
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.plugin.compatibility.compatibility
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

internal object GradlePluginPortalDistributionTarget : DistributionTarget {

    override fun configureDistributeTasks(project: Project, projekt: Projekt.Distributable): List<String> {
        val pluginProjekt = projekt.ensureGradlePlugin()
        val supportsConfigurationCache = pluginProjekt.configuration.supportsConfigurationCache.get()
        val supportsIsolatedProjects = pluginProjekt.configuration.supportsIsolatedProjects.get()
        if (supportsIsolatedProjects && !supportsConfigurationCache) {
            throw IllegalArgumentException(
                "Invalid Gradle feature configuration for plugin '${pluginProjekt.id}': " +
                    "Isolated Projects require Configuration Cache to be enabled. " +
                    "Please enable Configuration Cache (supportsConfigurationCache = true) " +
                    "or disable Isolated Projects (supportsIsolatedProjects = false)."
            )
        }
        project.pluginManager.apply("com.gradle.plugin-publish")
        project.extensions.configure<GradlePluginDevelopmentExtension> {
            website.set(projekt.metadata.repo.url)
            vcsUrl.set(projekt.metadata.repo.vcsUrl)
            plugins.named(projekt.internalName).configure { plugin ->
                plugin.displayName = projekt.displayName
                plugin.description = projekt.description
                plugin.tags.set(pluginProjekt.tags)
                project.pluginManager.apply("org.gradle.plugin-compatibility")
                plugin.compatibility { compat ->
                    compat.features.configurationCache.set(supportsConfigurationCache)
                    compat.features.isolatedProjects.set(supportsIsolatedProjects)
                }
            }
        }
        val env = EnvProvider(project.providers)
        return if (env.isCI) {
            env.requireGradlePublishCredentials()
            listOf("publishPlugins")
        } else {
            listOf("validatePlugins")
        }
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

private fun EnvProvider.requireGradlePublishCredentials() {
    gradlePublishKey; gradlePublishSecret
}
