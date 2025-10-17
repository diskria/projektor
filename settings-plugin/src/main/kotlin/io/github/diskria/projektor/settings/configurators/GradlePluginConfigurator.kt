package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.settings.configurators.common.Configurator
import io.github.diskria.projektor.settings.extensions.dependencyRepositories
import org.gradle.api.initialization.Settings

open class GradlePluginConfigurator(
    config: GradlePluginConfiguration = GradlePluginConfiguration()
) : Configurator(config) {

    override fun configureRepositories(settings: Settings) = with(settings) {
        dependencyRepositories {
            gradlePluginPortal()
        }
    }
}
