package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.configurations.GradlePluginConfiguration
import io.github.diskria.projektor.settings.extensions.configureRepositories
import io.github.diskria.projektor.settings.projekt.GradlePlugin
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import io.github.diskria.projektor.settings.repositories.DependencyRepositories
import org.gradle.api.initialization.Settings

open class GradlePluginConfigurator(
    val config: GradlePluginConfiguration
) : Configurator<GradlePlugin>() {

    override fun configure(settings: Settings, projekt: IProjekt): GradlePlugin = with(settings) {
        val gradlePlugin = GradlePlugin(projekt, config)
        applyCommonConfiguration(settings, gradlePlugin)
        applyRepositories(this)
        return gradlePlugin
    }

    companion object {
        fun applyRepositories(settings: Settings) = with(settings) {
            configureRepositories(DependencyRepositories) {
                gradlePluginPortal()
            }
        }
    }
}
