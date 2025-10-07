package io.github.diskria.projektor.projekt

import io.github.diskria.projektor.projekt.common.AbstractProjekt
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

open class AndroidLibrary(
    projekt: IProjekt,
    project: Project
) : AbstractProjekt(projekt, project), IProjekt by projekt
