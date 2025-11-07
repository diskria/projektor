package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.configurations.AndroidApplicationConfiguration
import io.github.diskria.projektor.settings.configurators.common.SettingsConfigurator
import io.github.diskria.projektor.settings.configurators.common.repositories
import org.gradle.api.initialization.Settings

open class AndroidApplicationConfigurator(
    val config: AndroidApplicationConfiguration = AndroidApplicationConfiguration()
) : SettingsConfigurator() {

    override fun configureRepositories(settings: Settings) {
        applyMainRepositories(settings)
        applyExternalRepositories(settings)
    }

    override fun configureProjects(settings: Settings) {

    }

    companion object {
        fun applyMainRepositories(settings: Settings) = with(settings) {
            repositories {
                google()
            }
        }

        fun applyExternalRepositories(settings: Settings) = with(settings) {

        }
    }
}
