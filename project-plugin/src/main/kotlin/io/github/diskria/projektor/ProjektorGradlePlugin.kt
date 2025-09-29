package io.github.diskria.projektor

import io.github.diskria.gradle.utils.extensions.kotlin.registerExtension
import io.github.diskria.projektor.extensions.gradle.ProjektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjektorGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.registerExtension<ProjektExtension>()
    }
}
