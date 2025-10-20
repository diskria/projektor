package io.github.diskria.projektor.configurators

import io.github.diskria.gradle.utils.extensions.testImplementation
import io.github.diskria.kotlin.utils.extensions.appendPackageName
import io.github.diskria.projektor.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.extensions.gradlePlugin
import io.github.diskria.projektor.extensions.toProjekt
import io.github.diskria.projektor.projekt.GradlePlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

open class GradlePluginConfigurator(
    val config: GradlePluginConfiguration = GradlePluginConfiguration()
) : ProjectConfigurator<GradlePlugin>() {

    override fun configureProject(project: Project): GradlePlugin = with(project) {
        val gradlePlugin = project.toProjekt().toGradlePlugin(project, config)
        dependencies {
            testImplementation(gradleTestKit())
        }
        gradlePlugin {
            website.set(gradlePlugin.repo.getUrl())
            vcsUrl.set(gradlePlugin.repo.getUrl(isVcs = true))
            plugins {
                create(gradlePlugin.id) {
                    id = gradlePlugin.id
                    implementationClass = gradlePlugin.packageName.appendPackageName(
                        gradlePlugin.classNamePrefix + "GradlePlugin"
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
