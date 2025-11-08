package io.github.diskria.projektor.settings.configurators.common

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings

fun Settings.pluginRepositories(block: RepositoryHandler.() -> Unit) {
    pluginManagement {
        repositories {
            block()
        }
    }
}

fun Settings.dependencyRepositories(block: RepositoryHandler.() -> Unit) {
    dependencyResolutionManagement {
        repositories {
            block()
        }
    }
}

fun Settings.repositories(block: RepositoryHandler.() -> Unit) {
    pluginRepositories(block)
    dependencyRepositories(block)
}
