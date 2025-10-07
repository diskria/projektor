package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.projekt.common.AbstractProjekt
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

open class AndroidApplication(
    projekt: IProjekt,
    settings: Settings
) : AbstractProjekt(projekt, settings), IProjekt by projekt {

    override fun configureRepositories() {
        applyRepositories(settings)
    }

    companion object {
        fun applyRepositories(settings: Settings) {
            AndroidLibrary.applyRepositories(settings)
        }
    }
}
