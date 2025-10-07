package io.github.diskria.projektor.projekt

import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

data class AndroidLibrary(private val projekt: IProjekt, private val project: Project) : IProjekt by projekt
