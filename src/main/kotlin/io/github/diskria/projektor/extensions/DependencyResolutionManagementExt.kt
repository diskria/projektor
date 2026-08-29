package io.github.diskria.projektor.extensions

import io.github.diskria.projektor.core.model.ProjektType
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.resolve.DependencyResolutionManagement
import org.gradle.kotlin.dsl.maven

internal fun DependencyResolutionManagement.configureRepositories(projektType: ProjektType) = when (projektType) {
    ProjektType.GRADLE_PLUGIN -> with(repositories) {
        gradlePluginPortal()
        mavenCentrals()
    }

    ProjektType.KOTLIN_LIBRARY -> repositories.mavenCentrals()
}

private fun RepositoryHandler.mavenCentrals() {
    mavenCentral { it.name = "ApacheMavenCentral" }
    maven("https://repo1.maven.org/maven2") { name = "SonatypeMavenCentral" }
}
