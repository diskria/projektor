package io.github.diskria.projektor.settings.extensions

import io.github.diskria.projektor.settings.repositories.DependencyRepositories
import io.github.diskria.projektor.settings.repositories.PluginRepositories
import io.github.diskria.projektor.settings.repositories.RepositoriesFilterType
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings

fun Settings.configureRepositories(filter: RepositoriesFilterType? = null, block: RepositoryHandler.() -> Unit) {
    filter?.configure(this, block) ?: run {
        PluginRepositories.configure(this, block)
        DependencyRepositories.configure(this, block)
    }
}
