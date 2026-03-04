package io.github.diskria.projektor.configurators

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.projektor.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.extensions.gradlePlugin
import io.github.diskria.projektor.extensions.toProjekt
import io.github.diskria.projektor.projekt.GradlePlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign

open class GradlePluginConfigurator(
    val config: GradlePluginConfiguration = GradlePluginConfiguration()
) : ProjectConfigurator<GradlePlugin>() {

    override fun buildProjekt(project: Project): GradlePlugin =
        project.toProjekt().toGradlePlugin(config)

    override fun configureProject(project: Project, projekt: GradlePlugin) = with(project) {
        gradlePlugin {
            website = projekt.repo.getUrl()
            vcsUrl = projekt.repo.getUrl(isVcs = true)
            plugins {
                create(projekt.id) {
                    id = projekt.id
                    implementationClass = projekt.packageName.appendPackageName(
                        projekt.classNamePrefix + "GradlePlugin"
                    )
                    displayName = projekt.name
                    description = projekt.description
                    tags = projekt.tags
                }
            }
        }
    }
}
