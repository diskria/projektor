package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.settings.configurators.common.SettingsConfigurator
import io.github.diskria.projektor.settings.extensions.dependencyRepositories
import org.gradle.api.initialization.Settings

open class GradlePluginConfigurator(
    val config: GradlePluginConfiguration = GradlePluginConfiguration()
) : SettingsConfigurator() {

    override fun configureRepositories(settings: Settings) {
        applyRepositories(settings)
    }

    companion object {
        fun applyRepositories(settings: Settings) = with(settings) {
            dependencyRepositories {
                gradlePluginPortal()
            }
        }
    }
}
