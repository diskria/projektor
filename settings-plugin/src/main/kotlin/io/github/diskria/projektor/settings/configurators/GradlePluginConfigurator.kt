package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.extensions.dependencyRepositories
import org.gradle.api.initialization.Settings

open class GradlePluginConfigurator : Configurator() {

    override fun configureRepositories(settings: Settings) = with(settings) {
        super.configureRepositories(settings)
        dependencyRepositories {
            gradlePluginPortal()
        }
    }
}
