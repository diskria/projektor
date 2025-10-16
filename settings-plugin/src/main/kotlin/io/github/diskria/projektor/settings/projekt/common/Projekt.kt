package io.github.diskria.projektor.settings.projekt.common

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.common.projekt.metadata.ProjektMetadata
import io.github.diskria.projektor.settings.extensions.*
import io.ktor.http.*
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
                buildUrl("repo1.maven.org") {
                    path("maven2")
                }
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
