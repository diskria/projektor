package io.github.diskria.projektor.extensions

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings

internal fun Settings.dependencyRepositories(block: RepositoryHandler.() -> Unit) {
    @Suppress("UnstableApiUsage") dependencyResolutionManagement.repositories(block)
}
