package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.configurations.AndroidApplicationConfiguration
import io.github.diskria.projektor.settings.configurators.common.SettingsConfigurator
import io.github.diskria.projektor.settings.extensions.repositories
import org.gradle.api.initialization.Settings

open class AndroidApplicationConfigurator(
    val config: AndroidApplicationConfiguration = AndroidApplicationConfiguration()
) : SettingsConfigurator() {

    override fun configureRepositories(settings: Settings) {
        applyRepositories(settings)
    }

    companion object {
        fun applyRepositories(settings: Settings) = with(settings) {
            repositories {
                google()
            }
        }
    }
}
