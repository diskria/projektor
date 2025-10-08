package io.github.diskria.projektor.settings.configurators

import io.github.diskria.projektor.settings.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.settings.projekt.KotlinLibrary
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

open class KotlinLibraryConfigurator(
    val config: KotlinLibraryConfiguration
) : Configurator<KotlinLibrary>() {

    override fun configure(settings: Settings, projekt: IProjekt): KotlinLibrary {
        val kotlinLibrary = KotlinLibrary(projekt, config)
        applyCommonConfiguration(settings, kotlinLibrary)
        return kotlinLibrary
    }
}
