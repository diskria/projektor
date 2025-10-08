package io.github.diskria.projektor.settings.repositories

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings

data object PluginRepositories : RepositoriesFilterType {

    override fun configure(settings: Settings, block: RepositoryHandler.() -> Unit) = with(settings) {
        pluginManagement {
            repositories {
                block()
            }
        }
    }
}
