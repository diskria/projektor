package io.github.diskria.projektor.projekt

import io.github.diskria.projektor.projekt.common.IProjekt
import org.gradle.api.Project

data class AndroidApplication(private val projekt: IProjekt, private val project: Project) : IProjekt by projekt
