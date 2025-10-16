package io.github.diskria.projektor.settings.projekt.common

import io.github.diskria.projektor.common.projekt.ProjektMetadata
import io.github.diskria.projektor.settings.extensions.*
import org.gradle.api.initialization.Settings

abstract class Projekt {

    open fun configure(settings: Settings, metadata: ProjektMetadata) {
        applyCommonConfiguration(settings)
        configureRepositories(settings)
        configureProjects(settings)
    }

    open fun configureRepositories(settings: Settings) {

    }

    protected open fun configureProjects(settings: Settings) {

    }

    private fun applyCommonConfiguration(settings: Settings) = with(settings) {
        repositories {
            configureMaven(
                "MavenCentralMirror",
                "https://repo1.maven.org/maven2"
            )
            mavenCentral()
        }
        pluginRepositories {
            gradlePluginPortal()
        }
        dependencyRepositories {
            configureGithubPagesMaven("diskria", "projektor")
        }
    }
}
