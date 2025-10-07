package io.github.diskria.projektor.projekt

import io.github.diskria.projektor.projekt.common.AbstractProjekt
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

class AndroidLibrary(
    projekt: IProjekt,
    projectProvider: () -> Project
) : AbstractProjekt(
    projekt,
    projectProvider
), IProjekt by projekt
