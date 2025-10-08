package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.configurations.KotlinLibraryConfiguration
import io.github.diskria.projektor.settings.projekt.common.IProjekt

open class KotlinLibrary(
    projekt: IProjekt,
    val config: KotlinLibraryConfiguration
) : IProjekt by projekt
