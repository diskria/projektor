package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.projekt.common.AbstractProjekt
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

class KotlinLibrary(
    projekt: IProjekt,
    settingsProvider: () -> Settings
) : AbstractProjekt(
    projekt,
    settingsProvider
), IProjekt by projekt
