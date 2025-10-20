package io.github.diskria.projektor.settings.configurators.common

import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.common.configurators.IProjektConfigurator
import io.github.diskria.projektor.settings.ProjektBuildConfig
import io.github.diskria.projektor.settings.extensions.*
import io.ktor.http.*
import org.gradle.api.initialization.Settings

abstract class SettingsConfigurator : IProjektConfigurator {

    fun configure(settings: Settings) {
        applyCommonConfiguration(settings)
        configureRepositories(settings)
        configureProjects(settings)
    }

    protected open fun configureRepositories(settings: Settings) {

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
            configureGithubPagesMaven("diskria", ProjektBuildConfig.PLUGIN_NAME)
        }
    }
}
