package io.github.diskria.projektor.api

import io.github.diskria.projektor.core.model.ProjektType

class BuildLogicDsl internal constructor(private val metadataExtension: ProjektMetadataExtension) : ProjektorScope {

    fun gradlePlugin(path: String, name: String? = null) = registerModule(path, ProjektType.GRADLE_PLUGIN, name)
    fun kotlinLibrary(path: String, name: String? = null) = registerModule(path, ProjektType.KOTLIN_LIBRARY, name)

    private fun registerModule(path: String, type: ProjektType, name: String?) {
        metadataExtension.registerModule(path, type, name ?: path.substringAfterLast(":"), isBuildLogic = true)
    }
}
