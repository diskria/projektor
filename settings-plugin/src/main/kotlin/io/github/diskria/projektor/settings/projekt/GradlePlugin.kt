package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.extensions.dependencyRepositories
import io.github.diskria.projektor.settings.projekt.common.Projekt
import org.gradle.api.initialization.Settings

data object GradlePlugin : Projekt() {

    override fun configureRepositories(settings: Settings) = with(settings) {
        dependencyRepositories {
            gradlePluginPortal()
        }
    }
}
