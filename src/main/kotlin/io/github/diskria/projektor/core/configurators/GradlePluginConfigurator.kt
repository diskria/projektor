package io.github.diskria.projektor.core.configurators

import io.github.diskria.projektor.api.GradlePluginDsl
import io.github.diskria.projektor.core.model.GradlePlugin
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.gradleKotlinDsl
import org.gradle.plugin.compatibility.compatibility
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension

internal class GradlePluginConfigurator(
    private val configuration: GradlePluginDsl
) : ProjektConfigurator<GradlePlugin>() {

    override fun buildProjekt(project: Project, projektMetadata: ProjektMetadata): GradlePlugin =
        GradlePlugin.of(project, projektMetadata, configuration)

    override fun configureProject(project: Project, projekt: GradlePlugin) {
        project.pluginManager.apply("java-gradle-plugin")
        project.extensions.configure<GradlePluginDevelopmentExtension> {
            website.set(projekt.metadata.repo.url)
            vcsUrl.set(projekt.metadata.repo.vcsUrl)
            plugins.create(projekt.id).apply {
                id = projekt.id
                implementationClass = "${projekt.packageName}.${projekt.classNamePrefix}GradlePlugin"
                if (projekt is GradlePlugin.Distributable) {
                    displayName = projekt.displayName
                    description = projekt.description
                    tags.set(projekt.tags)
                    project.pluginManager.apply("org.gradle.plugin-compatibility")
                    compatibility {
                        it.features.configurationCache.set(configuration.supportsConfigurationCache)
                    }
                }
            }
        }
        project.dependencies {
            "implementation"(project.gradleKotlinDsl())
        }
    }
}
