package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.RepositoriesFilterType
import io.github.diskria.projektor.settings.extensions.configureRepositories
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

data class GradlePlugin(private val projekt: IProjekt, private val settings: Settings) : IProjekt by projekt {

    override val configureRepositories: Settings.() -> Unit = applyRepositories

    companion object {
        val applyRepositories: Settings.() -> Unit = {
            configureRepositories(RepositoriesFilterType.DEPENDENCIES) {
                gradlePluginPortal()
            }
        }
    }
}
