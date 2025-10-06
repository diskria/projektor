package io.github.diskria.projektor.settings.extensions

import io.github.diskria.projektor.settings.RepositoriesFilterType
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings

fun Settings.repositories(block: RepositoryHandler.() -> Unit) =
    configureRepositories(null, block)

fun Settings.dependencyRepositories(block: RepositoryHandler.() -> Unit) =
    configureRepositories(RepositoriesFilterType.DEPENDENCIES, block)

fun Settings.pluginRepositories(block: RepositoryHandler.() -> Unit) =
    configureRepositories(RepositoriesFilterType.PLUGINS, block)

private fun Settings.configureRepositories(filter: RepositoriesFilterType?, block: RepositoryHandler.() -> Unit) {
    when (filter) {
        null -> {
            RepositoriesFilterType.entries.forEach { target ->
                configureRepositories(target, block)
            }
        }

        RepositoriesFilterType.DEPENDENCIES -> {
            dependencyResolutionManagement {
                @Suppress("UnstableApiUsage")
                repositories {
                    block()
                }
            }
        }

        RepositoriesFilterType.PLUGINS -> {
            pluginManagement {
                repositories {
                    block()
                }
            }
        }
    }
}
