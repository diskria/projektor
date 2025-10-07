package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.extensions.configureRepositories
import io.github.diskria.projektor.settings.projekt.common.AbstractProjekt
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

class AndroidLibrary(
    projekt: IProjekt,
    settingsProvider: () -> Settings
) : AbstractProjekt(
    projekt,
    settingsProvider
), IProjekt by projekt {

    override fun configureRepositories() {
        script {
            applyRepositories(this)
        }
    }

    companion object {
        fun applyRepositories(settings: Settings) = with(settings) {
            configureRepositories {
                google()
            }
        }
    }
}
