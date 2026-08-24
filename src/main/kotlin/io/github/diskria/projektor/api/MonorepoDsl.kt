package io.github.diskria.projektor.api

import io.github.diskria.projektor.core.model.ProjektType
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.check
import org.gradle.api.initialization.Settings

class MonorepoDsl internal constructor(
    private val settings: Settings,
    private val rootExtension: ProjektMetadataExtension,
) : ProjektorScope {

    private val includedPaths = mutableSetOf<String>()

    fun gradlePlugin(path: String) {
        registerModule(path, ProjektType.GRADLE_PLUGIN)
        rootExtension.configureGradlePluginRepositories()
    }

    fun kotlinLibrary(path: String) {
        registerModule(path, ProjektType.KOTLIN_LIBRARY)
        rootExtension.configureKotlinLibraryRepositories()
    }

    private fun registerModule(path: String, type: ProjektType) {
        Errors.frontend.check(includedPaths.add(path)) {
            "Project path '$path' is already included in monorepo!"
        }

        settings.include(path)
        rootExtension.registerProjektModule(path, type)
    }
}
