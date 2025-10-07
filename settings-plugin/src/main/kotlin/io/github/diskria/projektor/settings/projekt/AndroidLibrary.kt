package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.extensions.configureRepositories
import io.github.diskria.projektor.settings.projekt.common.AbstractProjekt
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

open class AndroidLibrary(
    projekt: IProjekt,
    settings: Settings
) : AbstractProjekt(projekt, settings), IProjekt by projekt {

    override fun configureRepositories() {
        applyRepositories(settings)
    }

    companion object {
        fun applyRepositories(settings: Settings) = with(settings) {
            configureRepositories {
                google()
            }
        }
    }
}
