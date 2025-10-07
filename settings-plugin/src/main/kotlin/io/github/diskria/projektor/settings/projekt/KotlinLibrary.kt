package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

data class KotlinLibrary(private val projekt: IProjekt, private val settings: Settings) : IProjekt by projekt
