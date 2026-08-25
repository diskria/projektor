package io.github.diskria.projektor.core.configurators

import io.github.diskria.projektor.api.GradlePluginDsl
import io.github.diskria.projektor.core.model.BaseProjekt
import io.github.diskria.projektor.core.model.GradlePlugin
import io.github.diskria.projektor.extensions.ensurePluginApplied
import io.github.diskria.projektor.extensions.gradlePlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.gradleKotlinDsl
import org.gradle.plugin.compatibility.compatibility

internal open class GradlePluginConfigurator(val configuration: GradlePluginDsl) : ProjectConfigurator<GradlePlugin>() {

    override fun buildProjekt(project: Project): GradlePlugin =
        BaseProjekt.of(project).toGradlePlugin(configuration)

    override fun configureProject(project: Project, projekt: GradlePlugin) {
        project.ensurePluginApplied("java-gradle-plugin")
        project.gradlePlugin {
            website.set(projekt.metadata.repo.url)
            vcsUrl.set(projekt.metadata.repo.vcsUrl)
            plugins.create(projekt.id).apply {
                id = projekt.id
                implementationClass = "${projekt.packageName}.${projekt.classNamePrefix}GradlePlugin"
                displayName = projekt.displayName
                description = projekt.description
                tags.set(projekt.tags)

                project.ensurePluginApplied("org.gradle.plugin-compatibility")
                compatibility {
                    it.features.configurationCache.set(configuration.supportsConfigurationCache)
                }
            }
        }
        project.dependencies {
            "implementation"(project.gradleKotlinDsl())
        }
    }
}
