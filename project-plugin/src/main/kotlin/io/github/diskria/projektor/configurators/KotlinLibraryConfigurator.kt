package io.github.diskria.projektor.configurators

import io.github.diskria.projektor.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.extensions.toProjekt
import io.github.diskria.projektor.projekt.KotlinLibrary
import org.gradle.api.Project

open class KotlinLibraryConfigurator(
    val config: KotlinLibraryConfiguration = KotlinLibraryConfiguration()
) : ProjectConfigurator<KotlinLibrary>() {

    override fun configureProject(project: Project): KotlinLibrary {
        val kotlinLibrary = project.toProjekt().toKotlinLibrary(project, config)
        return kotlinLibrary
    }
}
