package io.github.diskria.projektor.configurators

import io.github.diskria.projektor.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.projekt.KotlinLibrary
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

open class KotlinLibraryConfigurator(
    val config: KotlinLibraryConfiguration
) : Configurator<KotlinLibrary>() {

    override fun configure(project: Project, projekt: IProjekt): KotlinLibrary = with(project) {
        val kotlinLibrary = KotlinLibrary(projekt, config)
        applyCommonConfiguration(project, kotlinLibrary)
        return kotlinLibrary
    }
}
