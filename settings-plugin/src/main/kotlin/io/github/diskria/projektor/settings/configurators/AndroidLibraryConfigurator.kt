package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.configurations.AndroidLibraryConfiguration
import io.github.diskria.projektor.settings.extensions.configureRepositories
import io.github.diskria.projektor.settings.projekt.AndroidLibrary
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

open class AndroidLibraryConfigurator(
    val config: AndroidLibraryConfiguration
) : Configurator<AndroidLibrary>() {

    override fun configure(settings: Settings, projekt: IProjekt): AndroidLibrary = with(settings) {
        val androidLibrary = AndroidLibrary(projekt, config)
        applyCommonConfiguration(settings, androidLibrary)
        applyRepositories(this)
        return androidLibrary
    }

    companion object {
        fun applyRepositories(settings: Settings) = with(settings) {
            configureRepositories {
                google()
            }
        }
    }
}
