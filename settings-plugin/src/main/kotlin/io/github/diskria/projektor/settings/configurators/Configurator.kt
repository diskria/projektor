package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.common.projekt.ProjektMetadata
import io.github.diskria.projektor.settings.extensions.configureMaven
import io.github.diskria.projektor.settings.extensions.pluginRepositories
import io.github.diskria.projektor.settings.extensions.repositories
import org.gradle.api.initialization.Settings

sealed class Configurator {

    open fun configure(settings: Settings, metadata: ProjektMetadata) {
        configureRepositories(settings)
        configureProjects(settings)
    }

    protected open fun configureRepositories(settings: Settings) = with(settings) {
        repositories {
            configureMaven(
                "Projektor",
                "https://diskria.github.io/projektor"
            )
            configureMaven(
                "MavenCentralMirror",
                "https://repo1.maven.org/maven2"
            )
            mavenCentral()
        }
        pluginRepositories {
            gradlePluginPortal()
        }
    }

    protected open fun configureProjects(settings: Settings) {

    }
}
