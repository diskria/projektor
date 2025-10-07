package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.projekt.common.AbstractProjekt
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

open class KotlinLibrary(
    projekt: IProjekt,
    settings: Settings
) : AbstractProjekt(projekt, settings), IProjekt by projekt
