package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.settings.configurators.common.SettingsConfigurator
import io.github.diskria.projektor.settings.configurators.common.dependencyRepositories
import org.gradle.api.initialization.Settings

open class GradlePluginConfigurator(
    val config: GradlePluginConfiguration = GradlePluginConfiguration()
) : SettingsConfigurator() {

    override fun configureRepositories(settings: Settings) {
        applyMainRepositories(settings)
        applyExternalRepositories(settings)
    }

    override fun configureProjects(settings: Settings) {

    }

    companion object {
        fun applyMainRepositories(settings: Settings) = with(settings) {
            dependencyRepositories {
                gradlePluginPortal()
            }
        }

        fun applyExternalRepositories(settings: Settings) = with(settings) {

        }
    }
}
