package io.github.diskria.projektor.core.configurators

import io.github.diskria.projektor.api.KotlinLibraryConfiguration
import io.github.diskria.projektor.core.model.KotlinLibrary
import io.github.diskria.projektor.core.model.metadata.ProjektMetadata
import org.gradle.api.Project

internal class KotlinLibraryConfigurator(
    private val configuration: KotlinLibraryConfiguration
) : ProjektConfigurator<KotlinLibrary, KotlinLibrary.Distributable, KotlinLibrary.BuildLogic>() {

    override fun buildProjekt(project: Project, projektMetadata: ProjektMetadata): KotlinLibrary =
        KotlinLibrary.of(project, projektMetadata, configuration)

    override fun configureProject(project: Project, projekt: KotlinLibrary) {}
}
