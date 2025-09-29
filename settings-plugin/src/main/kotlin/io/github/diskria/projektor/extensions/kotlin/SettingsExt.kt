package io.github.diskria.projektor.extensions.kotlin

import io.github.diskria.projektor.RepositoriesFilterType
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
