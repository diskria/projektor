package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.configurations.AndroidLibraryConfiguration
import io.github.diskria.projektor.settings.projekt.common.IProjekt

open class AndroidLibrary(
    projekt: IProjekt,
    val config: AndroidLibraryConfiguration
) : IProjekt by projekt
