package io.github.diskria.projektor.projekt

import io.github.diskria.projektor.configurations.AndroidLibraryConfiguration
import io.github.diskria.projektor.projekt.common.IProjekt

open class AndroidLibrary(projekt: IProjekt, val config: AndroidLibraryConfiguration) : IProjekt by projekt
