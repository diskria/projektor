package io.github.diskria.projektor.core.configurators

import io.github.diskria.projektor.api.KotlinLibraryDsl
import io.github.diskria.projektor.core.model.KotlinLibrary
import org.gradle.api.Project

internal open class KotlinLibraryConfigurator(
    val configuration: KotlinLibraryDsl
) : ProjectConfigurator<KotlinLibrary>() {

    override fun buildProjekt(project: Project): KotlinLibrary = KotlinLibrary.of(project, configuration)

    override fun configureProject(project: Project, projekt: KotlinLibrary) {}
}
