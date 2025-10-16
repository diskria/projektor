package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.extensions.repositories
import io.github.diskria.projektor.settings.projekt.common.Projekt
import org.gradle.api.initialization.Settings

data object AndroidLibrary : Projekt() {

    override fun configureRepositories(settings: Settings) = with(settings) {
        repositories {
            google()
        }
    }
}
