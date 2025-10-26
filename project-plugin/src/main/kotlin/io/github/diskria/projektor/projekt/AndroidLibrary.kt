package io.github.diskria.projektor.projekt

import io.github.diskria.projektor.configurations.AndroidLibraryConfiguration
import io.github.diskria.projektor.projekt.common.AbstractProjekt
import io.github.diskria.projektor.projekt.common.Projekt

class AndroidLibrary(projekt: Projekt, val config: AndroidLibraryConfiguration) : AbstractProjekt(projekt) {

    override val publicationComponentName: String get() = "release"
}
