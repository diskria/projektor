package io.github.diskria.projektor.settings.extensions

import io.github.diskria.projektor.settings.RepositoriesFilterType
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings

fun Settings.configureRepositories(filter: RepositoriesFilterType? = null, block: RepositoryHandler.() -> Unit) {
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
