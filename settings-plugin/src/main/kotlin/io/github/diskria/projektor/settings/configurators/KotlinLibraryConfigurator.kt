package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.settings.configurators.common.SettingsConfigurator
import org.gradle.api.initialization.Settings

open class KotlinLibraryConfigurator(
    val config: KotlinLibraryConfiguration = KotlinLibraryConfiguration()
) : SettingsConfigurator() {

    override fun configureRepositories(settings: Settings) {
        applyRepositories(settings)
    }

    companion object {
        fun applyRepositories(settings: Settings) = with(settings) {

        }
    }
}
