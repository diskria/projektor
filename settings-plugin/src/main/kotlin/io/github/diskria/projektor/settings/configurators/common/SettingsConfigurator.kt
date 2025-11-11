package io.github.diskria.projektor.settings.configurators.common

import io.github.diskria.gradle.utils.extensions.rootDirectory
import io.github.diskria.gradle.utils.helpers.GradleProjects
import io.github.diskria.kotlin.utils.extensions.common.buildUrl
import io.github.diskria.projektor.common.configurators.IProjektConfigurator
import io.github.diskria.projektor.settings.configurators.*
import io.github.diskria.projektor.settings.extensions.configureMaven
import io.ktor.http.*
import org.gradle.api.initialization.Settings

abstract class SettingsConfigurator : IProjektConfigurator {

    fun configure(settings: Settings) {
        applyCommonConfiguration(settings)
        configureRepositories(settings)
        configureProjects(settings)
    }

    protected abstract fun configureRepositories(settings: Settings)

    protected abstract fun configureProjects(settings: Settings)

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

        AndroidApplicationConfigurator.applyExternalRepositories(settings)
        AndroidLibraryConfigurator.applyExternalRepositories(settings)
        GradlePluginConfigurator.applyExternalRepositories(settings)
        KotlinLibraryConfigurator.applyExternalRepositories(settings)
        MinecraftModConfigurator.applyExternalRepositories(settings)

        if (rootDirectory.resolve(GradleProjects.Common.NAME).exists()) {
            include(GradleProjects.Common.PATH)
        }
    }
}
