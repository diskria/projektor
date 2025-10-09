package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.extensions.repositories
import org.gradle.api.initialization.Settings

open class AndroidLibraryConfigurator : Configurator() {

    override fun configureRepositories(settings: Settings) = with(settings) {
        super.configureRepositories(settings)
        repositories {
            google()
        }
    }
}
