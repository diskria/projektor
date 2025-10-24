package io.github.diskria.projektor.settings.extensions

import com.gradle.develocity.agent.gradle.DevelocityConfiguration
import io.github.diskria.gradle.utils.extensions.withPluginExtension
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
        @Suppress("UnstableApiUsage")
        repositories {
            block()
        }
    }
}

fun Settings.repositories(block: RepositoryHandler.() -> Unit) {
    pluginRepositories(block)
    dependencyRepositories(block)
}

fun Settings.develocity(block: DevelocityConfiguration.() -> Unit) {
    withPluginExtension<DevelocityConfiguration>("com.gradle.develocity", block)
}
