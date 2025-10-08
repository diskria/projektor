package io.github.diskria.projektor.settings.repositories

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings

sealed interface RepositoriesFilterType {
    fun configure(settings: Settings, block: RepositoryHandler.() -> Unit)
}
