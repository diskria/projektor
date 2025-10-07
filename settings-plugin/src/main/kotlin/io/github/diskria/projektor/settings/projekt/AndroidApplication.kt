package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.projekt.common.AbstractProjekt
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

class AndroidApplication(
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
        fun applyRepositories(settings: Settings) {
            AndroidLibrary.applyRepositories(settings)
        }
    }
}
