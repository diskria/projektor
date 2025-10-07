package io.github.diskria.projektor.settings.projekt

import io.github.diskria.projektor.settings.projekt.common.AbstractProjekt
import io.github.diskria.projektor.settings.projekt.common.IProjekt
import org.gradle.api.initialization.Settings

class KotlinLibrary(projekt: IProjekt, val settings: Settings) : AbstractProjekt(projekt), IProjekt by projekt
