package io.github.diskria.projektor.configurators

import io.github.diskria.gradle.utils.extensions.testImplementation
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.projektor.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.extensions.gradlePlugin
import io.github.diskria.projektor.projekt.GradlePlugin
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

open class GradlePluginConfigurator(
    val config: GradlePluginConfiguration
) : Configurator<GradlePlugin>() {

    override fun configure(project: Project, projekt: IProjekt): GradlePlugin = with(project) {
        dependencies {
            testImplementation(gradleTestKit())
        }
        val gradlePlugin = GradlePlugin(projekt, config)
        applyCommonConfiguration(project, gradlePlugin)
        gradlePlugin {
            website.set(gradlePlugin.metadata.repository.getUrl())
            vcsUrl.set(gradlePlugin.metadata.repository.getPath(isVcs = true))
            plugins {
                create(gradlePlugin.id) {
                    id = gradlePlugin.id
                    implementationClass = gradlePlugin.packageName.appendPackageName(
                        gradlePlugin.classNamePrefix + "GradlePlugin"
                    )
                    displayName = gradlePlugin.metadata.name
                    description = gradlePlugin.metadata.description
                    tags.set(gradlePlugin.metadata.tags)
                }
            }
        }
        return gradlePlugin
    }
}
