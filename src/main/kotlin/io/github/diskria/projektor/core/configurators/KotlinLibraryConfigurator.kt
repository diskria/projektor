package io.github.diskria.projektor.core.configurators

import io.github.diskria.projektor.api.KotlinLibraryDsl
import io.github.diskria.projektor.core.model.BaseProjekt
import io.github.diskria.projektor.core.model.KotlinLibrary
import org.gradle.api.Project

internal open class KotlinLibraryConfigurator(
    val configuration: KotlinLibraryDsl
) : ProjectConfigurator<KotlinLibrary>() {

    override fun buildProjekt(project: Project): KotlinLibrary =
        BaseProjekt.of(project).toKotlinLibrary(configuration)

    override fun configureProject(project: Project, projekt: KotlinLibrary) {}
}
