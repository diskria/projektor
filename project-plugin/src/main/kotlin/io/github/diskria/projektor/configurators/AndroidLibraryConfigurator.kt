package io.github.diskria.projektor.configurators

import io.github.diskria.projektor.configurations.AndroidLibraryConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.extensions.toProjekt
import io.github.diskria.projektor.projekt.AndroidLibrary
import org.gradle.api.Project

open class AndroidLibraryConfigurator(
    val config: AndroidLibraryConfiguration = AndroidLibraryConfiguration()
) : ProjectConfigurator<AndroidLibrary>() {

    override fun buildProjekt(project: Project): AndroidLibrary =
        project.toProjekt().toAndroidLibrary(config)

    override fun configureProject(project: Project, projekt: AndroidLibrary) {

    }
}
