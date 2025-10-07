package io.github.diskria.projektor.projekt

import io.github.diskria.projektor.projekt.common.AbstractProjekt
import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

class AndroidApplication(projekt: IProjekt, val project: Project) : AbstractProjekt(projekt), IProjekt by projekt {

}
