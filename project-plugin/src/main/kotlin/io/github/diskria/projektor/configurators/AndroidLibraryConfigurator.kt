package io.github.diskria.projektor.configurators

import io.github.diskria.projektor.configurations.AndroidLibraryConfiguration
import io.github.diskria.projektor.projekt.AndroidLibrary
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

open class AndroidLibraryConfigurator(
    val config: AndroidLibraryConfiguration
) : Configurator<AndroidLibrary>() {

    override fun configure(project: Project, projekt: IProjekt): AndroidLibrary {
        val androidLibrary = AndroidLibrary(projekt, config)
        applyCommonConfiguration(project, androidLibrary)
        return androidLibrary
    }
}
