package io.github.diskria.projektor.api

import io.github.diskria.projektor.core.model.ProjektType
import io.github.diskria.projektor.internal.utils.Errors
import io.github.diskria.projektor.internal.utils.check
import org.gradle.api.initialization.Settings

class MonorepoDsl internal constructor(
    private val settings: Settings,
    private val metadataExtension: ProjektMetadataExtension,
) : ProjektorScope {

    private val includedPaths = mutableSetOf<String>()

    fun gradlePlugin(path: String) {
        metadataExtension.configureGradlePluginRepositories()
        registerModule(path, ProjektType.GRADLE_PLUGIN)
    }

    fun kotlinLibrary(path: String) {
        metadataExtension.configureKotlinLibraryRepositories()
        registerModule(path, ProjektType.KOTLIN_LIBRARY)
    }

    private fun registerModule(path: String, type: ProjektType) {
        Errors.frontend.check(includedPaths.add(path)) {
            "Project path '$path' is already included in monorepo!"
        }
        settings.include(path)
        metadataExtension.registerProjekt(path, type)
    }
}
