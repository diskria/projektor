package io.github.diskria.projektor.settings.repositories

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings

data object DependencyRepositories : RepositoriesFilterType {

    override fun configure(settings: Settings, block: RepositoryHandler.() -> Unit) = with(settings) {
        dependencyResolutionManagement {
            @Suppress("UnstableApiUsage")
            repositories {
                block()
            }
        }
    }
}
