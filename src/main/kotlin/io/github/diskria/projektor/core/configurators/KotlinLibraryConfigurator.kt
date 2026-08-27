package io.github.diskria.projektor.core.configurators

import io.github.diskria.projektor.api.KotlinLibraryDsl
import io.github.diskria.projektor.core.model.KotlinLibrary
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import org.gradle.api.Project

internal class KotlinLibraryConfigurator(
    private val configuration: KotlinLibraryDsl
) : ProjektConfigurator<KotlinLibrary>() {

    override fun buildProjekt(project: Project, projektMetadata: ProjektMetadata): KotlinLibrary =
        KotlinLibrary.of(project, projektMetadata, configuration)

    override fun configureProject(project: Project, projekt: KotlinLibrary) {}
}
