package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.configurations.AndroidApplicationConfiguration
import io.github.diskria.projektor.settings.extensions.configureRepositories
import io.github.diskria.projektor.settings.projekt.AndroidApplication
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

open class AndroidApplicationConfigurator(
    val config: AndroidApplicationConfiguration
) : Configurator<AndroidApplication>() {

    override fun configure(settings: Settings, projekt: IProjekt): AndroidApplication = with(settings) {
        val androidApplication = AndroidApplication(projekt, config)
        applyCommonConfiguration(settings, androidApplication)
        configureRepositories {
            google()
        }
        return androidApplication
    }
}
