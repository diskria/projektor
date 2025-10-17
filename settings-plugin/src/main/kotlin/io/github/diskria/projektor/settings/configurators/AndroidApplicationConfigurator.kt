package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.configurations.AndroidApplicationConfiguration
import io.github.diskria.projektor.settings.configurators.common.Configurator
import io.github.diskria.projektor.settings.extensions.repositories
import org.gradle.api.initialization.Settings

open class AndroidApplicationConfigurator(
    config: AndroidApplicationConfiguration = AndroidApplicationConfiguration()
) : Configurator(config) {

    override fun configureRepositories(settings: Settings) = with(settings) {
        repositories {
            google()
        }
    }
}
