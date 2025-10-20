package io.github.diskria.projektor.configurators

import io.github.diskria.projektor.configurations.AndroidLibraryConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.extensions.toProjekt
import io.github.diskria.projektor.projekt.AndroidLibrary
import io.github.diskria.projektor.projekt.common.Projekt
import org.gradle.api.Project

open class AndroidLibraryConfigurator(
    val config: AndroidLibraryConfiguration = AndroidLibraryConfiguration()
) : ProjectConfigurator<AndroidLibrary>() {

    override fun configureProject(project: Project): AndroidLibrary {
        val androidLibrary = project.toProjekt().toAndroidLibrary(project, config)
        return androidLibrary
    }
}
