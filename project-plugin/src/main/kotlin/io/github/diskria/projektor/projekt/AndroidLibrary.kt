package io.github.diskria.projektor.projekt

import org.gradle.api.Project

open class AndroidLibrary(private val projekt: IProjekt, project: Project) : IProjekt by projekt {

}
