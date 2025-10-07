package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.extensions.configureRepositories
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

data class AndroidLibrary(private val projekt: IProjekt, private val settings: Settings) : IProjekt by projekt {

    override val configureRepositories: Settings.() -> Unit = applyRepositories

    companion object {
        val applyRepositories: Settings.() -> Unit = {
            configureRepositories {
                google()
            }
        }
    }
}
