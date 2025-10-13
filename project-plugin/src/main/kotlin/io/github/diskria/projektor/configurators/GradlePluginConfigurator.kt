package io.github.diskria.projektor.configurators

import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.projektor.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.extensions.gradlePlugin
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

open class GradlePluginConfigurator(
    val config: GradlePluginConfiguration
) : Configurator<GradlePlugin>() {

    override fun configure(project: Project, projekt: IProjekt): GradlePlugin = with(project) {
        val gradlePlugin = GradlePlugin(projekt, config)
        applyCommonConfiguration(project, gradlePlugin)
        gradlePlugin {
            website.set(gradlePlugin.getRepoUrl())
            vcsUrl.set(gradlePlugin.getRepoPath(isVcs = true))
            plugins {
                create(gradlePlugin.id) {
                    id = gradlePlugin.id
                    implementationClass = gradlePlugin.packageName.appendPackageName(
                        gradlePlugin.classNameBase + "GradlePlugin"
                    )
                    displayName = gradlePlugin.name
                    description = gradlePlugin.description
                    tags.set(gradlePlugin.tags)
                }
            }
        }
        return gradlePlugin
    }
}
