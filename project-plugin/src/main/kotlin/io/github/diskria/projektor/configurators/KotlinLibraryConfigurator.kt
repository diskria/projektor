package io.github.diskria.projektor.configurators

import io.github.diskria.projektor.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.configurators.common.ProjectConfigurator
import io.github.diskria.projektor.projekt.KotlinLibrary
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

open class KotlinLibraryConfigurator(
    val config: KotlinLibraryConfiguration = KotlinLibraryConfiguration()
) : ProjectConfigurator<KotlinLibrary>() {

    override fun configureProject(project: Project, projekt: IProjekt): KotlinLibrary =
        KotlinLibrary(projekt, config)
}
