package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.settings.configurators.common.SettingsConfigurator
import org.gradle.api.initialization.Settings

open class KotlinLibraryConfigurator(
    val config: KotlinLibraryConfiguration = KotlinLibraryConfiguration()
) : SettingsConfigurator() {

    override fun configureRepositories(settings: Settings) {
        applyMainRepositories(settings)
        applyExternalRepositories(settings)
    }

    override fun configureProjects(settings: Settings) {

    }

    companion object {
        fun applyMainRepositories(settings: Settings) = with(settings) {

        }

        fun applyExternalRepositories(settings: Settings) = with(settings) {

        }
    }
}
